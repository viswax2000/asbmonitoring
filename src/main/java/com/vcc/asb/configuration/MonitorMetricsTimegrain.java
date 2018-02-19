package com.vcc.asb.configuration;

public enum MonitorMetricsTimegrain {
	
	PT1M("P93D"),
	PT1H("P93D");
	
	private String rollupDuration;
	
	private MonitorMetricsTimegrain(String s) {
		this.rollupDuration = s;
	}
	
	public String getRollupDuration() {
		return this.rollupDuration;
	}

}
