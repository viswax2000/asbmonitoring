package com.vcc.asb.context;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BatchContext {
	
	private static final ThreadLocal<MetricsContext> METRICS = new ThreadLocal<MetricsContext>();
	private static final ThreadLocal<ConfigContext> CONFIG = new ThreadLocal<ConfigContext>();
	private static final ThreadLocal<DateTime> BATCH_TIME = new ThreadLocal<DateTime>();
	private static final ThreadLocal<BatchType> BATCH_TYPE = new ThreadLocal<BatchType>();
	private static final List<DateTime> previousRuns = new AppContext.FixedSizeRollingList<DateTime>();
	private static DateTime lastRun = null; 
	
	private static Logger logger = LoggerFactory.getLogger(BatchContext.class);
	
	//private. All invokations to use static methods
	private BatchContext() {
	}
	
	public static void setMetricsContext(MetricsContext context) {
		METRICS.set(context);
	}
	
	public static MetricsContext getMetricsContext() {
		MetricsContext ctx = null;
		
		if(METRICS.get() == null) {
			ctx = new MetricsContext();
			setCurrentContext(ctx);
		} else {
			ctx = METRICS.get();
		}
		
		return ctx;
	}
	
	public static ConfigContext getConfigContext() {
		ConfigContext ctx = null;
		if(CONFIG.get() == null) {
			ctx = new ConfigContext();
			setCurrentContext(ctx);
		} else {
			ctx = CONFIG.get();
		}
		
		return ctx;
	}
	
	public static BatchType getCurrentBatchType() {
		return BATCH_TYPE.get();
	}
	
	public static void removeCurrentContext() {
		CONFIG.set(null);
		METRICS.set(null);
		BATCH_TYPE.set(null);
		BATCH_TIME.set(null);
	}
	
	public static void setCurrentContext(Context context) {
		if(context instanceof ConfigContext) {
			CONFIG.set((ConfigContext)context);
		} else if(context instanceof MetricsContext) {
			METRICS.set((MetricsContext)context);
		}
	}
	
	public static void initBatchContext(BatchType batchType) {
		logger.debug("Initializing the Batch Context with batch type:"+batchType);
		lastRun = BATCH_TIME.get();
		removeCurrentContext();
		BATCH_TIME.set(DateTime.now());
		BATCH_TYPE.set(batchType);
		previousRuns.add(DateTime.now());
		setCurrentContext(new MetricsContext());
		setCurrentContext(new ConfigContext());
	}
	
	public static DateTime getLastRun() {
		return lastRun;
	}
	
	public static List<DateTime> getPreviousRuns() {
		return previousRuns;
	}
	
}
