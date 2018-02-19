package com.vcc.asb.configuration;

public enum RollupMetricsTimegrain {

	PT5M("PT2H"),
	PT1H("P7D"),
	P1D("P60D"),
	P7D("P730D");
	
	private String rollupDuration;
	
	private RollupMetricsTimegrain(String s) {
		this.rollupDuration = s;
	}
	
	public String getRollupDuration() {
		return this.rollupDuration;
	}
	
	
}
