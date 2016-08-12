package com.aeye.pam.socket.interceptor.impl;

import org.apache.commons.lang.StringUtils;

import com.aeye.pam.socket.interceptor.BaseSocketInterceptor;

public class EmptyScoketInterceptor implements BaseSocketInterceptor{

	@Override
	public boolean handle(String receiveMsg) {
		return StringUtils.isNotBlank(receiveMsg);
	}

}
