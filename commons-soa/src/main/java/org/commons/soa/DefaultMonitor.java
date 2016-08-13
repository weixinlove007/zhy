package org.commons.soa;

public class DefaultMonitor implements Monitor {

	@Override
	public boolean heartbeat(String ip) {
		return true;
	}

}
