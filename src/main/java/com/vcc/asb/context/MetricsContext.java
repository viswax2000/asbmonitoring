package com.vcc.asb.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vcc.asb.metrics.command.Command;
import com.vcc.asb.metrics.model.Base;

public class MetricsContext extends AbstractContext {
	
	private Map<MetricsEntityType, ArrayList<? extends Base>> metrics;
	private Map<MetricsCommandType, ArrayList<? extends Command>> commands;
	private String aadAccessToken;
	
	public MetricsContext() {
		
		this.metrics = new HashMap<MetricsEntityType, ArrayList<? extends Base>>();

		for(MetricsEntityType mt: MetricsEntityType.values()) {
			metrics.put(mt, new ArrayList<Base>());
		}

		this.commands = new HashMap<MetricsCommandType, ArrayList<? extends Command>>();

		for(MetricsCommandType mct: MetricsCommandType.values()) {
			this.commands.put(mct, new ArrayList<Command>());
		}

	}
	
	@SuppressWarnings("unchecked")
	public <T> void addMetrics(T t) {

		MetricsEntityType mt = MetricsEntityType.getMetricsEntityType(t.getClass());
		if(mt!=null) {
			ArrayList<T> metricsTypeList = (ArrayList<T>) this.metrics.get(mt);
			metricsTypeList.add(t);
		} else {
			//throw new Exception("!!!INvalid Metrics Type !!!");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Base> getMetrics(MetricsEntityType mType) {
		ArrayList<Base> metricsList = (ArrayList<Base>) this.metrics.get(mType);
		return metricsList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Command> getMetricTypeCommands(MetricsCommandType mcType) {
		return (List<Command>) this.commands.get(mcType);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void addMetricsTypeCommand(T command) {
		MetricsCommandType mct = MetricsCommandType.getMetricsCommandType(command.getClass());
		if(mct!=null) {
			ArrayList<T> commandsList = (ArrayList<T>) this.commands.get(mct);
			commandsList.add(command);
		} else {
			
		}
	}
	
	public void setAADAccessToken(String token) {
		this.aadAccessToken = token;
	}
	
	public String getAADAccessToken() {
		return this.aadAccessToken;
	}

}
