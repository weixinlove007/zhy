package org.commons.soa;

public class ServiceRegister {

	private Object implObj;

	private Class impl;

	private Class intf;

	public Class getImpl() {
		return impl;
	}

	public Object getImplObj() {
		return implObj;
	}

	public void setImplObj(Object implObj) {
		this.implObj = implObj;
	}

	public void setImpl(Class impl) {
		this.impl = impl;
	}

	public Class getIntf() {
		return intf;
	}

	public void setIntf(Class intf) {
		this.intf = intf;
	}

}
