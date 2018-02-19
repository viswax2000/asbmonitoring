package com.vcc.asb.exception;

public class ConfigException extends RuntimeException {
	
	public ConfigException() {
		super();
	}
	
	public ConfigException(String exMsg) {
		super(exMsg);
	}
	
	public ConfigException(String exMsg, Exception ex) {
		super(exMsg, ex);
	}
	
	public ConfigException(Exception ex) {
		super(ex);
	}

}
