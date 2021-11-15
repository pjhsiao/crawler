package com.ecommerce.crawler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PreDestroy;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {
	private ThreadPoolTaskExecutor executor;
	
	@Bean
	 public ThreadPoolTaskExecutor threadPool() {
		 executor = new ThreadPoolTaskExecutor();  
		 executor.setCorePoolSize(5);
		 executor.setMaxPoolSize(10);
		 executor.setQueueCapacity(5000);
         executor.setAllowCoreThreadTimeOut(true);
         executor.setThreadNamePrefix("ThreadPool-");
         executor.setKeepAliveSeconds(300);
         executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());  
         return executor;
     }
	@PreDestroy
	public void  destroy () {
		if(null != executor) {
			executor.shutdown();
		}
	}
}
