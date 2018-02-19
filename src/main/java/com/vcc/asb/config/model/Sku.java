package com.vcc.asb.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sku {
	
    @JsonProperty(value = "name")
	SkuName skuName;
    
    @JsonProperty(value = "tier", required = true)
	SkuTier skuTier;
    
    @JsonProperty(value = "capacity")
	Integer capacity;

	public SkuName getSkuName() {
		return skuName;
	}

	public Sku withSkuName(SkuName skuName) {
		this.skuName = skuName;
		return this;
	}

	public SkuTier getSkuTier() {
		return skuTier;
	}

	public Sku withSkuTier(SkuTier skuTier) {
		this.skuTier = skuTier;
		return this;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public Sku withCapacity(Integer capacity) {
		this.capacity = capacity;
		return this;
	}
	
}
