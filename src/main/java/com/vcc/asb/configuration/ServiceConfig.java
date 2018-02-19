package com.vcc.asb.configuration;

import static com.vcc.asb.configuration.ConfigConstants.AAD_RESOURCE_URI;
import static com.vcc.asb.configuration.ConfigConstants.AAD_TOKEN_ENDPOINT_2;
import static com.vcc.asb.configuration.ConfigConstants.AMPERSAND;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.vcc.asb.context.AppContext;
import com.vcc.asb.jms.JmsConfigSender;
import com.vcc.asb.jms.JmsMetricsSender;
import com.vcc.asb.jms.JmsSender;
import com.vcc.asb.model.GrantType;
import com.vcc.asb.model.OAuth2Token;
import com.vcc.asb.util.ConfigFormatter;

import ch.qos.logback.classic.LoggerContext;

@Component
public class ServiceConfig {
	
	@Value("${default.subscription.id}")
	private String subscriptionId;
	
	@Value("${default.resourcegroup.name}")
	private String resourceGroupName;
	
	@Value("${default.namespace.name}")
	private String namespaceName;

	@Value("${default.tenant.id}")
	private String tenantId;

	@Value("${default.client.id}")
	private String clientId;

	@Value("${default.client.secret}")
	private String clientSecret;
	
	@Value("${default.metrics.timespan}")
	private String metricsTimespan;
	
	@Value("${credfile.path}")
	private String credFilePath;
	
	@Value("${batch.metrics.frequency")
	private String batchMetricsFrequency;
	
	@Value("${monitormetrics.timegrain}")
	private String monitorMetricsTimegrain;
	
	@Value("${message.size}")
	private long messageSize;

	@Autowired
	RestTemplatePool.RestTemplateMgmtAPI restTemplateMgmtAPIPool;

	@Autowired
	RestTemplatePool.RestTemplateServiceBusAPI restTemplateSBAPIPool;

	@Autowired
	RestTemplatePool.RestTemplateMonitorAPI restTemplateMonitorAPIPool;
	
	@Autowired
	RestTemplatePool.RestTemplateAADAPI restTemplateAADAPIPool;
	
	@Autowired
	ConfigFormatter configFormatter;
	
	@Autowired
	JmsConfigSender jmsConfigSender;
	
	@Autowired
	JmsMetricsSender jmsMetricsSender;
	
	Azure azureService;
	
	private static Logger logger = LoggerFactory.getLogger(ServiceConfig.class);
	
	@PostConstruct
	public void init() {
		try {
			logger.info(" ***** Initializing the ServiceConfig Service ****** ");
			//File credFile = new File(credFilePath);
			
			ApplicationTokenCredentials credentials = new ApplicationTokenCredentials(clientId, tenantId, clientSecret, null);
			this.azureService = Azure.configure().authenticate(credentials).withDefaultSubscription();

			initAppContext();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}			
	}

