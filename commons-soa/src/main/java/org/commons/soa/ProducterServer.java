package org.commons.soa;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducterServer {

	private static final Logger logger = LoggerFactory.getLogger(ProducterServer.class);

	InputStream config = null;

	private ProducterConfig producterConfig;

	public static Object lock = new Object();

	private static ProducterServer instance;

	public static synchronized void init(InputStream is) {

		if (instance != null) {
			//throw new RuntimeException("hased init ");
			return;
		}

		if (is == null) {
			throw new RuntimeException("config inputStream is null ");
		}

		synchronized (lock) {
			if (instance == null) {
				instance = new ProducterServer(is);
			}
		}

	}

	public static ProducterServer getHolder() {
		if (instance == null) {
			throw new RuntimeException("not init ");
		}
		return instance;
	}

	private ProducterServer(InputStream config) {
		this.config = config;
		ProducterConfigBuilder builder = new ProducterConfigBuilder(config);
		builder.build();
		producterConfig = builder.getObject();
	}

	public ServiceRegister getRegistService(String module, String serviceName) {

		ProducterServiceMeta meta = producterConfig.getModuleMetaConfig().get(module).get(serviceName);
		if (meta != null) {
			ServiceRegister service = new ServiceRegister();
			service.setIntf(meta.getIntfClz());
			service.setImpl(meta.getImplClz());
			service.setImplObj(meta.getImplObj());
			return service;
		}
		return null;
	}

}