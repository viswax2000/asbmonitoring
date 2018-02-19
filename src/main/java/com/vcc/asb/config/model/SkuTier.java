package com.vcc.asb.config.model;

public enum SkuTier {
	
	BASIC("Basic"),
	STANDARD("Standard"),
	PREMIUM("Premium");
	
	private String value;
	
	private SkuTier(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	

}
