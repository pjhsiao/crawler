package com.ecommerce.crawler.service;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
public class MomoCrawlerService extends AbstractCrawlerService implements ICrawlerService{
    @Getter
    private final String storeTitle = "【摸摸】";
    final String mobile_momo_url = "https://m.momoshop.com.tw/";
    final String mobile_momo_goods_url = "https://m.momoshop.com.tw/cateGoods.momo";
    final String mobile_momo_search_url = "https://m.momoshop.com.tw/search.momo";

    final String momo_goodsDetail_url = "https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=";

    final String momo_TP_goodsDetail_url ="https://www.momoshop.com.tw/TP/%s/goodsDetail/%s";
    @Resource
    RestTemplate restTemplate;
    @Resource
    ThreadPoolTaskExecutor threadPool;

    BiFunction<MultiValueMap<String, String>, ConcurrentMap<String, String>, Void> crawlerSearch = (uriVars, recorderMap) ->{
        try {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(mobile_momo_search_url).queryParams(uriVars).build();
            log.debug("uri :{}", uriComponents.toUriString());
            String url = uriComponents.toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl(CacheControl.noCache());
            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            log.debug("resp body:{}", resp.getBody());
            Document doc  = Jsoup.parse(resp.getBody());
            Elements elements = doc.select("li.goodsItemLi");
            log.info("uri :{} element size:{}", uriComponents.toUriString(), elements.size());
            for(Element element: elements){
                if(!element.childNode(1).attr("value").equals("0")){
                    String prodName = element.childNode(11).attr("title");
                    String prodPrice = element.select("span.ec-current-price.price").text();
                    String goodscode  = element.childNode(11).attr("goodscode");
                    String fullUrl;
                    if(goodscode.contains("TP")){
                        String entpcode = element.childNode(11).attr("entpcode");
                        fullUrl  = String.format(momo_TP_goodsDetail_url, entpcode, goodscode); //https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=13663996
                    }else {
                        fullUrl  = momo_goodsDetail_url + goodscode; //https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=13663996
                    }
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
        final long fixedRateMill = 6000l;
        final ConcurrentMap<String, String> momoRecorderMap =  new ConcurrentHashMap<>();

        for(int page = 1 ; page < 3; page++){
            final int fixPage = page;
        //5070 Ti
        CompletableFuture<ConcurrentMap<String, String>> cf70Ti = CompletableFuture.supplyAsync(()->{
                                                                    MultiValueMap<String, String> rtx70Timap =  getSearchMap(String.valueOf(fixPage), "5070 Ti");
                                                                    ConcurrentMap<String, String> tempMap = new ConcurrentHashMap<>();
                                                                    crawlerSearch.apply(rtx70Timap, tempMap);
                                                                    return tempMap;
                                                                }).exceptionally(ex->{
                                                                    log.error( "在 cf70Ti 任務中發生異常: {}", ex.getMessage());
                                                                    return new ConcurrentHashMap<>();
                                                                });
        //search 5080
        CompletableFuture<ConcurrentMap<String, String>> cf80 = CompletableFuture.supplyAsync(()->{
                                                                    MultiValueMap<String, String> rtx80map =  getSearchMap(String.valueOf(fixPage), "5080");
                                                                    ConcurrentMap<String, String> tempMap = new ConcurrentHashMap<>();
                                                                    crawlerSearch.apply(rtx80map, tempMap);
                                                                    return tempMap;
                                                                }).exceptionally(ex->{
                                                                    log.error( "在 cf80 任務中發生異常: {}", ex.getMessage());
                                                                    return new ConcurrentHashMap<>();
                                                                });
        //  search 5090
        CompletableFuture<ConcurrentMap<String, String>> cf90 = CompletableFuture.supplyAsync(()->{
                                                                    MultiValueMap<String, String> rtx90map =  getSearchMap(String.valueOf(fixPage), "5090");
                                                                    ConcurrentMap<String, String> tempMap = new ConcurrentHashMap<>();
                                                                    crawlerSearch.apply(rtx90map, tempMap);
                                                                    return tempMap;
                                                                }).exceptionally(ex->{
                                                                    log.error( "在 cf90 任務中發生異常: {}", ex.getMessage());
                                                                    return new ConcurrentHashMap<>();
                                                                });
        //9800x3d
        CompletableFuture<ConcurrentMap<String, String>> cfAMDx3d = CompletableFuture.supplyAsync(()->{
                                                                    MultiValueMap<String, String> rtxADMx3dmap =  getSearchMap(String.valueOf(fixPage),"9800x3d");
                                                                    ConcurrentMap<String, String> tempMap = new ConcurrentHashMap<>();
                                                                    crawlerSearch.apply(rtxADMx3dmap, tempMap);
                                                                    return tempMap;
                                                                }).exceptionally(ex->{
                                                                    log.error( "在 cfAMDx3d 任務中發生異常: {}", ex.getMessage());
                                                                    return new ConcurrentHashMap<>();
                                                                });
            momoRecorderMap.putAll(cf70Ti.get());
            momoRecorderMap.putAll(cf80.get());
            momoRecorderMap.putAll(cf90.get());
            momoRecorderMap.putAll(cfAMDx3d.get());
            Thread.sleep(fixedRateMill);
        }

        log.info("momoRecorderMap size: {}", momoRecorderMap.size());
        crawlerServiceDTO.setMomoCrawlerRecorderMap(momoRecorderMap);
        log.debug("momoRecorderMap: {}", momoRecorderMap);
    }


    private MultiValueMap<String, String> getSearchMap(String page, String keyword){
        MultiValueMap<String, String> varsMap = new LinkedMultiValueMap<>();
        varsMap.add("searchKeyword", keyword);
        varsMap.add("curPage", page);
        varsMap.add("cateCode", "1200200000");
        varsMap.add("maxPage", "3");
        varsMap.add("minPage", "1");
        return varsMap;
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
                    log.info("{} {}", key, value);
                }
            });
        }
        crawlerServiceDTO.setEffectiveData(effectiveData);
        log.info("effectiveData size: {}", effectiveData.size());
        log.info("goodsMap size: {}", goodsMap.size());
    }
}


