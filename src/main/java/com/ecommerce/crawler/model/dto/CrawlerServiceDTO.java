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
    //for momo
    private Map<String, String> momoCrawlerRecorderMap;
    //for pchome
    private Map<String, String> pchomeCrawlerRecorderMap;
    //effectiveData will be sand finally
    private List<String> effectiveData;
}
