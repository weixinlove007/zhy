package org.commons.soa;

import java.io.InputStream;

public class CustomerClientFactory {

	public static CustomerClient client = null;

	public static Object lock = new Object();

	private CustomerClientFactory() {

	}

	public static synchronized void init(InputStream is) {

		if (client != null) {
			//throw new RuntimeException("hased init ");
			return;
		}

		if (is == null) {
			throw new RuntimeException("config inputStream is null ");
		}

		synchronized (lock) {
			if (client == null) {
				client = new CustomerClient(is);
			}
		}
	}

	public static void destory() {
		client = null;
	}

	public static CustomerClient getHolder() {
		if (client == null) {
			throw new RuntimeException("not init ");
		}
		return client;
	}

}
