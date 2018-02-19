package com.vcc.asb.context;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;

import com.vcc.asb.model.ASBEntities;
import com.vcc.asb.util.JAXBUtils;

public class AppContext {
	
	@Value("${default.subscription.id}")
	private static String subscriptionId;
	
	@Value("${default.tenant.id}")
	private static String tenantId;
	
	@Value("${default.resourcegroup.name}")
	private static String resourceGroupName;
	
	@Value("${default.namespace.name}")
	private static String namespaceName;
	
	@Value("${monitormetrics.timegrain}")
	private static String monitorMetricsTimegrain;
	
	@Value("${batch.metrics.frequency}")
	private static String batchMetricsFrequency;
	
	@Value("${default.client.id}")
	private static String clientId;

	@Value("${default.client.secret}")
	private static String clientSecret;
	
	@Value("${default.metrics.timespan}")
	private static String metricsTimespan;
	
	@Value("${message.size}")
	private static long messageSize;
	
	private static ASBEntities asbEntities;
	
	private static String monitorMetricsDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	private static com.vcc.asb.config.model.ObjectFactory objFac = new com.vcc.asb.config.model.ObjectFactory();
	private static com.vcc.asb.metrics.model.ObjectFactory metricsObjFac = new com.vcc.asb.metrics.model.ObjectFactory();
	
	private static JAXBUtils jaxbUtils = new JAXBUtils();
	
	public AppContext() {
		
	}

	public static ASBEntities getAsbEntities() {
		/*if(asbEntities == null) {
			asbEntities = new ASBEntities();
		}*/
		return asbEntities;
	}

	public static void setAsbEntities(ASBEntities asbE) {
		asbEntities = asbE;
	}
	
	public static String getDefaultNamespaceName() {
		return namespaceName;
	}
	
	public static String getDefaultResourceGroupName() {
		return resourceGroupName;
	}
	
	public static String getDefaultSubscriptionId() {
		return subscriptionId;
	}
	
	public static String getDefaultTenantId() {
		return tenantId;
	}
	
	public static String getMonitorMetricsTimegrain() {
		return monitorMetricsTimegrain;
	}
	
	public static String getBatchMetricsFrequency() {
		return batchMetricsFrequency;
	}
	
	public static String getMonitorMetricsDateTimeFormat() {
		return monitorMetricsDateTimeFormat;
	}
	
	public static String getClientId() {
		return clientId;
	}
	
	public static String getClientSecret() {
		return clientSecret;
	}
	
	public static String getMetricsTimespan() {
		return metricsTimespan;
	}
	
	public static com.vcc.asb.config.model.ObjectFactory getConfigObjectFactory() {
		return objFac;
	}
	
	public static com.vcc.asb.metrics.model.ObjectFactory getMetricsObjectFactory() {
		return metricsObjFac;
	}

	public static JAXBUtils getJaxbUtils() {
		return jaxbUtils;
	}

	public static void setClientId(String clientId2) {
		clientId = clientId2;
	}

	public static void setClientSecret(String clientSecret2) {
		clientSecret = clientSecret2;
	}

	public static void setDefaultNamespaceName(String namespaceName2) {
		namespaceName = namespaceName2;
	}

	public static void setDefaultResourceGroupName(String resourceGroupName2) {
		resourceGroupName = resourceGroupName2;
	}

	public static void setDefaultSubscriptionId(String subscriptionId2) {
		subscriptionId = subscriptionId2;
	}

	public static void setDefaultTenantId(String tenantId2) {
		tenantId = tenantId2;
	}

	public static void setMetricsTimespan(String metricsTimespan2) {
		metricsTimespan = metricsTimespan2;
	}

	public static void setMonitorMetricsTimegrain(String monitorMetricsTimegrain2) {
		monitorMetricsTimegrain = monitorMetricsTimegrain2;
	}

	public static void setBatchMetricsFrequency(String batchMetricsFrequency2) {
		batchMetricsFrequency = batchMetricsFrequency2;
	}
	
	public static void setMessageSize(long size) {
		messageSize = size;
	}
	
	public static long getMessageSize() {
		return messageSize;
	}
	
	/**
	 * Creates a Fixed size ArrayList. The List elements move to the left, similar to
	 * the bitsize operator if more elements are added.
	 * 
	 * @author HCL/Volvo-IT
	 *
	 * @param <E>
	 */
	static class FixedSizeRollingList<E> extends ArrayList<E> {
		
		int size = 0;
		
		//If not available default size of 100
		FixedSizeRollingList() {
			size = 100;
		}
		
		FixedSizeRollingList(int s) {
			this.size = s;
		}
		
		@Override
		public boolean add(E e) {
			if((this.size() + 1) > size) {
				for(int i=1;i<size;++i) {
					super.set((i-1), super.get(i));
				}
				super.set((size-1), e);
			} else {
				super.add(e);
			}
			return true;
		}
		
	}

	public static long getMessageSizeInBytes() {
		return messageSize;
	}		

}
