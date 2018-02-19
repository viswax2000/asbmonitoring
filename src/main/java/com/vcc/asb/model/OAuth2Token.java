package com.vcc.asb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuth2Token {
	
	@JsonProperty("token_type")
	String tokenType;
	
	@JsonProperty("expires_in")
	long expiresIn;
	
	@JsonProperty("ext_expires_in")
	long extExpiresIn;
	
	@JsonProperty("expires_on")
	long expiresOn;
	
	@JsonProperty("not_before")
	long notBefore;
	
	@JsonProperty("resources")
	String resourceUri;
	
	@JsonProperty("access_token")
	String accessToken;

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public long getExtExpiresIn() {
		return extExpiresIn;
	}

	public void setExtExpiresIn(long extExpiresIn) {
		this.extExpiresIn = extExpiresIn;
	}

	public long getExpiresOn() {
		return expiresOn;
	}

	public void setExpiresOn(long expiresOn) {
		this.expiresOn = expiresOn;
	}

	public long getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(long notBefore) {
		this.notBefore = notBefore;
	}

	public String getResourceUri() {
		return resourceUri;
	}

	public void setResourceUri(String resourceUri) {
		this.resourceUri = resourceUri;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
}
