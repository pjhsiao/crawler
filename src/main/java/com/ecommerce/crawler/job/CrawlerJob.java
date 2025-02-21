package com.ecommerce.crawler.job;

import com.ecommerce.crawler.messager.TelegramMessager;
import com.ecommerce.crawler.service.CoolpcCrawlerService;
import com.ecommerce.crawler.service.MomoCrawlerService;
import com.ecommerce.crawler.service.PCHomeCrawlerService;
import com.ecommerce.crawler.service.SinyaCrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    MomoCrawlerService momoCrawlerService;

    @Resource
    PCHomeCrawlerService pcHomeCrawlerService;

    @Resource
    TelegramMessager messager;

    private boolean momoFirstLoop = true;

    private boolean sinyaFirstLoop = true;

    private boolean coolpcFirstLoop = true;

    private boolean pchomeFirstLoop = true;

//    @Scheduled(cron = "0 05 15 * * MON-FRI")
//    public void rebuild_stock() {
//    	log.info("Rebuild Cache for daily job");
//    	Flux.fromStream(caches.stream())
//	    	.doOnNext(c->c.destoryCache())
//	    	.doOnNext(c->c.initCache())
//	    	.subscribe();
//    }

//    @Scheduled(initialDelay=1000, fixedRate=50000)
    public void momoJob() {
        log.info("crawler momo for fixed time");
        List<String> resultData = momoCrawlerService.doCrawler();
        //do not send message at first time
        if(!resultData.isEmpty() && !momoFirstLoop){
            resultData.forEach(item->{
                try {
                    messager.send(String.format("%s\n%s",momoCrawlerService.getStoreTitle(), item));
                    log.info("has been sent :{}", item);
                    Thread.sleep(3000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        momoFirstLoop =false;
    }
    @Async
    @Scheduled(initialDelay=1000, fixedRate=10000)
    public void pchomeJob() {
        log.info("crawler pchome for fixed time");
        List<String> resultData = pcHomeCrawlerService.doCrawler();
        //do not send message at first time
        if(!resultData.isEmpty() && !pchomeFirstLoop){
            resultData.forEach(item->{
                try {
                    messager.send(String.format("%s\n%s",pcHomeCrawlerService.getStoreTitle(), item));
                    log.info("has been sent :{}", item);
                    Thread.sleep(3000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        pchomeFirstLoop =false;
    }

//    @Scheduled(initialDelay=6000, fixedRate=10000)
//    public void sinyaJob() {
//        log.info("crawler sinya for fixed time");
//        List<String> resultData = sinyaCrawlerService.doCrawler();
//        if(!resultData.isEmpty() && !sinyaFirstLoop ){
//            StringBuffer sb = new StringBuffer(sinyaCrawlerService.getStoreTitle());
//            sb.append("\n");
//            resultData.forEach(item->{
//                sb.append(item).append("\n");
//            });
//            messager.send(sb.toString());
//        }
//        sinyaFirstLoop = false;
//    }
//
//    @Scheduled(initialDelay=1000, fixedRate=10000)
//    public void coolpcJob() {
//    	log.info("crawler coolpc for fixed time");
//        List<String> resultData = coolpcCrawlerService.doCrawler();
//        if(!resultData.isEmpty() && !coolpcFirstLoop ){
//            StringBuffer sb = new StringBuffer(coolpcCrawlerService.getStoreTitle());
//            sb.append("\n");
//            resultData.forEach(item->{
//                sb.append(item).append("\n\n");
//            });
//            messager.send(sb.toString());
//        }
//        coolpcFirstLoop = false;
//    }
}
