package org.example.dao;

import org.apache.ibatis.annotations.Param;
import org.example.dataobject.ItemDO;

import java.util.List;

public interface ItemDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    int insert(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    int insertSelective(ItemDO record);

    //查询全部item
    List<ItemDO> listItem();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    ItemDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    int updateByPrimaryKeySelective(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    int updateByPrimaryKey(ItemDO record);

    int increaseSales(@Param("id")Integer id, @Param("amount")Integer amount);
}