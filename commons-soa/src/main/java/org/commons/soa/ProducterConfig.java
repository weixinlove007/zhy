package org.commons.soa;

import java.util.HashMap;
import java.util.Map;

public class ProducterConfig {

	private Map<String, Map<String, ProducterServiceMeta>> moduleMetaConfig = new HashMap<String, Map<String, ProducterServiceMeta>>();

	public Map<String, Map<String, ProducterServiceMeta>> getModuleMetaConfig() {
		return moduleMetaConfig;
	}

	public void setModuleMetaConfig(Map<String, Map<String, ProducterServiceMeta>> moduleMetaConfig) {
		this.moduleMetaConfig = moduleMetaConfig;
	}

}
