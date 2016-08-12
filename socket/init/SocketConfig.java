package com.aeye.pam.socket.init;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aeye.pam.socket.interceptor.BaseSocketInterceptor;
import com.aeye.pam.socket.itf.AbstractSocketHandler;

/**
 * 此类主要是从配置文件读取监听端口和端口处理类，在WEB启动的时候进行注册和加载
 * @author weixin
 *
 */
public class SocketConfig {
	
	private static Logger logger = LoggerFactory.getLogger(SocketConfig.class);
	
	private static SocketConfig config = null;
	
	// 端口监听处理器
	private static List<SocketPortHandler> portHandlerList = new ArrayList<SocketPortHandler>();
	
	private SocketConfig(){}

	public static SocketConfig getInstance(){
		if(null == config){
			try {
				loadConfig();
			} catch (Exception e) {
				logger.error("初始化加载socket-config出现异常!!", e);
			} 
			return new SocketConfig();
		}
		return config;
	}
	
	public List<SocketPortHandler> getHandlerList(){
		return portHandlerList;
	}
	
	private static void buildInterceptor(Iterator<Element> it, 
			SocketPortHandler portHandler) throws Exception{
		 while(it.hasNext()){
        	Element node = it.next();
        	String role = node.attributeValue("role");
        	Class<?> clazz = Class.forName(node.attributeValue("bean"));
        	if("pre".equals(role)){
        		portHandler.addPreInterceptor((BaseSocketInterceptor) clazz.newInstance());
        	}else if("after".equals(role)){
        		portHandler.addAfterInterceptor((BaseSocketInterceptor) clazz.newInstance());
        	}else if("around".equals(role)){
        		portHandler.addAroundInterceptor((BaseSocketInterceptor) clazz.newInstance());
        	}
		 }
	}
	
	@SuppressWarnings("unchecked")
	public static void loadConfig() throws Exception {
		String xmlPath = "/config/socket-config.xml";
		InputStream ips = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlPath);
		if (null == ips) {
           throw new RuntimeException("未找到socket-config.xml");
        }  
        SAXReader reader = new SAXReader();  
        Document doc = reader.read(ips);  
        Element root = doc.getRootElement();
        Iterator<Element> it = root.elementIterator();
        while(it.hasNext()){
        	SocketPortHandler portHandler = new SocketPortHandler();
        	Element node = it.next();
        	portHandler.setPort(Integer.valueOf(node.attribute("port").getStringValue()));
        	portHandler.setIdleTime(Integer.valueOf(node.attribute("idle").getStringValue()));
        	Class<?> clazz = Class.forName(node.attribute("bean").getStringValue());
        	portHandler.setHandler((AbstractSocketHandler) clazz.newInstance());
        	// 构建拦截器
        	buildInterceptor(node.elementIterator(), portHandler);
        	portHandlerList.add(portHandler);
        }
	}
	
}
