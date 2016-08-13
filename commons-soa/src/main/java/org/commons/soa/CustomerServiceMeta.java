package org.commons.soa;

/**
 * 服务调用元数据
 * 
 * @author cyp
 */
public class CustomerServiceMeta {

	private String name;

	private String version;

	private String intf;

	private String logLever;

	public String getLogLever() {
		return logLever;
	}

	public void setLogLever(String logLever) {
		this.logLever = logLever;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIntf() {
		return intf;
	}

	public void setIntf(String intf) {
		this.intf = intf;
	}

}
