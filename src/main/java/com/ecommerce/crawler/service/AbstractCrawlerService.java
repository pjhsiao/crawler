package com.ecommerce.crawler.service;

import com.ecommerce.crawler.model.dto.CrawlerServiceDTO;
import lombok.Getter;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class AbstractCrawlerService {
    //goodName, inStock
    protected ConcurrentHashMap<String, Boolean> goodsMap;
    protected CrawlerServiceDTO crawlerServiceDTO;



    @PostConstruct
    public void init(){
        //coolpc using
        goodsMap = new ConcurrentHashMap<>();
        crawlerServiceDTO = CrawlerServiceDTO.builder().build();

    }
    public List<String> doCrawler(){
        crawler();
        parse();
        return crawlerServiceDTO.getEffectiveData();
    }

    public abstract void crawler();
    public abstract void parse();
}