	/**
	 * Getting the AAD OAuth2 AccessToken for the resourceUri and ServicePrincipal 
	 * 
	 * @param tenatId
	 * @param clntId
	 * @param clntSecret
	 * @return
	 */
	public String getOAuth2AccessToken(String tenatId, String clntId, String clntSecret) {

		logger.debug("ServiceConfig:getOAuth2AccessToken - start:tenantId:"+tenatId+",ClientId:"+clntId+", clntSecret:"+clntSecret);
		
		RestTemplate rt = null;
		try {
			rt = (RestTemplate) restTemplateAADAPIPool.getTarget();
		} catch(Exception e) {
			logger.error("GOt Exception while retrieving the RestTemplate from AADAPI Pool", e);
			throw new RuntimeException(e);
		}

		//rt = new RestTemplate();
		
		String clSecret = getEncodedString(clntSecret);
		UriTemplate template = new UriTemplate(AAD_TOKEN_ENDPOINT_2);
		URI tokenEndpointUri = template.expand(tenatId);
		String resorceUri = getEncodedString(AAD_RESOURCE_URI);
		String accessToken = null;

		try {

			StringBuilder sb = new StringBuilder();
			sb.append("grant_type=").append(GrantType.CLIENT_CREDENTIALS.getGrantType()).append(AMPERSAND)
			  .append("client_id=").append(clntId).append(AMPERSAND)
			  .append("resource=").append(resorceUri).append(AMPERSAND)
			  .append("client_secret=").append(clSecret);
			
			//System.out.println("Token Endpoint Request URI:"+tokenEndpointUri);
			
			BodyBuilder bodyBuilder = RequestEntity.post(tokenEndpointUri);
			
			for(Entry entry: ConfigConstants.AAD_TOKEN_HEADERS.entrySet()) {
				bodyBuilder.header((String)entry.getKey(), (String)entry.getValue());
			}
			
			RequestEntity<String> requestEntity = bodyBuilder.body(sb.toString());
			
			ResponseEntity<OAuth2Token> responseEntity = rt.exchange(requestEntity, OAuth2Token.class);
			//ResponseEntity<String> responseEntity = rt.exchange(requestEntity, String.class);
			
			//System.out.println("Response :"+responseEntity.getBody());
			//System.out.println("Status Code:"+responseEntity.getStatusCodeValue()+","+responseEntity.getStatusCode());
			
			OAuth2Token tokenObject = responseEntity.getBody();

			logger.debug("TokenType:"+tokenObject.getTokenType()+", ResourceUri:"+tokenObject.getResourceUri()+
					"Access Token:"+tokenObject.getAccessToken()+", tokenObject:"+tokenObject);
			
			accessToken = tokenObject.getAccessToken();
			
			try {
				if(rt != null) {
					restTemplateAADAPIPool.releaseTarget(rt);
				} 
			} catch(Exception e) {
				logger.error("Caught Exception while releasing the RestTemplate to AADAPIPool", e);
				throw new RuntimeException(e);
			}			
		
		} catch(Exception e) {
			logger.error("Caught Exception while Fetching OAuth2 AAD AccessToken", e);
			throw new RuntimeException(e);
		}
		
		return accessToken;
		
	}
	
	/**
	 * The SAS Token is just a combination of url-encoded strings of resourceUri, KeyName, and KeyValue
	 * 
	 * @param resourceUri
	 * @param keyName
	 * @param key
	 * @return
	 */
	public String getSASToken(String resourceUri, String keyName, String key) {
		
		logger.debug("Getting the SasToken for resourceUri:"+resourceUri+", KeyName:"+keyName+", KeyValue:"+key);
		
        long epoch = System.currentTimeMillis()/1000L;
        int week = 60*60*24*7;
        String expiry = Long.toString(epoch + week);

        String sasToken = null;
        StringBuilder sb = new StringBuilder();
        
        try {
            String stringToSign = URLEncoder.encode(resourceUri, "UTF-8") + "\n" + expiry;
            String signature = getHMAC256(key, stringToSign);
            
            sb.append("SharedAccessSignature sr=").append(URLEncoder.encode(resourceUri, "UTF-8"))
              .append("&sig=").append(URLEncoder.encode(signature, "UTF-8")).append("&se=").append(expiry)
              .append("&skn=").append(keyName);
            //sasToken = "SharedAccessSignature sr=" + URLEncoder.encode(resourceUri, "UTF-8") +"&sig=" +
            //        URLEncoder.encode(signature, "UTF-8") + "&se=" + expiry + "&skn=" + keyName;
            sasToken = sb.toString();
            
        } catch (UnsupportedEncodingException e) {
            logger.error("Got Exception while getting the SASToken", e);
            throw new RuntimeException(e);
        }
        
        logger.debug("Retrieved the SasToken successfully");
        
        return sasToken;

	}
	
	  public String getHMAC256(String key, String input) {
	      Mac sha256_HMAC = null;
	      String hash = null;
	      try {
	          sha256_HMAC = Mac.getInstance("HmacSHA256");
	          SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
	          sha256_HMAC.init(secret_key);
	          Encoder encoder = Base64.getEncoder();

	          hash = new String(encoder.encode(sha256_HMAC.doFinal(input.getBytes("UTF-8"))));

	      } catch (Exception e) {
	          logger.error("Got Exception while getting the HashSigned Value of SAS AccessToken", e);
	      }

	      return hash;
	  } 	
	

