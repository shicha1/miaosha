package org.example.controller;

import org.example.error.BusinessException;
import org.example.error.EmBusinessError;
import org.example.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

//如果说异常连controller层都没进，那么这里的异常处理也就不起作用了，所以可以优化一下，用globalexceptionhandler来替代它
public class BaseController {

    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";

    //定义exceptionhander解决未被controller层吸收的异常
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public Object handlerException(HttpServletRequest request, Exception ex){
//        Map<String,Object> responseData=new HashMap<>();
//        if(ex instanceof BusinessException){
//            BusinessException businessException=(BusinessException)ex;
//            responseData.put("errCode",businessException.getErrCode());
//            responseData.put("errMsg",businessException.getErrMsg());
//        }else{
//            responseData.put("errCode", EmBusinessError.UNKNOW_ERROR.getErrCode());
//            responseData.put("errMsg",EmBusinessError.UNKNOW_ERROR.getErrMsg());
//        }
//        return CommonReturnType.create(responseData,"fail");
//    }
}
