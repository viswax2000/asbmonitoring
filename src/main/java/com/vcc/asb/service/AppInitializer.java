package com.vcc.asb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.management.Azure;
import com.vcc.asb.configuration.ServiceConfig;
import com.vcc.asb.context.AppContext;
import com.vcc.asb.model.ASBEntities;

/**
 * Initializes the ApplicationContext - loads the ASB model objects
 * from Azure Service, creates different data structures based on these ASB
 * model objects.   
 * 
 * @author volvo-it
 *
 */
@Component
public class AppInitializer {
	
	@Autowired
	MetricsService metricsService;
	
	@Autowired
	ServiceConfig serviceConfig;
	
	private static Logger logger = LoggerFactory.getLogger(AppInitializer.class);
	
	public AppInitializer() {
		
	}

	//@PostConstruct
	public void initialize() {
		logger.info("Initializing AppInitializer");
		Azure azureService = serviceConfig.getAzureService();
		ASBEntities asbEntities = new ASBEntities();
		logger.debug("Populating ASBEntities Namespaces with AzureService");
		asbEntities.populateNamespaces(azureService, serviceConfig);
		AppContext.setAsbEntities(asbEntities);
	}

	public void refreshAllContexts() {
		logger.info("AppInitializer Refresh All Contexts, Calling ServiceConfig.init()");
		serviceConfig.init();
		Azure azureService = serviceConfig.getAzureService();
		ASBEntities asbEntities = new ASBEntities();
		logger.debug("Populating ASBEntities Namespaces with AzureService");
		asbEntities.populateNamespaces(azureService, serviceConfig);
		AppContext.setAsbEntities(asbEntities);
		
	}

}
