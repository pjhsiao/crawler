package com.ecommerce.crawler.service;

import com.ecommerce.crawler.model.resp.SinyaResp;
import com.ecommerce.crawler.model.vo.SinyaProdVO;
import com.ecommerce.crawler.model.vo.SinyaVO;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Service
@Slf4j
public class MomoCrawlerService extends AbstractCrawlerService implements ICrawlerService{
    @Getter
    private final String storeTitle = "【摸摸】";
    final String soldout_keyword = "forso";
    final String mobile_momo_url = "https://m.momoshop.com.tw/";
    final String mobile_momo_goods_url = "https://m.momoshop.com.tw/cateGoods.momo";

    @Resource
    RestTemplate restTemplate;

    BiFunction<Integer, ConcurrentMap<String, String>, Void> crawlerAllPage = (page, momoRecorderMap) ->{
        MultiValueMap<String, String>uriVars = new LinkedMultiValueMap<>();
        uriVars.add("cn","4302000169");
        uriVars.add("page", page.toString());
        uriVars.add("sortType", "3");
        uriVars.add("imgSH", "fourCardStyle");
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(mobile_momo_goods_url).queryParams(uriVars).build();
        log.info("uri :{}", uriComponents.toUriString());
        final String url = uriComponents.toUriString();

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
        log.debug("resp body:{}", resp.getBody());
        Document doc  = Jsoup.parse(resp.getBody());
        Elements elements = doc.select("a.productInfo");

        for(Element element: elements){
            if(!element.childNode(7).outerHtml().contains(soldout_keyword)){
                String prodName = element.attr("title");
                String prodPrice = element.select("b.price").text();
                String prodUrl  = element.attr("href");
                String fullInfo = prodPrice +"_"+ prodName;
                String fullUrl  = mobile_momo_url + prodUrl;
                log.info("{} {}", fullInfo, fullUrl);
                momoRecorderMap.put(fullInfo, fullUrl);
            }
        }
        return null;
    };

    @Override
    @SneakyThrows
    public void crawler() {
        ConcurrentMap<String, String> momoRecorderMap =  new ConcurrentHashMap<>();
        for(int page = 1 ; page < 6; page++){
            crawlerAllPage.apply(page, momoRecorderMap);
            Thread.sleep(1000l);
        }
        log.info("momoRecorderMap size: {}", momoRecorderMap.size());
        crawlerServiceDTO.setMomoCrawlerRecorderMap(momoRecorderMap);
        log.info("momoRecorderMap: {}", momoRecorderMap);
    }

    @Override
    public void parse() {
        Map<String, String>  recorderMap = crawlerServiceDTO.getMomoCrawlerRecorderMap();
        List<String> effectiveData = new CopyOnWriteArrayList<>();
        if(CollectionUtils.isEmpty(recorderMap)){
            log.error("crawlerRecorderMap is empty");
        }else{
            log.info("crawlerRecorderMap size: {}", recorderMap.size());
            //first parse raw data to goodsMap data
            recorderMap.forEach((key,value)->{
                if( null == goodsMap.putIfAbsent(key, true)){
                    effectiveData.add(String.format("%s %s /n", key, value));
                }
            });
        }
        crawlerServiceDTO.setEffectiveData(effectiveData);
        log.info("effectiveData size: {}", effectiveData.size());
        log.info("goodsMap size: {}", goodsMap.size());
    }


}
