package com.ecommerce.crawler.service;

import com.ecommerce.crawler.model.resp.SinyaResp;
import com.ecommerce.crawler.model.vo.SinyaProdVO;
import com.ecommerce.crawler.model.vo.SinyaVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


@Service
@Slf4j
public class SinyaCrawlerService extends AbstractCrawlerService implements ICrawlerService{
    @Getter
    private final String storeTitle = "【綠牛店】";
    private final String validKeyWord = "組裝價";
    private final String prod_sub_slave_id = "5";
    private final String option_url = "https://www.sinya.com.tw/diy/show_option";
    private final String prods_url = "https://www.sinya.com.tw/diy/api_prods";
    private final String stockText = "補貨中";
    private final String[] filter_keyword = new String[]{"RX", "RTX", "GTX"};

    @Resource
    RestTemplate restTemplate;

    @Override
    public void crawler() {

        //1. Get ShowOption
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("id", prod_sub_slave_id);

        ResponseEntity<SinyaResp> showOptionResponseEntity =  doPostFunc.apply(map, SinyaResp.class);
        log.debug("ShowOptionResponseEntity: {}", showOptionResponseEntity);

        if(HttpStatus.NO_CONTENT.value() == showOptionResponseEntity.getStatusCode().value()){
            throw new RuntimeException("ShowOption Get NO_CONTENT");
        }

        CopyOnWriteArrayList<BigInteger> group_ids = new CopyOnWriteArrayList<>();
        List<SinyaVO> showOptions = showOptionResponseEntity.getBody().getShowOption();
        log.info("showOptions size: {}", showOptions.size());

        showOptions.stream()
                   .filter(vo -> filter_product.test(vo.getTitle()))
                   .forEach(vo -> group_ids.add(vo.getGroup_id()));
        log.info("group_ids size: {}", group_ids.size());

        //2. Get
        final Map<BigInteger, String> sinyaRecorderMap = new ConcurrentHashMap<>();
        group_ids.parallelStream().forEach(id->{
            log.info("{} : {} ", Thread.currentThread(), id);
            MultiValueMap<String, String> map2= new LinkedMultiValueMap<>();
            map2.add("item[group_id]", id.toString());
            map2.add("item[prod_sub_slave_id]", prod_sub_slave_id);
            ResponseEntity<SinyaProdVO[]> prodsResponseEntity =  doPostFunc.apply(map2, SinyaProdVO[].class);
            SinyaProdVO[] sinyaProdVOs = prodsResponseEntity.getBody();
            log.info("sinyaProdVOs id:{} size: {}", id, sinyaProdVOs.length);
            Stream.of(sinyaProdVOs)
                    .filter(prod->!prod.getStockText().contains(stockText))
                    .filter(prod->!prod.getProd_name().contains(validKeyWord))
                    .forEach(prod-> {
                        log.info("prod: {}", prod);
                        BigInteger prod_id = BigInteger.valueOf(Long.parseLong(prod.getProd_id()));
                        sinyaRecorderMap.put(prod_id, prod.getSortPrice()+"_"+prod.getProd_name());
                    });
        });
        log.info("sinyaRecorderMap size: {}", sinyaRecorderMap.size());
        crawlerServiceDTO.setCrawlerRecorderMap(sinyaRecorderMap);
        log.info("sinyaRecorderMap: {}", sinyaRecorderMap);
    }

    @Override
    public void parse() {
        Map<BigInteger, String>  recorderMap = crawlerServiceDTO.getCrawlerRecorderMap();
        List<String> effectiveData = new CopyOnWriteArrayList<>();
        if(CollectionUtils.isEmpty(recorderMap)){
            log.error("crawlerRecorderMap is empty");
        }else{
            log.info("crawlerRecorderMap size: {}", recorderMap.size());
            //first parse raw data to goodsMap data
            recorderMap.forEach((key,value)->{
                if( null == goodsMap.putIfAbsent(value, true)){
                    effectiveData.add(value);
                }
            });
        }
        crawlerServiceDTO.setEffectiveData(effectiveData);
        log.info("effectiveData size: {}", effectiveData.size());
        log.info("goodsMap size: {}", goodsMap.size());
    }

     BiFunction<MultiValueMap, Class, ResponseEntity> doPostFunc = (map, cls) ->{
        HttpHeaders headers = new HttpHeaders();
        String url = map.size()==1 ? option_url: prods_url;
        ResponseEntity responseEntity = ResponseEntity.noContent().build();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, cls);
        return responseEntity;
    };

    Predicate<String> filter_product  = title -> {
        return StringUtils.containsAny(title, filter_keyword);
    };

}
