package com.vcc.asb.config.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SkuName {
	
	BASIC("Basic"),
	STANDARD("Standard"),
	PREMIUM("Premium");
	
	private String value;
	
	private SkuName(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
    @JsonValue
    @Override
    public String toString() {
        return value;
    }

}
