package com.ecommerce.crawler.service;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

@Service
@Slf4j
public class PCHomeCrawlerService extends AbstractCrawlerService implements ICrawlerService{
    @Getter
    private final String storeTitle = "【電腦家】";

    final String pchome_url = "https://24h.pchome.com.tw";

    final String pchome_search_url = "https://24h.pchome.com.tw/search/";

    @Resource
    RestTemplate restTemplate;

    BiFunction<MultiValueMap<String, String>, ConcurrentMap<String, String>, Void> crawlerSearch = (uriVars, recorderMap) ->{
        try {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(pchome_search_url).queryParams(uriVars).build();
            log.debug("uri :{}", uriComponents.toUriString());
            String url = uriComponents.toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl(CacheControl.noCache());
            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            log.debug("resp body:{}", resp.getBody());
            Document doc  = Jsoup.parse(resp.getBody());
            Elements elements = doc.select("li.c-listInfoGrid__item");
            log.info("uri :{} element size:{}", uriComponents.toUriString(), elements.size());
            for(Element element: elements){
                List<Element> soldOutElements = element.select("span.c-label__notice");
                if(soldOutElements.isEmpty()){ //on prod must be empty
                    String prodName = element.select("div.c-prodInfoV2__title").text();
                    String prodPrice = element.select("div.c-prodInfoV2__price").text();
                    String goodHref  = element.select("a.c-prodInfoV2__link").attr("href");
                    String fullUrl = pchome_url + goodHref; //https://24h.pchome.com.tw/prod/DRADTJ-1900IAR3R
                    String fullInfo = prodPrice +"_"+ prodName;
                    log.debug("{} {}", fullInfo, fullUrl);
                    recorderMap.put(fullInfo, fullUrl);
                }
            }
        }catch (Exception e){
            log.error("error: {}", e.getMessage());
        }
        return null;
    };

    @Override
    @SneakyThrows
    public void crawler() {
        log.debug("do {} crawler", storeTitle);
        final long fixedRateMill = 3000l;
        final ConcurrentMap<String, String> pchomeRecorderMap =  new ConcurrentHashMap<>();

        CompletableFuture<ConcurrentMap<String, String>> cf80 = CompletableFuture.supplyAsync(()->{
                                                                    MultiValueMap<String, String> rtx80map =  getSearchMap("5080");
                                                                    ConcurrentMap<String, String> tempMap = new ConcurrentHashMap<>();
                                                                    crawlerSearch.apply(rtx80map, tempMap);
                                                                    return tempMap;
                                                                }).exceptionally(ex->{
                                                                    log.error( "在 cf80 任務中發生異常: {}", ex.getMessage());
                                                                    return new ConcurrentHashMap<>();
                                                                });

        CompletableFuture<ConcurrentMap<String, String>> cf90 = CompletableFuture.supplyAsync(()->{
                                                                    MultiValueMap<String, String> rtx90map =  getSearchMap("5090");
                                                                    ConcurrentMap<String, String> tempMap = new ConcurrentHashMap<>();
                                                                    crawlerSearch.apply(rtx90map, tempMap);
                                                                    return tempMap;
                                                                }).exceptionally(ex->{
                                                                    log.error( "在 cf90 任務中發生異常: {}", ex.getMessage());
                                                                    return new ConcurrentHashMap<>();
                                                                });
        CompletableFuture<ConcurrentMap<String, String>> cfAMDx3d = CompletableFuture.supplyAsync(()->{
                                                                    MultiValueMap<String, String> rtxADMx3dmap =  getSearchMap("9800x3d");
                                                                    ConcurrentMap<String, String> tempMap = new ConcurrentHashMap<>();
                                                                    crawlerSearch.apply(rtxADMx3dmap, tempMap);
                                                                    return tempMap;
                                                                }).exceptionally(ex->{
                                                                    log.error( "在 cfAMDx3d 任務中發生異常: {}", ex.getMessage());
                                                                    return new ConcurrentHashMap<>();
                                                                });

        pchomeRecorderMap.putAll(cf80.get());
        Thread.sleep(fixedRateMill);
        pchomeRecorderMap.putAll(cf90.get());
        Thread.sleep(fixedRateMill);
        pchomeRecorderMap.putAll(cfAMDx3d.get());
        Thread.sleep(fixedRateMill);
        log.info("pchomeRecorderMap size: {}", pchomeRecorderMap.size());
        crawlerServiceDTO.setPchomeCrawlerRecorderMap(pchomeRecorderMap);
        log.debug("end {} crawler", storeTitle);
    }

    private MultiValueMap<String, String> getSearchMap(String keyword){
        MultiValueMap<String, String> varsMap = new LinkedMultiValueMap<>();
        varsMap.add("q", keyword);
        varsMap.add("pCate", "103004000000000");
        return varsMap;
    }

    @Override
    public void parse() {
        log.debug("do {} parse", storeTitle);
        Map<String, String> recorderMap = crawlerServiceDTO.getPchomeCrawlerRecorderMap();
        List<String> effectiveData = new CopyOnWriteArrayList<>();
        if(CollectionUtils.isEmpty(recorderMap)){
            log.error("crawlerRecorderMap is empty");
        }else{
            log.info("crawlerRecorderMap size: {}", recorderMap.size());
            //first parse raw data to goodsMap data
            recorderMap.forEach((key,value)->{
                if( null == goodsMap.putIfAbsent(key, true)){
                    effectiveData.add(String.format("%s %s /n", key, value));
                    log.info("{} {}", key, value);
                }
            });
        }
        crawlerServiceDTO.setEffectiveData(effectiveData);
        log.info("effectiveData size: {}", effectiveData.size());
        log.info("goodsMap size: {}", goodsMap.size());
        log.debug("end {} parse", storeTitle);
    }
}
