package com.aeye.pam.socket.init;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aeye.pam.socket.proxy.BaseHandlerProxy;

public class SocketInit{

	private static Logger logger = LoggerFactory.getLogger(SocketInit.class);
	
	private static SocketInit procesor = null;
	
	private SocketInit(){}
	
	public static SocketInit getInstance(){
		if(null == procesor){
			return new SocketInit();
		}
		return procesor;
	}

	// web启动完成后调用该方法
	public void init() {
		try {
			List<SocketPortHandler> handlerList = SocketConfig.getInstance().getHandlerList();
			for(SocketPortHandler handler : handlerList){
				IoAcceptor acceptor = new NioSocketAcceptor();
				DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
				chain.addLast("codec", new ProtocolCodecFilter(  
						new TextLineCodecFactory(Charset.forName("UTF-8"))));  
		        chain.addLast("logger", new LoggingFilter());
		        BaseHandlerProxy handlerProxy = BaseHandlerProxy.getInstance().proxy(handler.getHandler());
		        if(!CollectionUtils.isEmpty(handler.getPreInterceptorList())){
		        	handlerProxy.setPreInterceptors(handler.getPreInterceptorList());
		        }
		        if(!CollectionUtils.isEmpty(handler.getAfterInterceptorList())){
		        	handlerProxy.setAfterInterceptors(handler.getAfterInterceptorList());
		        }
		        if(!CollectionUtils.isEmpty(handler.getAroundInterceptorList())){
		        	handlerProxy.setAroundInterceptors(handler.getAroundInterceptorList());
		        }
		        acceptor.setHandler(handlerProxy);
				acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, handler.getIdleTime()); // 单位是秒
				acceptor.bind(new InetSocketAddress(handler.getPort()));
				logger.info("-----> 服务器端口:{}已开始socket监听!", handler.getPort());
				// 目前不支持多个端口
				break;
			}
			logger.info("************* 系统启动测试InitProcessor成功 ***************");
		} catch (Exception e) {
			logger.error("!!!!!!!!!!!!系统启动加载socket服务时出现异常!!!!!!!!!!!!!!!", e);
		}
	}
	

}
