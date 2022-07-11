package org.example.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.mysql.jdbc.StringUtils;
import org.example.error.BusinessException;
import org.example.error.EmBusinessError;
import org.example.mq.MqProducer;
import org.example.response.CommonReturnType;
import org.example.service.ItemService;
import org.example.service.OrderService;
import org.example.service.PromoService;
import org.example.service.model.OrderModel;
import org.example.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Executor;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = {"*"},allowCredentials = "true" )
public class OrderController extends BaseController {
    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PromoService promoService;

    private RateLimiter orderCreateRateLimiter;

    private ExecutorService executorService;

    /***
     * 同步调用线程池的submit方法
     * 拥塞窗口为20的等待队列，实现队列泄洪
     * 限流
     */
    @PostConstruct
    public void init(){
        executorService = Executors.newFixedThreadPool(2000);
        orderCreateRateLimiter = RateLimiter.create(30000);
    }

    //生成秒杀令牌
    @RequestMapping(value = "/generatetoken",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody     //这里刚开始名字取错了，写出createItem了，后来又改的，不知道有没有影响。
    public CommonReturnType generatetoken(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "promoId")Integer promoId) throws BusinessException {
        //根据token获取用户信息
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isNullOrEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "请登录后再下单");
        }
        //获取用户登录信息
        UserModel userModel=(UserModel) redisTemplate.opsForValue().get(token);
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "请登录后再下单");
        }
        //获取秒杀访问令牌
        String promoToken= promoService.generateSecondKillToken(promoId,itemId,userModel.getId());

        if(promoToken==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"生成令牌失败");
        }
        return CommonReturnType.create((promoToken));
    }

    @RequestMapping(value = "/createOrder",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                               @RequestParam(name = "amount")Integer amount,
                               @RequestParam(name = "promoId",required = false)Integer promoId,
                               @RequestParam(name = "promoToken",required = false)String promoToken) throws BusinessException {
        if(!orderCreateRateLimiter.tryAcquire()){
            throw new BusinessException(EmBusinessError.RATELIMIT);
        }


        //判断是否登录
        //Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isNullOrEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "请登录后再下单");
        }
        UserModel userModel=(UserModel) redisTemplate.opsForValue().get(token);
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "请登录后再下单");
        }
//        if(isLogin == null || !isLogin){
//            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "请登录后再下单");
//        }




        //校验秒杀令牌是否正确
        if(promoId != null){
            String inRedisPromoToken = (String) redisTemplate.opsForValue().get("promo_token_"+promoId+"_userid_"+userModel.getId()+"_itemid_"+itemId);
            if(inRedisPromoToken == null){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀令牌校验失败");
            }
            if(!org.apache.commons.lang3.StringUtils.equals(promoToken,inRedisPromoToken)){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀令牌校验失败");
            }
        }



        //获取用户的登录信息
        //UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        //判断库存是否售罄，如果售罄的key存在(也就是has)，则直接返回下单失败
        //Boolean k=redisTemplate.hasKey("promo_item_stock_invalid_)"+itemId);


        //同步调用线程池的submit方法
        //拥塞窗口为20的等待队列，用来队列化泄洪
        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                //加入库存流水init状态
                String stockLogId = itemService.initStockLog(itemId,amount);
                //再去完成对应的下单事务型消息机制，在这里面执行了createOrder的方法
                if(!mqProducer.transactionAsyncReduceStock(userModel.getId(),itemId,promoId,amount,stockLogId)){
                    throw new BusinessException(EmBusinessError.UNKNOW_ERROR,"下单失败");
                }
                return null;
            }
        });
        try {
            future.get();
        } catch (InterruptedException e) {
            throw new BusinessException(EmBusinessError.UNKNOW_ERROR);
        } catch (ExecutionException e) {
            throw new BusinessException(EmBusinessError.UNKNOW_ERROR);
        }


        return CommonReturnType.create(null);
    }
}
