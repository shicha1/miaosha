package org.example.service;

import org.example.error.BusinessException;
import org.example.service.model.ItemModel;

import java.util.List;

/**
 * @Author manster
 * @Date 2021/5/24
 **/
public interface ItemService {

    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //商品列表浏览
    List<ItemModel> listItem();

    //item及promo model缓存模型
    ItemModel getItemByIdInCache(Integer id);


    //商品详情浏览
    ItemModel getItemById(Integer id);
    /***
     * 库存回补
     * @param itemId
     * @param amount
     * @return
     * @throws BusinessException
     */
    boolean increaseStock(Integer itemId, Integer amount)throws BusinessException;
    //库存扣减
    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;
    /***
     * 异步更新库存
     * @param itemId
     * @param amount
     * @return
     */
    boolean asyncDecreaseStock(Integer itemId, Integer amount);

    //销量增加
    void increaseSales(Integer itemId, Integer amount) throws BusinessException;
    /***
     * 初始化库存流水
     * @param itemId
     * @param amount
     * @return
     */
    String initStockLog(Integer itemId, Integer amount);
}
