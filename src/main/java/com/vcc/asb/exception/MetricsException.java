package com.vcc.asb.exception;

public class MetricsException extends RuntimeException {

	public MetricsException() {
		super();
	}
	
	public MetricsException(Exception ex) {
		super(ex);
	}
	
	public MetricsException(String exMsg) {
		super(exMsg);
	}
	
	public MetricsException(String exMsg, Exception ex) {
		super(exMsg, ex);
	}

}
