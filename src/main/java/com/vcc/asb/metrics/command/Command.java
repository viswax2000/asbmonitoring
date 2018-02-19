package com.vcc.asb.metrics.command;

import java.util.List;

import com.vcc.asb.configuration.EntityType;
import com.vcc.asb.model.BaseEntity;
import com.vcc.asb.service.Client;

public interface Command<V> {
	
	//void execute();
	
	void execute(Client client);
	
	List<MetricsType> getMetricsTypes();
	
	V getCommandResult();
	
	boolean isCommandSuccessful();
	
	BaseEntity getEntity();
	
	EntityType getEntityType();
	
	Exception getCommandException();
	
	void setMetricsType(MetricsType metricsType);
	
}
