package com.ecommerce.crawler;

import com.ecommerce.crawler.messager.TelegramMessager;
import com.ecommerce.crawler.service.CoolpcCrawlerService;
import com.ecommerce.crawler.service.ICrawlerService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

//@SpringBootTest
//@RunWith(SpringRunner.class)
public class CrawlerApplicationTests {

    @Resource
    CoolpcCrawlerService coolpcCrawlerService;

    @Resource
    TelegramMessager messager;

//    @Test
//    void coolpc() {
//        List<String> resultData = coolpcCrawlerService.doCrawler();
//        StringBuffer sb = new StringBuffer(coolpcCrawlerService.getStoreTitle());
//        sb.append("\n");
//        resultData.forEach(item->{
//            sb.append(item).append("\n\n");
//        });
//        messager.send(sb.toString());
//    }


//    @Test
//    void load() {
//    }
}
