package com.vcc.asb.configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletContext;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2CollectionHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Configuration
public class RestTemplateFactory {
	
	@Value("${keystore.path}")
	private String keyStorePath;
	
	@Value("${truststore.path}")
	private String trustStorePath;
	
	@Value("${keystore.password}")
	private String keyStorePassword;
	
	@Value("${key.password}")
	private String keyPassword;
	
	@Value("${truststore.password}")
	private String trustStorePassword;
	
	private static Logger logger = LoggerFactory.getLogger(RestTemplateFactory.class);
	
	@Autowired
	ServletContext context;
	
	public RestTemplateFactory() {
	}
	
	@Bean("restTemplateMgmtAPI")
	@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RestTemplate getRestTemplateForMgmtAPI() {
		
		logger.debug("Getting the RestTemplate @ MgmtAPI .. starting");
		RequestConfig.Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(60000);
		RequestConfig config = builder.build();
		
		HttpClient httpClient = HttpClients.custom().setSSLContext(getSSLContext()).setDefaultRequestConfig(config).build();
		ClientHttpRequestFactory httpFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		
		RestTemplate restTemplate = new RestTemplate(httpFactory);
		restTemplate.setMessageConverters(getHttpMessageConverters().getConverters());
		
		logger.debug("Returning the RestTemplate @ MgmtAPI");
		return restTemplate;
	}
	
	@Bean("restTemplateServiceBusAPI")
	@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RestTemplate getRestTemplateForServiceBusAPI() {

		logger.debug("Getting the RestTemplate @ ServiceBusAPI");
		
		RequestConfig.Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(60000);
		RequestConfig config = builder.build();
		
		HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
		ClientHttpRequestFactory httpFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		
		RestTemplate restTemplate = new RestTemplate(httpFactory);
		restTemplate.setMessageConverters(getHttpMessageConverters().getConverters());
		
		logger.debug("Returning the RestTemplate @ ServiceBusAPI");
		
		return restTemplate;
	}
	
	@Bean("restTemplateMonitorAPI")
	@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RestTemplate getRestTemplateForMonitorAPI() {
		
		logger.debug("Getting the RestTemplate @ MonitorAPI");

		RequestConfig.Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(60000);
		RequestConfig config = builder.build();
		
		HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
		ClientHttpRequestFactory httpFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		
		RestTemplate restTemplate = new RestTemplate(httpFactory);
		restTemplate.setMessageConverters(getHttpMessageConverters().getConverters());
		
		logger.debug("Returning the RestTemplate @ MonitorAPI");
		
		return restTemplate;
	}
	
	@Bean("restTemplateAADAPI")
	@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RestTemplate getRestTemplateForAADAPI() {

		logger.debug("Getting the RestTemplate @ AADAPI");

		RequestConfig.Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(60000);
		RequestConfig config = builder.build();
		
		HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
		ClientHttpRequestFactory httpFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		
		RestTemplate restTemplate = new RestTemplate(httpFactory);
		restTemplate.setMessageConverters(getHttpMessageConverters().getConverters());
		
		logger.debug("Returning the RestTemplate @ AADAPI");
		
		return restTemplate;
	}	
	
	private SSLContext getSSLContext() {
		
		//System.out.println("KeyStorePath>>>>>>>>>>>>>>>>"+keyStorePath);
		//System.out.println("TrustStorePath>>>>>>>>>>>>>>>>"+trustStorePath);
		logger.debug("Getting the SSLContext, to Configure the RestTemplate @ MgmtAPI");
		
		URL url = null;
		try {
			url = context.getResource("/WEB-INF/classes/"+keyStorePath);
			logger.debug("********** FilePath:"+url.getPath());
		} catch(MalformedURLException urlE) {
			logger.error("URL Exception While getting Keystore Path:", urlE);
		}
		
		File keyStoreFile = new File(url.getPath());
		logger.info("******* Keystore File Exists:"+keyStoreFile.exists());

		File trustStoreFile = new File(trustStorePath);
		SSLContext sslContext = null;

		try {
			TrustStrategy trustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;			//we will trust all the certs from the server
				}
			};
			sslContext = SSLContextBuilder.create()
										  .loadKeyMaterial(keyStoreFile, keyStorePassword.toCharArray(), keyPassword.toCharArray())
										  .loadTrustMaterial(trustStrategy).build();
		} catch(Exception e) {
			logger.error("Got Exception while getting the SSLContext to Configure The RestTemplate @ MgmtAPI", e);
			throw new RuntimeException(e);
		}
		logger.debug("Returning the SSLContext, to Configure the RestTemplate @ MgmtAPI");
		
		return sslContext;
	}
	
	private HttpMessageConverters getHttpMessageConverters() {
		
		List<HttpMessageConverter<?>> httpConverters = new ArrayList<HttpMessageConverter<?>>() {
			{
				add(new MappingJackson2HttpMessageConverter());
				add(new Jaxb2RootElementHttpMessageConverter());
				add(new Jaxb2CollectionHttpMessageConverter());
				add(new MappingJackson2XmlHttpMessageConverter());
				add(new MarshallingHttpMessageConverter());
				//add(new JsonbHttpMessageConverter());
				add(new StringHttpMessageConverter());
				add(new SourceHttpMessageConverter());
			}
		};
		
		HttpMessageConverters converters = new HttpMessageConverters(httpConverters);
		
		return converters;
	}
}
