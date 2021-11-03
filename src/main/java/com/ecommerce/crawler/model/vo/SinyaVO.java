package com.ecommerce.crawler.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * {"group_id":"2211","title":"外接顯卡轉接盒(內裝顯卡)",
 * "prod_sub_slave_id":"5","link":"","prods":"","sort":""}
 */
@Data
@NoArgsConstructor
public class SinyaVO implements Serializable {
    @JsonProperty("group_id")
    private BigInteger group_id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("prod_sub_slave_id")
    private String prod_sub_slave_id;
    @JsonProperty("link")
    private String link;
    @JsonProperty("prods")
    private String prods;
    @JsonProperty("sort")
    private String sort;
}
