package com.vcc.asb.model;

import java.util.ArrayList;
import java.util.List;

import com.vcc.asb.config.model.AccessRights;

public class AuthKeys {
	
	private String keyName;
	private String primaryKey;
	private String secondaryKey;
	private List<AccessRights> rights;
	
	public AuthKeys(String keyName, String pk, String sk) {
		this.keyName = keyName;
		this.primaryKey = pk;
		this.secondaryKey = sk;
		this.rights = new ArrayList<AccessRights>();
	}
	
	public String getKeyName() {
		return this.keyName;
	}
	
	public String getPrimaryKey() {
		return this.primaryKey;
	}
	
	public String getSecondaryKey() {
		return this.secondaryKey;
	}
	
	public List<AccessRights> getRights() {
		if(this.rights==null) {
			this.rights = new ArrayList<AccessRights>();
		}
		
		return this.rights;
	}
	
}
