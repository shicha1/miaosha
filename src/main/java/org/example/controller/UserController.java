package org.example.controller;

import com.alibaba.druid.util.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.example.controller.viewobject.UserVO;
import org.example.error.BusinessException;
import org.example.error.EmBusinessError;
import org.example.response.CommonReturnType;
import org.example.service.UserService;
import org.example.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.apache.commons.codec.binary.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(origins = {"*"},allowCredentials = "true" )
public class UserController extends BaseController{



    @Autowired  //通过bean做注入
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    //用户获取otp短信的接口
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public  CommonReturnType getOtp(@RequestParam(name="telphone")String telphone){
        //需要按照一定规则生成OTP验证嘛
        Random random=new Random();
        int randomInt=random.nextInt(99999);
        randomInt+=10000;
        String otpCode=String.valueOf(randomInt);

        //将OTP验证码和用户对应的手机号关联,使用httpsession的方式
        httpServletRequest.getSession().setAttribute(telphone,otpCode);

        //将OTP验证码通过短信通道发送给用户（此项目忽略此步骤）
        System.out.println("telephone="+telphone+"&otpCode"+otpCode);
        return CommonReturnType.create(null);
    }





    //用户注册接口
    @RequestMapping(value = "/register", method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody                        //这里接收的都是前端传过来的参数
    public CommonReturnType register(@RequestParam(name = "telphone")String telphone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name = "age")Integer age,
                                     @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpcode是否相符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        if(!StringUtils.equals(otpCode, inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }

        //用户注册
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender)));
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(EncodeByMd5(password));

        userService.register(userModel);

        return CommonReturnType.create(null);
    }


    //用户登录接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telphone) ||
                org.apache.commons.lang3.StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "请登录后下单");
        }

        //用户登录服务
        UserModel userModel = userService.vaildateLogin(telphone, EncodeByMd5(password));

        //若登陆成功，则将登录凭证加入到用户登录成功的session内

        //修改成若登录成功则将对应的登录信息和登录凭证一起存入redis中
        //生成登录凭证token，UUID
        String uuidToken= UUID.randomUUID().toString();
        uuidToken=uuidToken.replace("-","");

        //建立token和用户登录态之间的联系
        redisTemplate.opsForValue().set(uuidToken,userModel);
        redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);

        //this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        //this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        //return CommonReturnType.create(null);
        return CommonReturnType.create(uuidToken);
    }





    public String EncodeByMd5(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64 base64Encoder = new Base64();
        String newstr = base64Encoder.encodeToString(md5.digest(str.getBytes("utf-8")));

        //BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        //String newstr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }






    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        //实际上不需要全部返回给前端，至少密码不应该返回，所以应该再加上一个viewobject层
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        //将完整的用户对象转化为可供UI使用的viewobject
        UserVO userVO = convertFromModel(userModel);
        //返回通用对象,可以看到这里只有一个参数，所以对应的是success的情况
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return  userVO;
    }


}
