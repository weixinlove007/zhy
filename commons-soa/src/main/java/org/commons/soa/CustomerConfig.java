package org.commons.soa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerConfig {

	private Map<String, Map<String, CustomerServiceMeta>> moduleMetaConfig = new HashMap<String, Map<String, CustomerServiceMeta>>();

	private Map<String, List<CustomerClusterMeta>> clusterList = new HashMap<String, List<CustomerClusterMeta>>();

	public Map<String, Map<String, CustomerServiceMeta>> getModuleMetaConfig() {
		return moduleMetaConfig;
	}

	public void setModuleMetaConfig(Map<String, Map<String, CustomerServiceMeta>> moduleMetaConfig) {
		this.moduleMetaConfig = moduleMetaConfig;
	}

	public Map<String, List<CustomerClusterMeta>> getClusterList() {
		return clusterList;
	}

	public void setClusterList(Map<String, List<CustomerClusterMeta>> clusterList) {
		this.clusterList = clusterList;
	}

}
