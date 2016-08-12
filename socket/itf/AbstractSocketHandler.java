package com.aeye.pam.socket.itf;


public abstract class AbstractSocketHandler implements ISocketHandler {

	@Override
	public abstract void onMessage(String deviceCode, String message);

	@Override
	public abstract void onCLose(String deviceCode);
	
	@Override
	public abstract void firstConn(String deviceCode);

}
