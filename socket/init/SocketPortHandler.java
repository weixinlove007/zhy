package com.aeye.pam.socket.init;

import java.util.ArrayList;
import java.util.List;

import com.aeye.pam.socket.interceptor.BaseSocketInterceptor;
import com.aeye.pam.socket.itf.ISocketHandler;

public class SocketPortHandler {

	private int port;

	private int idleTime;

	private ISocketHandler handler;

	// 消息接收的前置拦截器
	private List<BaseSocketInterceptor> preInterceptorList = new ArrayList<BaseSocketInterceptor>();
	private List<BaseSocketInterceptor> afterInterceptorList = new ArrayList<BaseSocketInterceptor>();
	private List<BaseSocketInterceptor> aroundInterceptorList = new ArrayList<BaseSocketInterceptor>();

	public void addPreInterceptor(BaseSocketInterceptor interceptor){
		preInterceptorList.add(interceptor);
	}
	
	public void addAfterInterceptor(BaseSocketInterceptor interceptor){
		afterInterceptorList.add(interceptor);
	}
	
	public void addAroundInterceptor(BaseSocketInterceptor interceptor){
		aroundInterceptorList.add(interceptor);
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public int getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}

	public ISocketHandler getHandler() {
		return handler;
	}

	public void setHandler(ISocketHandler handler) {
		this.handler = handler;
	}

	public List<BaseSocketInterceptor> getPreInterceptorList() {
		return preInterceptorList;
	}

	public void setPreInterceptorList(List<BaseSocketInterceptor> preInterceptorList) {
		this.preInterceptorList = preInterceptorList;
	}

	public List<BaseSocketInterceptor> getAfterInterceptorList() {
		return afterInterceptorList;
	}

	public void setAfterInterceptorList(
			List<BaseSocketInterceptor> afterInterceptorList) {
		this.afterInterceptorList = afterInterceptorList;
	}

	public List<BaseSocketInterceptor> getAroundInterceptorList() {
		return aroundInterceptorList;
	}

	public void setAroundInterceptorList(
			List<BaseSocketInterceptor> aroundInterceptorList) {
		this.aroundInterceptorList = aroundInterceptorList;
	}

}
