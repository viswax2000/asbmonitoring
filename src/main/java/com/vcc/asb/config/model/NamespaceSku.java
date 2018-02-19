package com.vcc.asb.config.model;

public enum NamespaceSku {
	
	BASIC(SkuName.BASIC, SkuTier.BASIC),
	STANDARD(SkuName.STANDARD, SkuTier.STANDARD),
	PREMIUM_CAPACITY1(SkuName.PREMIUM, SkuTier.BASIC, 1), 
	PREMIUM_CAPACITY2(SkuName.PREMIUM, SkuTier.BASIC, 2),
	PREMIUM_CAPACITY4(SkuName.PREMIUM, SkuTier.BASIC, 4);
	
	private Sku sku;
	
	private NamespaceSku(SkuName name, SkuTier tier) {
		this.sku = new Sku().withSkuName(name).withSkuTier(tier);
	}
	
	private NamespaceSku(SkuName name, SkuTier tier, int capacity) {
		this.sku = new Sku().withSkuName(name).withSkuTier(tier).withCapacity(capacity);
	}
	
	
	public SkuName getSkuName() {
		return this.sku.getSkuName();
	}
	
	public SkuTier getSkuTier() {
		return this.sku.getSkuTier();
	}
	
	public int getCapacity() {
		return this.sku.getCapacity();
	}

}
