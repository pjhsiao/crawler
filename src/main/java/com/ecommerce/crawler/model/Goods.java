package com.ecommerce.crawler.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 商品物件
 */
@Data
@ToString
@Builder
public class Goods implements Serializable {
    private BigInteger seqNo;
    private String goodsName;
    private BigDecimal goodsPrice;
    private Boolean inStock;
}
