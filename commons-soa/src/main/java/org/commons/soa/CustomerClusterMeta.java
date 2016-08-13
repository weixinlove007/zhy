package org.commons.soa;

import java.util.Date;

public class CustomerClusterMeta {

	private String ip;

	private int state = 1;// 1 ok ,0 error

	private Date lastCheckTime = null;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getLastCheckTime() {
		return lastCheckTime;
	}

	public void setLastCheckTime(Date lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}

}
