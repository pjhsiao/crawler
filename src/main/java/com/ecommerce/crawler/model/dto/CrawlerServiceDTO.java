package com.ecommerce.crawler.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class CrawlerServiceDTO {
    private String rawData;
    private Map<BigInteger, String> crawlerRecorderMap;
    //effectiveData will be sand finally
    private List<String> effectiveData;
}
