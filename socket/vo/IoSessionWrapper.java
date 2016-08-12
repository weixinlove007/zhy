package com.aeye.pam.socket.vo;

import java.util.Date;

import org.apache.mina.core.session.IoSession;

public class IoSessionWrapper{

	private IoSession ioSession;
	
	private Date lastUpdateTime;
	
	private String ipPort;
	
	private String deviceCode;
	
	private Integer deviceType;
	
	public IoSessionWrapper(IoSession ioSession, String ipPort, String deviceCode, Integer deviceType){
		this.ioSession = ioSession;
		this.ipPort = ipPort;
		this.deviceCode = deviceCode;
		this.deviceType = deviceType;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public IoSession getIoSession() {
		return ioSession;
	}

	public String getIpPort() {
		return ipPort;
	}

	public void setIpPort(String ipPort) {
		this.ipPort = ipPort;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}
	
}
