package org.commons.soa;

/**
 * 服务调用元数据
 * 
 * @author cyp
 */
public class ProducterServiceMeta {

	private String name;

	private String version;

	private String impl;

	private Class implClz;

	private Object implObj;

	private String intf;

	private Class intfClz;

	private String logLever;

	public Class getImplClz() {
		return implClz;
	}

	public void setImplClz(Class implClz) {
		this.implClz = implClz;
	}

	public Class getIntfClz() {
		return intfClz;
	}

	public void setIntfClz(Class intfClz) {
		this.intfClz = intfClz;
	}

	public String getLogLever() {
		return logLever;
	}

	public Object getImplObj() {
		return implObj;
	}

	public void setImplObj(Object implObj) {
		this.implObj = implObj;
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

	public String getImpl() {
		return impl;
	}

	public void setImpl(String impl) {
		this.impl = impl;
	}

	public String getIntf() {
		return intf;
	}

	public void setIntf(String intf) {
		this.intf = intf;
	}

}
