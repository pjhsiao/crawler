package com.ecommerce.crawler.job;

import com.ecommerce.crawler.messager.TelegramMessager;
import com.ecommerce.crawler.service.CoolpcCrawlerService;
import com.ecommerce.crawler.service.SinyaCrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

/**
 * @Class: CrawlerJob.java
 * @apiNote
 */
@Component
@Slf4j
public class CrawlerJob {

    @Resource
    CoolpcCrawlerService coolpcCrawlerService;

    @Resource
    SinyaCrawlerService sinyaCrawlerService;

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

    @Scheduled(initialDelay=5000, fixedRate=30000)
    public void sinyaJob() {
        log.info("crawler sinya for fixed time");
        List<String> resultData = sinyaCrawlerService.doCrawler();
        if(!resultData.isEmpty()){
            StringBuffer sb = new StringBuffer(sinyaCrawlerService.getStoreTitle());
            sb.append("\n");
            resultData.forEach(item->{
                sb.append(item).append("\n\n");
            });
            messager.send(sb.toString());
        }
    }

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
