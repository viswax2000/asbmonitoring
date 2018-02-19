package com.vcc.asb.jms;

import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesInitialContextFactory extends PropertiesFileInitialContextFactory
		implements InitialContextFactory {
	
	private static Logger logger = LoggerFactory.getLogger(PropertiesInitialContextFactory.class);
	
	@Override
	public Context getInitialContext(Hashtable environment) throws NamingException {
		
		logger.debug("--------------Entered getINitialContext of PropertiesInitialContextFactory---------");
		/*for(StackTraceElement s: Thread.currentThread().getStackTrace()) {
			System.out.println(s);
		}*/
		
		Map data = new ConcurrentHashMap();

		if (environment.containsKey(Context.PROVIDER_URL)) {
			Properties props = (Properties) environment.get(Context.PROVIDER_URL);
			Iterator<Map.Entry<Object, Object>> iter = props.entrySet().iterator();

			while (iter.hasNext()) {
				Map.Entry<Object, Object> entry = iter.next();
				environment.put(entry.getKey(), entry.getValue());
			}
		}

		try {
			createConnectionFactories(data, environment);
		} catch (MalformedURLException e) {
			NamingException ne = new NamingException();
			ne.setRootCause(e);
			throw ne;
		}

		createDestinations(data, environment);

		createQueues(data, environment);

		createTopics(data, environment);
		
		logger.debug("------------Now returning the ReadContext Object----------------");
		
		return super.createContext(data, environment);		
		
		
	}

}
