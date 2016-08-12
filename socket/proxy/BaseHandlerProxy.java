package com.aeye.pam.socket.proxy;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aeye.pam.socket.interceptor.BaseSocketInterceptor;
import com.aeye.pam.socket.interceptor.impl.EmptyScoketInterceptor;
import com.aeye.pam.socket.itf.ISocketHandler;
import com.aeye.pam.socket.vo.IoSessionWrapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class BaseHandlerProxy extends IoHandlerAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(BaseHandlerProxy.class);

	private static Map<String, IoSessionWrapper> socketSessionMap = new ConcurrentHashMap<String, IoSessionWrapper>();

	private ISocketHandler handler;
	
	private static BaseHandlerProxy proxy;
	
	private List<BaseSocketInterceptor> preInterceptors;
	
	private List<BaseSocketInterceptor> afterInterceptors;
	
	private List<BaseSocketInterceptor> aroundInterceptors;

	private BaseHandlerProxy(ISocketHandler handler) {
		this.handler = handler;
	}
	
	public static BaseHandlerProxy getInstance(){
		if(null == proxy){
			return new BaseHandlerProxy(null);
		}
		return proxy;
	}
	
	public BaseHandlerProxy proxy(ISocketHandler handler){
		if(null == proxy){
			return new BaseHandlerProxy(handler);
		}
		return proxy;
	}
	
	public IoSession getIoSessionByCode(String deviceCode){
		if(!socketSessionMap.containsKey(deviceCode)) return null;
		return socketSessionMap.get(deviceCode).getIoSession();
	}
	
	public void sendMessage(String deviceCode, String message){
		IoSession ioSession = 
				BaseHandlerProxy.getInstance().getIoSessionByCode(deviceCode);
		if(null != ioSession && ioSession.isConnected()){
			ioSession.write(message);
			logger.info("------------> 指令发送成成功: {}", message);
		}
	}
	
	// 接受消息时 简单地打印了错误的堆栈跟踪
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {

	}
	
	private String getDeviceCode(String receiveMsg){
		JSONObject jsonObject = JSON.parseObject(receiveMsg);
		return jsonObject.getString("DeviceCode");
	}
	
	private Integer getDeviceType(String receiveMsg){
		JSONObject jsonObject = JSON.parseObject(receiveMsg);
		return jsonObject.getInteger("DeviceType");
	}

	// 从客户端接收到的数据
	@Override
	public void messageReceived(IoSession ioSession, Object message)
			throws Exception {
		String receiveMsg = message == null ? "" : message.toString();
		// 0. 执行默认拦截器
		EmptyScoketInterceptor emptyInterceptor = new EmptyScoketInterceptor();
		if(!emptyInterceptor.handle(receiveMsg)){
			logger.warn("服务socket接收到的消息为空!!");
			return;
		}
		
		// 1. 执行 preInterceptor
		if(CollectionUtils.isNotEmpty(this.preInterceptors)){
			logger.debug("开始执行服务器前置拦截器!!");
			for(BaseSocketInterceptor interceptor : this.preInterceptors){
				boolean result = interceptor.handle(receiveMsg);
				if(!result){
					throw new RuntimeException("违反约束拦截器:" + interceptor.getClass().getName());
				}
			}
			logger.debug("服务器前置拦截器验证通过!!");
		}
		// 2. 执行 aroundInterceptor
		if(CollectionUtils.isNotEmpty(this.aroundInterceptors)){
			for(BaseSocketInterceptor interceptor : this.aroundInterceptors){
				interceptor.handle(receiveMsg);
			}
		}
		// 主要时完成session管理和session更新
		String deviceCode = getDeviceCode(receiveMsg);
		Integer deviceType = getDeviceType(receiveMsg);
		// 将session会话信息保存在内存中
		// 将会话保存在缓存中
		IoSessionWrapper wIoSession = new IoSessionWrapper(
				ioSession, ioSession.getRemoteAddress().toString(), deviceCode, deviceType);
		wIoSession.setLastUpdateTime(new Date());
		if(!socketSessionMap.containsKey(deviceCode)){
			// 首次连接需要操作的逻辑
			socketSessionMap.put(deviceCode, wIoSession);
			handler.firstConn(deviceCode);
			// 推送升级指令
		}else{
			socketSessionMap.put(deviceCode, wIoSession);
		}
		handler.onMessage(deviceCode, receiveMsg);
		// 3. 执行 aroundInterceptor
		if(CollectionUtils.isNotEmpty(this.aroundInterceptors)){
			for(BaseSocketInterceptor interceptor : this.aroundInterceptors){
				interceptor.handle(receiveMsg);
			}
		}
		
		// 4. 执行 afterInterceptor
		if(CollectionUtils.isNotEmpty(this.afterInterceptors)){
			for(BaseSocketInterceptor interceptor : this.afterInterceptors){
				interceptor.handle(receiveMsg);
			}
		}
	}
	
    private String getDeviceCodebySession(IoSession ioSession){
    	String ipPort = ioSession.getRemoteAddress().toString();
		String targetKey = "";
		for (IoSessionWrapper sessionWrapper : socketSessionMap.values()) {
			if (sessionWrapper.getIpPort().equals(ipPort)) {
				targetKey = sessionWrapper.getDeviceCode();
				break;
			}
		}
		return targetKey;
    }

	@SuppressWarnings("deprecation")
	@Override
	public void sessionIdle(IoSession ioSession, IdleStatus status)
			throws Exception {
		ioSession.close(true);
	}
	
	public boolean isExist(String deviceCode){
		return socketSessionMap.containsKey(deviceCode);
	}
	
	public Map<String, IoSessionWrapper> getAllSessionMap(){
		return socketSessionMap;
	}

	@Override
	public void sessionClosed(IoSession ioSession) throws Exception {
		String deviceCode = getDeviceCodebySession(ioSession);
		if(StringUtils.isBlank(deviceCode)) return;
		socketSessionMap.remove(deviceCode);
		handler.onCLose(deviceCode);
	}

	public List<BaseSocketInterceptor> getPreInterceptors() {
		return preInterceptors;
	}

	public void setPreInterceptors(List<BaseSocketInterceptor> preInterceptors) {
		this.preInterceptors = preInterceptors;
	}

	public List<BaseSocketInterceptor> getAfterInterceptors() {
		return afterInterceptors;
	}

	public void setAfterInterceptors(List<BaseSocketInterceptor> afterInterceptors) {
		this.afterInterceptors = afterInterceptors;
	}

	public List<BaseSocketInterceptor> getAroundInterceptors() {
		return aroundInterceptors;
	}

	public void setAroundInterceptors(List<BaseSocketInterceptor> aroundInterceptors) {
		this.aroundInterceptors = aroundInterceptors;
	}
}
