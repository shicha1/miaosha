package org.example.service;

import org.example.service.model.PromoModel;

public interface PromoService {

    //根据商品id获取即将进行以及正在进行的活动信息
    PromoModel getPromoByItemId(Integer itemId);

    //
//    String generateSecondKillToken(Integer promoId,Integer itemId,Integer userId);
    /***
     * 活动发布
     * @param promoId
     */
    void publishPromo(Integer promoId);

    /***
     * 生成秒杀用的令牌
     * @param promoId
     * @param itemId
     * @param userId
     * @return
     */
    String generateSecondKillToken(Integer promoId, Integer itemId, Integer userId);
}
