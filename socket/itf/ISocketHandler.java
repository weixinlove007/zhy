package com.aeye.pam.socket.itf;



public interface ISocketHandler {

	public void onMessage(String deviceCode, String message);
	
	public void onCLose(String deviceCode);
	
	public void firstConn(String deviceCode);
	
}
