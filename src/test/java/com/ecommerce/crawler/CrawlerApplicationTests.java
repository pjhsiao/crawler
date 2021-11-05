package com.ecommerce.crawler;

import com.ecommerce.crawler.messager.TelegramMessager;
import com.ecommerce.crawler.service.CoolpcCrawlerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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
//    public void momo() {
//        final String soldout_keyword = "forso";
//        final String mobile_momo_url = "https://m.momoshop.com.tw/";
//        final String mobile_momo_goods_url = "https://m.momoshop.com.tw/cateGoods.momo";
//        final String lineSeparator = System.getProperty("line.separator");
//        final MultiValueMap<String, String> uriVars = new LinkedMultiValueMap<>();
//        uriVars.add("cn","4302000169");
//        uriVars.add("page", "1");
//        uriVars.add("sortType", "3");
//        uriVars.add("imgSH", "fourCardStyle");
//
//        UriComponents uriComponents = UriComponentsBuilder.fromUriString(mobile_momo_goods_url).queryParams(uriVars).build();
//        log.info("uri :{}", uriComponents.toUriString());
//        final String url = uriComponents.toUriString();
//
//        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
//        log.debug("resp body:{}", resp.getBody());
//        Document doc  = Jsoup.parse(resp.getBody());
//        Elements elements = doc.select("a.productInfo");
//
//        StringBuffer sb = new StringBuffer();
//        for(Element element: elements){
//            if(!element.childNode(7).outerHtml().contains(soldout_keyword)){
//                String prodName = element.attr("title");
//                String prodPrice = element.select("b.price").text();
//                String prodUrl  = element.attr("href");
//                String fullInfo = prodPrice +"_"+ prodName;
//                String fullUrl  = mobile_momo_url + prodUrl;
//                log.info("{} {}", fullInfo, fullUrl);
//                messager.send(String.format("%s %s /n", fullInfo, fullUrl));
//            }
//        }
//    }

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
