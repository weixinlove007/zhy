package org.commons.soa;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.HessianProxyFactory;

public class CustomerClient {

	private static final Logger logger = LoggerFactory.getLogger(CustomerClient.class);

	private InputStream config = null;

	private CustomerConfig customerConfig;

	private MonitorThread monitor = null;

	public CustomerClient(InputStream config) {
		this.config = config;
		CustomerConfigBuilder builder = new CustomerConfigBuilder(config);
		builder.build();
		customerConfig = builder.getObject();
		monitor = new MonitorThread(this);
		monitor.start();
	}

	public CustomerConfig getCustomerConfig() {
		return customerConfig;
	}

	public void setCustomerConfig(CustomerConfig customerConfig) {
		this.customerConfig = customerConfig;
	}

	protected <T> T getService(Class<T> clz, String iIp,String module, String version, boolean tipCheck) {

		String key = clz.getName() + "#" + version;

		if (!tipCheck) {
			CustomerServiceMeta meta = customerConfig.getModuleMetaConfig().get(module).get(key);
			if (meta == null) {
				throw new RuntimeException("not config service  : " + key);
			}
		}
		
		String ip = null;
		if(iIp == null || iIp.length() == 0){
			ip = this.randomIp(module);
		}
		else{
			ip = iIp;
		}
		
		String url = ip + "/rpc?id=" + clz.getName() + "&v=" + version+"&m="+module;
		HessianProxyFactory factory = new HessianProxyFactory();

		T d = null;
		try {
			d = (T) factory.create(clz, url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return d;
	}
	
	protected <T> List<T> getServiceCluster(Class<T> clz, String module, String version, boolean tipCheck) {

		String key = clz.getName() + "#" + version;

		if (!tipCheck) {
			CustomerServiceMeta meta = customerConfig.getModuleMetaConfig().get(module).get(key);
			if (meta == null) {
				throw new RuntimeException("not config service  : " + key);
			}
		}
		
		List<String> ipList = randomIpList(module);
		List<T> resultList = new ArrayList<T>();
		for(String ip : ipList){
			String url = ip + "/rpc?id=" + clz.getName() + "&v=" + version+"&m="+module;
			HessianProxyFactory factory = new HessianProxyFactory();
			T d = null;
			try {
				d = (T) factory.create(clz, url);
				resultList.add(d);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		}
		return resultList;
	}
	
	public <T> T getService(Class<T> clz, String module, String version) {
		return this.getService(clz,null, module, version, false);
	}
	
	public <T> List<T> getServiceCluster(Class<T> clz, String module, String version) {
		return this.getServiceCluster(clz, module, version, false);
	}

	private String randomIp(String module) {
		List<CustomerClusterMeta> list = customerConfig.getClusterList().get(module);
		
		System.out.println("test "+list.toString());
		System.out.println("test "+module);
		
		List<CustomerClusterMeta> randomList = new ArrayList<CustomerClusterMeta>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getState() == 1) {
				randomList.add(list.get(i));
			}
		}

		if (randomList.size() == 0) {
			throw new RuntimeException("not alive service producter node ");
		}

		int random = new Random().nextInt(randomList.size());
		return randomList.get(random).getIp();
	}
	
	private List<String> randomIpList(String module) {
		List<CustomerClusterMeta> list = customerConfig.getClusterList().get(module);
		List<String> resultIpList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getState() == 1) {
				resultIpList.add(list.get(i).getIp());
			}
		}

		if (resultIpList.size() == 0) {
			throw new RuntimeException("not alive service producter node");
		}

		return resultIpList;
	}

	public <T> T getService(Class<T> clz, String module) {
		return getService(clz, module, "");
	}

}