package org.example.service.impl;

import org.example.dao.PromoDOMapper;
import org.example.dataobject.PromoDO;
import org.example.error.BusinessException;
import org.example.error.EmBusinessError;
import org.example.service.ItemService;
import org.example.service.PromoService;
import org.example.service.UserService;
import org.example.service.model.ItemModel;
import org.example.service.model.PromoModel;
import org.example.dao.PromoDOMapper;
import org.example.service.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author manster
 * @Date 2021/5/27
 **/
@Service
public class PromoServiceImpl implements PromoService {
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);
        //entity->model
        PromoModel promoModel = convertFromEntity(promoDO);
        if(promoModel == null){
            return null;
        }

        //判断当前时间活动是否即将开始或正在进行
        if(promoModel.getStartDate().isAfterNow()){
            //活动未开始
            promoModel.setStatus(1);
        }else if (promoModel.getEndDate().isBeforeNow()){
            //活动已结束
            promoModel.setStatus(3);
        }else {
            //正在进行中
            promoModel.setStatus(2);
        }

        return promoModel;
    }




//    @Override
//    public String generateSecondKillToken(Integer promoId,Integer itemId,Integer userId) {
//
//        //判断是否库存已售罄，若对应的售罄key存在，则直接返回下单失败
//        if(redisTemplate.hasKey("promo_item_stock_invalid_"+itemId)){
//            return null;
//        }
//        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
//        //dataobject->model
//        PromoModel promoModel = convertFromDataObject(promoDO);
//        if(promoModel == null){
//            return null;
//        }
//        //判断当前时间是否秒杀活动即将开始或正在进行
//        if(promoModel.getStartDate().isAfterNow()){
//            promoModel.setStatus(1);
//        }else if(promoModel.getEndDate().isBeforeNow()){
//            promoModel.setStatus(3);
//        }else{
//            promoModel.setStatus(2);
//        }
//        //判断活动是否正在进行
//        if(promoModel.getStatus().intValue() != 2){
//            return null;
//        }
//        //判断item信息是否存在
//        ItemModel itemModel = itemService.getItemByIdInCache(itemId);
//        if(itemModel == null){
//            return null;
//        }
//        //判断用户信息是否存在
//        UserModel userModel = userService.getUserByIdInCache(userId);
//        if(userModel == null){
//            return null;
//        }
//        //获取秒杀大闸的count数量
//        long result = redisTemplate.opsForValue().increment("promo_door_count_"+promoId,-1);
//        if(result < 0){
//            return null;
//        }
//        //生成token并且存入redis内并给一个5分钟的有效期
//        String token = UUID.randomUUID().toString().replace("-","");
//        redisTemplate.opsForValue().set("promo_token_"+promoId+"_userid_"+userId+"_itemid_"+itemId,token);
//        redisTemplate.expire("promo_token_"+promoId+"_userid_"+userId+"_itemid_"+itemId,5, TimeUnit.MINUTES);
//        return token;
//    }



    /***
     * 根据id获取活动
     * @param promoId
     */
    @Override
    public void publishPromo(Integer promoId) {
        //通过活动id获取活动
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if(promoDO.getItemId() == null || promoDO.getItemId().intValue() == 0){
            return;
        }
        ItemModel itemModel = itemService.getItemById(promoDO.getItemId());
        //将库存同步到redis内
        redisTemplate.opsForValue().set("promo_item_stock_"+itemModel.getId(), itemModel.getStock());
        //将大闸的限制数字设到redis内
        redisTemplate.opsForValue().set("promo_door_count_"+promoId,itemModel.getStock().intValue() * 5);

    }


    @Override
    public String generateSecondKillToken(Integer promoId,Integer itemId,Integer userId) {
        if(redisTemplate.hasKey("promo_item_stock_invalid_)"+itemId)){
            return null;
        }

        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        //entity->model
        PromoModel promoModel = convertFromEntity(promoDO);
        if(promoModel == null){
            return null;
        }

        //判断当前时间活动是否即将开始或正在进行
        if(promoModel.getStartDate().isAfterNow()){
            //活动未开始
            promoModel.setStatus(1);
        }else if (promoModel.getEndDate().isBeforeNow()){
            //活动已结束
            promoModel.setStatus(3);
        }else {
            //正在进行中
            promoModel.setStatus(2);
        }

        //判断活动是否正在进行
        if(promoModel.getStatus().intValue()!=2){
            return null;
        }

        //判断item信息是否存在
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null){
            return null;
        }

        UserModel userModel = userService.getUserById(userId);
        if(userModel == null){
            return null;
        }
        //获取秒杀大闸的count数量
        long result = redisTemplate.opsForValue().increment("promo_door_count_"+promoId,-1);
        if(result < 0){
            return null;
        }
        //生成token存入redis内，并给一个5分钟的有效期
        String token= UUID.randomUUID().toString().replace("-","");

        redisTemplate.opsForValue().set("promo_token_"+promoId+"_userid_"+userId+"_itemid_"+itemId,token);
        redisTemplate.expire("promo_token_"+promoId+"_userid_"+userId+"_itemid_"+itemId,5, TimeUnit.MINUTES);

        return token;

    }

    //这里的方法命名和~里有点不一样
    private PromoModel convertFromEntity(PromoDO promoDO){
        if(promoDO == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        //这里的写法有点不一样，但是感觉应该是没问题的
        promoModel.setPromoItemPrice(BigDecimal.valueOf(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }

}
