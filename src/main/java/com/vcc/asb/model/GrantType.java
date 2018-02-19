package com.vcc.asb.model;

public enum GrantType {
	
	CLIENT_CREDENTIALS("client_credentials"),
	AUTHORIZATION_CODE("authorization_code"),
	IMPLICIT("implicit"),
	REFRESH_TOKEN("refresh_token"),
	PASSWORD("password"),
	JWT_BEARER("urn:ietf:params:oauth:grant-type:jwt-bearer"),
	SAML2_BEARER("urn:ietf:params:oauth:grant-type:saml2-bearer");
	
	private String grantType;
	
	private GrantType(String grantType) {
		this.grantType = grantType;
	}
	
	public String getGrantType() {
		return this.grantType;
	}

}
