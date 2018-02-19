package com.vcc.asb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.vcc.asb.configuration.RestTemplateFactory;
import com.vcc.asb.configuration.RestTemplatePool;
import com.vcc.asb.configuration.ServiceConfig;

@SpringBootConfiguration
@Import({RestTemplateFactory.class, RestTemplatePool.class})
@ComponentScan(basePackages={"com.vcc.asb.configuration", "com.vcc.asb.metrics.model", "com.vcc.asb.model", "com.vcc.asb.config.model",
							 "com.microsoft.windowsazure.services.servicebus.implementation", "com.vcc.asb.util", "com.vcc.asb.service",
							 "com.vcc.asb.metrics.command", "com.vcc.asb.context"})
@RunWith(SpringRunner.class)
@SpringBootTest
public class AsbMonitoringApplicationTests {

	@Autowired
	ServiceConfig serviceConfig;
	
	@Test
	public void testGetOAuth2AccessToken() {
		
		serviceConfig.getOAuth2AccessToken("5c784763-f700-41ad-88a5-479fe3717bf1", "d205b97e-6e78-45eb-af5a-feeefecd147b", 
				"8bGbqTv/ktAXwFdtsFZw/5JSUay03eJABER+qsTfDf4=");
		
	}

}
