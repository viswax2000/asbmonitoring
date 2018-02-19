package com.vcc.asb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vcc.asb.configuration.RestTemplateFactory;
import com.vcc.asb.configuration.RestTemplatePool;
import com.vcc.asb.context.BatchType;

@EnableScheduling
@Import({RestTemplateFactory.class, RestTemplatePool.class})
@ComponentScan(basePackages={"com.vcc.asb.configuration", "com.vcc.asb.metrics.model", "com.vcc.asb.model", "com.vcc.asb.config.model",
							 "com.microsoft.windowsazure.services.servicebus.implementation", "com.vcc.asb.util", "com.vcc.asb.service",
							 "com.vcc.asb.metrics.command", "com.vcc.asb.context", "com.vcc.asb.jms"})
@RestController
@SpringBootApplication
public class BatchScheduler extends SpringBootServletInitializer {
	
	@Autowired
	BatchService batchService;
	
	private static Logger logger = LoggerFactory.getLogger(BatchScheduler.class);
	
	public static void main(String[] args) {
		SpringApplication.run(BatchScheduler.class, args);
	}
	
	@Override
	public SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(BatchScheduler.class);
	}
	
	@RequestMapping("reload")
	public void reloadAsbEntities() {
		logger.debug("***** Reloading the batch *****");
		synchronized(BatchScheduler.class) {
			batchService.appInitializer.initialize();
		}
	}
	
	@Scheduled(initialDelay=20000, fixedDelay=60000)
	public void triggerMetricsBatch() {
		logger.info("******** METRICS BATCH SERVICE TRIGGERED ******");
		synchronized(BatchScheduler.class) {
			batchService.executeBatch(BatchType.METRICS);
		}
	}
	
	@Scheduled(initialDelay=20000, fixedDelay=120000)
	public void triggerConfigBatch() {
		logger.info("******** CONFIG BATCH SERVICE TRIGGERED ******");
		synchronized(BatchScheduler.class) {
			batchService.executeBatch(BatchType.CONFIG);
		}
		
	}
	
	

}
