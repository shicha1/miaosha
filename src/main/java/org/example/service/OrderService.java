package org.example.service;

import org.example.error.BusinessException;
import org.example.service.model.OrderModel;

/**
 * @Author manster
 * @Date 2021/5/26
 **/
public interface OrderService {
    //两种方案：第一种方案更好一些
    //1.通过前端url传秒杀活动id,然后下单接口内校验对应id是否属于对应的商品且活动已开始
    //2.直接在下单页面接口内判断对应商品是否存在秒杀活动，如存在就以秒杀价格下单
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount,String stockLogId) throws BusinessException;

    String generateOrderNo();

}
