package org.example.dataobject;

public class ItemStockDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column item_stock.id
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column item_stock.stock
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    private Integer stock;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column item_stock.item_id
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    private Integer itemId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column item_stock.id
     *
     * @return the value of item_stock.id
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column item_stock.id
     *
     * @param id the value for item_stock.id
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column item_stock.stock
     *
     * @return the value of item_stock.stock
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column item_stock.stock
     *
     * @param stock the value for item_stock.stock
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column item_stock.item_id
     *
     * @return the value of item_stock.item_id
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column item_stock.item_id
     *
     * @param itemId the value for item_stock.item_id
     *
     * @mbg.generated Sat Jun 12 19:35:13 CST 2021
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}