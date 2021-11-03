package com.ecommerce.crawler;

import com.ecommerce.crawler.messager.TelegramMessager;
import com.ecommerce.crawler.model.resp.SinyaResp;
import com.ecommerce.crawler.model.vo.SinyaProdVO;
import com.ecommerce.crawler.service.CoolpcCrawlerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class CrawlerApplicationTests {

    @Resource
    CoolpcCrawlerService coolpcCrawlerService;

    @Resource
    TelegramMessager messager;

    @Resource
    RestTemplate restTemplate;

//    @Test
//    @SneakyThrows
//    public void sinya() {
//        final String option_url = "https://www.sinya.com.tw/diy/show_option";
//        final String prods_url = "https://www.sinya.com.tw/diy/api_prods";
//
//        final String ValidKeyWord = "組裝價";
//        final String Prod_sub_slave_id = "5";
//
//        final String[] filter_keyword = new String[]{"RX", "RTX", "GTX"};
//
//        Predicate<String> filter_product  = title -> {
//            return StringUtils.containsAny(title, filter_keyword);
//        };
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
//        map.add("id", "5");
//
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
//        ResponseEntity<SinyaResp> resp = restTemplate.exchange(option_url, HttpMethod.POST, entity, SinyaResp.class);
//
//        log.info("rawData:{}", resp.getBody());
//
//        CopyOnWriteArrayList<BigInteger> group_ids = new CopyOnWriteArrayList<>();
//        resp.getBody().getShowOption()
//                      .stream().parallel()
//                      .filter(vo -> filter_product.test(vo.getTitle()))
//                      .forEach(vo -> group_ids.add(vo.getGroup_id()));
//
//
//        HttpHeaders headers2 = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> map2= new LinkedMultiValueMap<>();
//        map.add("item[group_id]", "2230");
//        map.add("item[prod_sub_slave_id]", "5");
//        HttpEntity<MultiValueMap<String, String>> entity2 = new HttpEntity<>(map, headers);
//        ResponseEntity<SinyaProdVO[]> resp2 = restTemplate.exchange(prods_url, HttpMethod.POST, entity, SinyaProdVO[].class);
//
//        log.info("result : {}", resp2);
//    }
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
//    public void load() {
//    }
}
