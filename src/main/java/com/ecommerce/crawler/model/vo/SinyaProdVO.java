package com.ecommerce.crawler.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 {
 "barcode": "162575",
 "prod_id": "162575",
 "category": "0",
 "urls": null,
 "prod_name": "【整組價/組裝價】(LHR)華碩 DUAL-RTX3060-O12G-V2/Std:1837MHz/雙風扇/註冊四年保(長20公分)",
 "stockText": "【補貨中】",
 "price": "13,990",
 "discount": "",
 "prod_img": "Build_s.jpg",
 "state": null,
 "sortPrice": 13990,
 "stocks": "",
 "ec_prodFree": [],
 "prodFree": [],
 "addProds": [],
 "event": ""
 }
 */
@Data
@NoArgsConstructor
public class SinyaProdVO implements Serializable {
    @JsonProperty("prod_id")
    private String prod_id;
    @JsonProperty("prod_name")
    private String prod_name;
    @JsonProperty("stockText")
    private String stockText;
    @JsonProperty("sortPrice")
    private String sortPrice;
}
