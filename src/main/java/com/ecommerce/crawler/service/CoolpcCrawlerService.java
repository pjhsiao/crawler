package com.ecommerce.crawler.service;

import com.ecommerce.crawler.model.Goods;
import com.ecommerce.crawler.model.dto.CrawlerServiceDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoolpcCrawlerService extends AbstractCrawlerService implements ICrawlerService{

    @Getter
    private final String StoreTitle = "【罰站屋】";
    private final String ValidKeyWord = "限組";

    @Resource
    RestTemplate restTemplate;

    @Override
    public void crawler() {
      String rawData =  restTemplate.getForObject("https://www.coolpc.com.tw/evaluate.php", String.class);
      log.debug("rawData:{}", rawData);
      Document doc = Jsoup.parse(rawData);
      List<Node> vgaGroupList = doc.select("TR").get(26).child(2).childNode(1).childNodes();
      List<Node> targetGroupList =  vgaGroupList.subList(14, vgaGroupList.size()-1);

      final Map<BigInteger, String> coolpcRecorderMap = new ConcurrentHashMap<>();

      Flux.fromStream(targetGroupList.parallelStream())
              .subscribe(node -> {
                  List<Node> itemNodes =  node.childNodes();
                  log.debug("{}", itemNodes);
                  for(Node resultNode: itemNodes){
                      if(0!=resultNode.siblingIndex()
                          && !resultNode.hasAttr("disabled")
                              && !resultNode.toString().trim().equals("")
                          ){
                          BigInteger goodsIdx = BigInteger.valueOf(Long.valueOf(resultNode.attributes().get("value")));
                          String goodsText = resultNode.childNode(0).outerHtml();
                          coolpcRecorderMap.put(goodsIdx, goodsText);
                          log.info("resultNode: {}", resultNode);
                      }
                  }
              });
        crawlerServiceDTO.setCrawlerRecorderMap(coolpcRecorderMap);
        log.info("coolpcRecorderMap: {}", coolpcRecorderMap);
    }

    @Override
    public void parse() {
      Map<BigInteger, String>  recorderMap = crawlerServiceDTO.getCrawlerRecorderMap();
      Predicate<String> isValid = v -> !StringUtils.contains(v, ValidKeyWord);
      List<String> effectiveData = new CopyOnWriteArrayList<>();

        if(CollectionUtils.isEmpty(recorderMap)){
            log.error("crawlerRecorderMap is empty");
        }else{
            log.info("crawlerRecorderMap size: {}", recorderMap.size());
                //first parse raw data to goodsMap data
                recorderMap.forEach((key,value)->{
                    if( null == goodsMap.putIfAbsent(value, isValid.test(value))){
                        if(isValid.test(value)){
                            effectiveData.add(value);
                        }
                    }
                });
        }
            crawlerServiceDTO.setEffectiveData(effectiveData);
            log.info("effectiveData size: {}", effectiveData.size());
            log.info("goodsMap size: {}", goodsMap.size());
        }
    }