	private String getEncodedString(String s) {
		
		String encodedString = null;
		try {
			encodedString = URLEncoder.encode(s, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return encodedString;
	}
	
	public Azure getAzureService() {
		init();
		return this.azureService;
	}
	
	public String getCredFilePath() {
		return this.credFilePath;
	}	
	
	public RestTemplate getRestTemplateForMgmtAPI() throws Exception {
		logger.debug("RestTemplateForMgmtAPI::GetIdelCount;"+this.restTemplateMgmtAPIPool.getIdleCount()+", ActiveCount:"+this.restTemplateMgmtAPIPool.getActiveCount());
		return (RestTemplate)this.restTemplateMgmtAPIPool.getTarget();
	}

	public RestTemplate getRestTemplateForServiceBusAPI() throws Exception {
		logger.debug("RestTemplateForServiceBusAPI::GetIdelCount;"+this.restTemplateMgmtAPIPool.getIdleCount()+", ActiveCount:"+this.restTemplateMgmtAPIPool.getActiveCount());
		return (RestTemplate)this.restTemplateSBAPIPool.getTarget();
	}	
	
	public RestTemplate getRestTemplateForMonitorAPI() throws Exception {
		logger.debug("RestTemplateForMonitorAPI::GetIdelCount;"+this.restTemplateMgmtAPIPool.getIdleCount()+", ActiveCount:"+this.restTemplateMgmtAPIPool.getActiveCount());
		return (RestTemplate)this.restTemplateMonitorAPIPool.getTarget();
	}	
	
	public void releaseRestTemplateMgmtAPI(RestTemplate rt) throws Exception{
		this.restTemplateMgmtAPIPool.releaseTarget(rt);
	}

	public void releaseRestTemplateMonitorAPI(RestTemplate rt) throws Exception{
		this.restTemplateMonitorAPIPool.releaseTarget(rt);
	}	
	
	public void releaseRestTemplateServiceBusAPI(RestTemplate rt) throws Exception{
		this.restTemplateSBAPIPool.releaseTarget(rt);
	}	
	
	public String getTenantId() {
		return this.tenantId;
	}
	
	public String getClientId() {
		return this.clientId;
	}
	
	public String getClientSecret() {
		return this.clientSecret;
	}
	
	public String getDefaultNamespaceName() {
		return this.namespaceName;
	}
	
	public String getDefaultResourceGroupName() {
		return this.resourceGroupName;
	}
	
	public String getDefaultSubscriptionId() {
		return this.subscriptionId;
	}
	
	public String getDefaultMetricsTimespan() {
		return this.metricsTimespan;
	}

	public RestTemplate getRestTemplateForAADAPI() throws Exception {
		logger.debug("RestTemplateForAADAPI::GetIdelCount;"+this.restTemplateMgmtAPIPool.getIdleCount()+", ActiveCount:"+this.restTemplateMgmtAPIPool.getActiveCount());
		RestTemplate rt = (RestTemplate) this.restTemplateAADAPIPool.getTarget();
		return rt;
	}
	
	public void releaseRestTemplateForAADAPI(RestTemplate rt) throws Exception {
		this.restTemplateAADAPIPool.releaseTarget(rt);
	}
	
	/**
	 * AppContext is the standard way we use throughout the Application to access the  
	 * configuration properties. But since we access them in a static way, we require 
	 * to inject those properties in a non-static way like this.
	 * 
	 */
	private void initAppContext() {
		
		AppContext.setClientId(this.clientId);
		AppContext.setClientSecret(this.clientSecret);
		AppContext.setDefaultNamespaceName(this.namespaceName);
		AppContext.setDefaultResourceGroupName(this.resourceGroupName);
		AppContext.setDefaultSubscriptionId(this.subscriptionId);
		AppContext.setDefaultTenantId(this.tenantId);
		AppContext.setMetricsTimespan(this.metricsTimespan);
		AppContext.setMonitorMetricsTimegrain(this.monitorMetricsTimegrain);
		AppContext.setBatchMetricsFrequency(this.batchMetricsFrequency);
		AppContext.setMessageSize(messageSize);
		
	}
	
	public JmsSender getJmsConfigSender() {
		return this.jmsConfigSender;
	}
	
	public JmsSender getJmsMetricsSender() {
		return this.jmsMetricsSender;
	}
	

}

