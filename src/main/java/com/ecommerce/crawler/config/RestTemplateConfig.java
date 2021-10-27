package com.ecommerce.crawler.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.util.Collections;

@Configuration
public class RestTemplateConfig {
	@Value("${restTemplate.connTimeout}")
	private int connTimeout;
	@Value("${restTemplate.readTimeout}")
	private int readTimeout;
	@Value("${restTemplate.connReqTimeout}")
	private int connReqTimeout;
	@Value("${restTemplate.connPoolSize}")
	private int connPoolSize;
	@Value("${restTemplate.maxPerRoute}")
	private int maxPerRoute;
	
	@Bean
    public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
		restTemplate.getMessageConverters().add(messageConverter());
        return restTemplate;
    }

	@Bean
	public MappingJackson2HttpMessageConverter messageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
		return converter;
	}
	
	
    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public HttpClient httpClient() {
    	 try {
    		 TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
             SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
             SSLConnectionSocketFactory sslConnectionSocketFactory =
                     new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
             
             Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                     .register("http", PlainConnectionSocketFactory.getSocketFactory())
                     .register("https", sslConnectionSocketFactory) //redefine SSLConnectionSocketFactory to by pass unable to find valid certification
                     .build();
         	
         	PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

             connectionManager.setMaxTotal(connPoolSize);
             connectionManager.setDefaultMaxPerRoute(maxPerRoute);
             RequestConfig requestConfig = RequestConfig.custom()
                     .setSocketTimeout(readTimeout) //read(response) timeout
                     .setConnectTimeout(connTimeout)//connect timeout
                     .setConnectionRequestTimeout(connReqTimeout)//  waiting for connection from pool
                     .build();
             return HttpClientBuilder.create()
             		.setRedirectStrategy(LaxRedirectStrategy.INSTANCE)
                     .setDefaultRequestConfig(requestConfig)
                     .setConnectionManager(connectionManager)
                     .build();
    	 }catch(Exception e) {
    		 throw new RuntimeException("Init HttpsRestTemplate fail", e);
    	 }
    }
	
}
