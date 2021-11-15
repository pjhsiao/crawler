package com.ecommerce.crawler.service;

import lombok.*;
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
import java.util.concurrent.*;
import java.util.function.BiFunction;

//https://m.momoshop.com.tw/cateGoods.momo?cn=4302000169&page=1&sortType=5&imgSH=fourCardStyle
//https://m.momoshop.com.tw/cateGoods.momo?cn=4302000216&page=1&sortType=4&imgSH=fourCardType
//https://m.momoshop.com.tw/search.momo?searchKeyword=3070&couponSeq=&searchType=2&cateLevel=NaN&curPage=1&cateCode=1200200000&cateName=%E9%9B%BB%E8%85%A6%E7%B5%84%E4%BB%B6%2822%29&maxPage=2&minPage=1&_advCp=N&_advFirst=N&_advFreeze=N&_advSuperstore=N&_advTvShop=N&_advTomorrow=N&_advNAM=N&_advStock=N&_advPrefere=N&_advThreeHours=N&_brandNoList=&ent=b&_imgSH=fourCardType&specialGoodsType=&_isFuzzy=0

@Service
@Slf4j
public class MomoCrawlerService extends AbstractCrawlerService implements ICrawlerService{
    @Getter
    private final String storeTitle = "【摸摸】";
    final String soldout_keyword = "forso";
    final String mobile_momo_url = "https://m.momoshop.com.tw/";
    final String mobile_momo_goods_url = "https://m.momoshop.com.tw/cateGoods.momo";
    final String mobile_momo_search_url = "https://m.momoshop.com.tw/search.momo";
    @Resource
    RestTemplate restTemplate;
    @Resource
    ThreadPoolTaskExecutor threadPool;

    BiFunction<MultiValueMap<String, String>, ConcurrentMap<String, String>, Void> crawlerSearch = (uriVars, recorderMap) ->{
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(mobile_momo_search_url).queryParams(uriVars).build();
        log.info("uri :{}", uriComponents.toUriString());
        final String url = uriComponents.toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache());
        HttpEntity httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        log.debug("resp body:{}", resp.getBody());
        Document doc  = Jsoup.parse(resp.getBody());
        Elements elements = doc.select("li.goodsItemLi");

