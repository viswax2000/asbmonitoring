package com.vcc.asb.configuration;

import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class RestTemplatePool {
	
	/*@Autowired
	RestTemplateFactory restTemplateFactory;*/
	
	@Bean
	@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RestTemplateMgmtAPI getRestTemplateForMgmtAPI() {
		return new RestTemplateMgmtAPI();
	}
	
	@Bean
	@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RestTemplateServiceBusAPI getRestTemplateForServiceBusAPI() {
		return new RestTemplateServiceBusAPI();
	}
	
	@Bean
	@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RestTemplateMonitorAPI getRestTemplateForMonitorAPI() {
		return new RestTemplateMonitorAPI();
	}
	
	@Bean
	@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RestTemplateAADAPI getRestTemplateForAADAPI() {
		return new RestTemplateAADAPI();
	}	
	
	
	
	public static class RestTemplateMgmtAPI extends CommonsPool2TargetSource  {
		RestTemplateMgmtAPI() {
			super();
			setMinIdle(10);
			setTargetBeanName("restTemplateMgmtAPI");
		}
	}
	
	public static class RestTemplateServiceBusAPI extends CommonsPool2TargetSource {
		RestTemplateServiceBusAPI() {
			super();
			setMinIdle(10);
			setTargetBeanName("restTemplateServiceBusAPI");
		}
	}
	
	public static class RestTemplateMonitorAPI extends CommonsPool2TargetSource {
		RestTemplateMonitorAPI() {
			super();
			setMinIdle(10);
			setTargetBeanName("restTemplateMonitorAPI");
		}
	}
	
	public static class RestTemplateAADAPI extends CommonsPool2TargetSource {
		RestTemplateAADAPI() {
			super();
			setMinIdle(10);
			setTargetBeanName("restTemplateAADAPI");
		}
	}
	
	/*RestTemplatePool() {
		super();
		setMinIdle(5);
		//restTemplateFactory = new RestTemplateFactory();
		setTargetBeanName("restTemplateMgmtAPI");
	}*/

}
