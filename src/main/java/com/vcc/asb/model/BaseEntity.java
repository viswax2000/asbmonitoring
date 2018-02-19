package com.vcc.asb.model;

import java.util.ArrayList;
import java.util.List;

import com.vcc.asb.config.model.AccessRights;

public class BaseEntity {
	
	protected String resourceGroupName;
	protected String namespaceName;
	protected List<AuthKeys> authKeys;
	protected String sasTokenNamespaceRootKey;
	
	protected BaseEntity(String resGrpName, String nsName) {
		this.resourceGroupName = resGrpName;
		this.namespaceName = nsName;
	}
	
	public String getNamespaceName() {
		return this.namespaceName;
	}
	
	public String getResourceGroupName() {
		return this.resourceGroupName;
	}
	
	public List<AuthKeys> getAuthKeys() {
		if(this.authKeys==null) {
			this.authKeys = new ArrayList<AuthKeys>();
		}
		return this.authKeys;
	}
	
	public AuthKeys getAuthKeys(AccessRights accessRights) {
		AuthKeys ak = null;
		for(AuthKeys authKey: authKeys) {
			for(AccessRights ar: authKey.getRights()) {
				if(ar == accessRights) {
					ak = authKey;
					break;
				}
			}
		}
		return ak;
	}

	public String getSasTokenNamespaceRootKey() {
		return sasTokenNamespaceRootKey;
	}

	public void setSasTokenNamespaceRootKey(String sasTokenNamespaceRootKey) {
		this.sasTokenNamespaceRootKey = sasTokenNamespaceRootKey;
	}
	
	

}