        for(Element element: elements){
            if(!element.childNode(1).attr("value").equals("0")){
                String prodName = element.childNode(9).attr("title");
                String prodPrice = element.select("b.price").text();
                String prodUrl  = element.childNode(9).attr("href");
                String fullInfo = prodPrice +"_"+ prodName;
                String fullUrl  = mobile_momo_url + prodUrl;
                log.debug("{} {}", fullInfo, fullUrl);
                recorderMap.put(fullInfo, fullUrl);
            }
        }
        return null;
    };

    BiFunction<MultiValueMap<String, String>, ConcurrentMap<String, String>, Void> crawlerCateGoods = (uriVars, momoRecorderMap) ->{
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(mobile_momo_goods_url).queryParams(uriVars).build();
        log.info("uri :{}", uriComponents.toUriString());
        final String url = uriComponents.toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache());
        HttpEntity httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        log.debug("resp body:{}", resp.getBody());
        Document doc  = Jsoup.parse(resp.getBody());
        Elements elements = doc.select("a.productInfo");
        for(Element element: elements){
            if(!element.childNode(7).outerHtml().contains(soldout_keyword)){
                String prodName = element.attr("title");
                String prodPrice = element.select("b.price").text().replaceAll(",","");
                String prodUrl  = element.attr("href");
                String fullInfo = prodPrice +"_"+ prodName;
                String fullUrl  = mobile_momo_url + prodUrl;
                log.debug("{} {}", fullInfo, fullUrl);
                momoRecorderMap.put(fullInfo, fullUrl);
            }
        }
        return null;
    };

    @Override
    @SneakyThrows
    public void crawler() {
        final long fixedRateMill = 300l;
        final ConcurrentMap<String, String> momoRecorderMap =  new ConcurrentHashMap<>();
        for(int page = 1 ; page < 5; page++){
            final int fixPage = page;
            CompletableFuture<ConcurrentMap<String, String>> cf4302000169 = CompletableFuture.supplyAsync(()->{
                                    MultiValueMap<String, String>uriVars = new LinkedMultiValueMap<>();
                                    uriVars.add("cn","4302000169");
                                    uriVars.add("page", String.valueOf(fixPage));
                                    uriVars.add("sortType", "5");
                                    uriVars.add("imgSH", "fourCardStyle");
                                    ConcurrentMap<String, String> recordMap = new ConcurrentHashMap<>();
                                    crawlerCateGoods.apply(uriVars, recordMap);
                                    return recordMap;
            });

            CompletableFuture<ConcurrentMap<String, String>> cf4302000216 = CompletableFuture.supplyAsync(()->{
                MultiValueMap<String, String>uriVars2 = new LinkedMultiValueMap<>();
                            uriVars2.add("cn","4302000216");
                            uriVars2.add("page", String.valueOf(fixPage));
                            uriVars2.add("sortType", "4");
                            uriVars2.add("imgSH", "fourCardStyle");
                            ConcurrentMap<String, String> recordMap = new ConcurrentHashMap<>();
                            crawlerCateGoods.apply(uriVars2, recordMap);
                            return recordMap;
            });

            momoRecorderMap.putAll(cf4302000169.get());
            momoRecorderMap.putAll(cf4302000216.get());
            Thread.sleep(fixedRateMill);
        }

        for(int page = 1 ; page < 4; page++){
            final int fixPage = page;
      //search 3060
      CompletableFuture<ConcurrentMap<String, String>> cf3060 = CompletableFuture.supplyAsync(()->{
                MultiValueMap<String, String> rtx3060map =  getSearchMap(String.valueOf(fixPage), "3060");
                ConcurrentMap<String, String> recordMap = new ConcurrentHashMap<>();
                crawlerSearch.apply(rtx3060map,  recordMap);
                return recordMap;
            });

      //  search 3070
        CompletableFuture<ConcurrentMap<String, String>> cf3070 = CompletableFuture.supplyAsync(()->{
            MultiValueMap<String, String> rtx3070map =  getSearchMap(String.valueOf(fixPage), "3070");
            ConcurrentMap<String, String> tempMap = new ConcurrentHashMap<>();
            crawlerSearch.apply(rtx3070map, tempMap);
            return tempMap;
        });
//            Thread.sleep(fixedRateMill);
        //search 3080
        CompletableFuture<ConcurrentMap<String, String>> cf3080 = CompletableFuture.supplyAsync(()->{
            MultiValueMap<String, String> rtx3080map =  getSearchMap(String.valueOf(fixPage), "3080");
            ConcurrentMap<String, String> tempMap = new ConcurrentHashMap<>();
            crawlerSearch.apply(rtx3080map, tempMap);
            return tempMap;
        });
        //  search 3090
        CompletableFuture<ConcurrentMap<String, String>> cf3090 = CompletableFuture.supplyAsync(()->{
            MultiValueMap<String, String> rtx3090map =  getSearchMap(String.valueOf(fixPage), "3090");
            ConcurrentMap<String, String> tempMap = new ConcurrentHashMap<>();
            crawlerSearch.apply(rtx3090map, tempMap);
            return tempMap;
        });
            momoRecorderMap.putAll(cf3060.get());
            momoRecorderMap.putAll(cf3070.get());
            momoRecorderMap.putAll(cf3080.get());
            momoRecorderMap.putAll(cf3090.get());
        }

        log.info("momoRecorderMap size: {}", momoRecorderMap.size());
        crawlerServiceDTO.setMomoCrawlerRecorderMap(momoRecorderMap);
        log.debug("momoRecorderMap: {}", momoRecorderMap);
    }


    private MultiValueMap<String, String> getSearchMap(String page, String keyword){
        MultiValueMap<String, String> varsMap = new LinkedMultiValueMap<>();
        varsMap.add("searchKeyword", keyword);
        varsMap.add("searchType", "2");
        varsMap.add("cateLevel", "NaN");
        varsMap.add("curPage", page);
        varsMap.add("cateCode", "1200200000");
        varsMap.add("cateName", "電腦組件");
        varsMap.add("maxPage", "2");
        varsMap.add("minPage", "1");
        varsMap.add("_advCp", "N");
        varsMap.add("_advFirst", "N");
        varsMap.add("_advFreeze", "N");
        varsMap.add("_advSuperstore", "N");
        varsMap.add("_advTvShop", "N");
        varsMap.add("_advTomorrow", "N");
        varsMap.add("_advNAM", "N");
        varsMap.add("_advStock", "N");
        varsMap.add("_advPrefere", "N");
        varsMap.add("_advThreeHours", "N");
        varsMap.add("ent", "b");
        varsMap.add("_imgSH", "fourCardType");
        varsMap.add("_isFuzzy", "0");
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
                }
            });
        }
        crawlerServiceDTO.setEffectiveData(effectiveData);
        log.info("effectiveData size: {}", effectiveData.size());
        log.info("goodsMap size: {}", goodsMap.size());
    }


}


