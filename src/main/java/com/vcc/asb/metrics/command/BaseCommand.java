package com.vcc.asb.metrics.command;

import java.util.List;

import org.joda.time.DateTime;

import com.vcc.asb.metrics.model.Timespan;

public abstract class BaseCommand<V> implements Command<V> {
	
	private MetricsType metricsType = null;
	protected boolean commandSuccessful = false;
	protected Exception commandException = null;
	Timespan metricsTimespan = null;
	DateTime startTime = null;
	DateTime endTime = null;
	
	public abstract List<MetricsType> getMetricsTypes();
	
	protected void invokeMonitorAPIEndpoint() {
		
	}
	
	public void setMetricsType(MetricsType mType) {
		metricsType = mType;
	}
	
	public MetricsType getMetricsType() {
		return this.metricsType;
	}
	
	public boolean isCommandSuccessful() {
		return this.commandSuccessful;
	}
	
	public Exception getCommandException() {
		return this.commandException;
	}
	
}
