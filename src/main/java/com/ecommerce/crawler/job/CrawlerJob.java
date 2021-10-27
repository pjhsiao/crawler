package com.ecommerce.crawler.job;

import com.ecommerce.crawler.messager.TelegramMessager;
import com.ecommerce.crawler.service.CoolpcCrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

/**
 * @Class: CrawlerJob.java
 * @apiNote
 * 0 0 16 * * MON-FRI
 */
@Component
@Slf4j
public class CrawlerJob {

    @Resource
    CoolpcCrawlerService coolpcCrawlerService;

    @Resource
    TelegramMessager messager;

//    @Scheduled(cron = "0 05 15 * * MON-FRI")
//    public void rebuild_stock() {
//    	log.info("Rebuild Cache for daily job");
//    	Flux.fromStream(caches.stream())
//	    	.doOnNext(c->c.destoryCache())
//	    	.doOnNext(c->c.initCache())
//	    	.subscribe();
//    }
 
//    @Scheduled(initialDelay=1000, fixedRate=1000)
//    public void sample() {
//    	log.info("sample job for fixed time");
//    }


    @Scheduled(initialDelay=1000, fixedRate=30000)
    public void coolpcJob() {
    	log.info("crawler coolpc for fixed time");
        List<String> resultData = coolpcCrawlerService.doCrawler();
        if(!resultData.isEmpty()){
            StringBuffer sb = new StringBuffer(coolpcCrawlerService.getStoreTitle());
            sb.append("\n");
            resultData.forEach(item->{
                sb.append(item).append("\n\n");
            });
            messager.send(sb.toString());
        }
    }
}
