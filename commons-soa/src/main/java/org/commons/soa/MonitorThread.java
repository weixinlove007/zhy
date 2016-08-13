package org.commons.soa;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监控节点数据状态线程
 * 
 */
public class MonitorThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(MonitorThread.class);

	private boolean isPause = false;

	private CustomerClient client = null;

	public MonitorThread(CustomerClient client) {
		this.client = client;
	}

	@Override
	public void run() {
		while (true) {
			try {

				Thread.sleep(30000);

				if (this.isPause) {
					synchronized (this) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				refresh();

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} 
	}

	private void refresh() {

		Iterator it = client.getCustomerConfig().getClusterList().keySet().iterator();

		while (it.hasNext()) {
			String module = (String) it.next();
			List<CustomerClusterMeta> list = client.getCustomerConfig().getClusterList().get(module);
			for (int i = 0; i < list.size(); i++) {
				CustomerClusterMeta meta = list.get(i);
				boolean alive = false;
				try {
					Monitor monitor = client.getService(Monitor.class, meta.getIp(),module,"",true);
					alive = monitor.heartbeat(meta.getIp());
				} catch (Exception e) {
					alive = false;
				}
				meta.setLastCheckTime(new Date());
				meta.setState(alive ? 1 : 0);
			}
		}

	}

	public void pauseReceive() {
		this.isPause = true;
	}

	public void resumeReceive() {
		this.isPause = false;
		synchronized (this) {
			this.notify();
		}
	}
}
