package com.ecommerce.crawler.messager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Component
@Slf4j
public class TelegramMessager {

	@Value("${telegram.apiUrl}")
	private String apiUrl;
	@Value("${telegram.apiToken}")
	private String apiToken;
	@Value("${telegram.chatId}")
	private String chatId;

	private String messageUrlPattern = "%s/bot%s/sendMessage?chat_id=%s&text=%s";
	private String documentUrlPattern = "%s/bot%s/sendDocument";
	@Resource
    private RestTemplate restTemplate;
	
	public void send(String text) {
		  String fullUrl = String.format(messageUrlPattern, apiUrl, apiToken, chatId, text);
		  restTemplate.exchange(fullUrl, HttpMethod.GET, RequestEntity.EMPTY, String.class);
		  log.info("telegram bot sent message: {}", text);
	}
	
	public void sendWithChatId(String text, String otherChatId) {
		  String fullUrl = String.format(messageUrlPattern, apiUrl, apiToken, otherChatId, text);
		  restTemplate.exchange(fullUrl, HttpMethod.GET, RequestEntity.EMPTY, String.class);
		  log.info("telegram bot sent message: {}", text);
	}

	public void sendDocument(ByteArrayResource fileAsResource, String otherChatId){

		String fullUrl = String.format(documentUrlPattern, apiUrl, apiToken);

		MultiValueMap<String,Object> multipartBody = new LinkedMultiValueMap<>();
		HttpHeaders headers = new HttpHeaders();

		HttpHeaders fileHeader = new HttpHeaders();
		fileHeader.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<ByteArrayResource> filePart = new HttpEntity<>(fileAsResource,fileHeader);
		multipartBody.set("document",filePart);

		HttpHeaders txtHeader = new HttpHeaders();
		txtHeader.setContentType(MediaType.TEXT_HTML);
		HttpEntity<String> txtPart = new HttpEntity<>(otherChatId, txtHeader);
		multipartBody.set("chat_id",txtPart);

		HttpEntity<MultiValueMap<String, Object>> requestEntity   = new HttpEntity<>(multipartBody, headers);
		restTemplate.postForEntity(fullUrl, requestEntity, String.class);
	}
}
