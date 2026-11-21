package com.dimbox.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dimbox.product.entity.ProductSku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    @Update("UPDATE t_product_sku SET stock = stock - #{quantity} " +
            "WHERE id = #{skuId} AND stock >= #{quantity} AND deleted = 0")
    int deductStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 回补库存，订单取消或下单失败时使用。
     */
    @Update("UPDATE t_product_sku SET stock = stock + #{quantity} " +
            "WHERE id = #{skuId} AND deleted = 0")
    int restoreStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Update("UPDATE t_product_sku SET stock = stock + #{quantity} " +
            "WHERE id = #{skuId} AND deleted = 0")
    int increaseStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);
}
