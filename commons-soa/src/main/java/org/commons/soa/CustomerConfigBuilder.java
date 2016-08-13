package org.commons.soa;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author cyp
 */
public class CustomerConfigBuilder {

	private static final Logger logger = LoggerFactory.getLogger(CustomerConfigBuilder.class);

	public static final Object lock = new Object();

	private CustomerConfig bean = null;

	private InputStream config;

	public CustomerConfigBuilder(InputStream config) {
		this.config = config;
	}

	public void build() {
		buildClass();
	}

	public CustomerConfig getObject() {
		return this.bean;
	}

	private void buildClass() {

		bean = new CustomerConfig();

		// 得到DOM解析器的工厂实例
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		// 从DOM工厂中获得DOM解析器
		DocumentBuilder dbBuilder;
		try {
			dbBuilder = dbFactory.newDocumentBuilder();

			// 把要解析的xml文档读入DOM解析器
			Document document = dbBuilder.parse(config);

			Element rootElement = document.getDocumentElement();
			NodeList moduleNodeList = rootElement.getElementsByTagName("module");
			for (int m = 0; m < moduleNodeList.getLength(); m++) {
				Element moduleElement = (Element) moduleNodeList.item(m);
				String moduleName = moduleElement.getAttribute("name");
				String groupName = moduleElement.getAttribute("group");

				NodeList modelServiceList = moduleElement.getElementsByTagName("service");
				for (int i = 0; i < modelServiceList.getLength(); i++) {
					Element node = (Element) modelServiceList.item(i);
					String name = node.getAttribute("name");
					String version = node.getAttribute("version");
					String logLever = node.getAttribute("logLever");

					NodeList serviceIntfList = node.getElementsByTagName("service-intf");
					if (serviceIntfList != null && serviceIntfList.getLength() > 0) {

						Element serviceIntf = (Element) serviceIntfList.item(0);
						CustomerServiceMeta meta = new CustomerServiceMeta();
						meta.setName(name);
						meta.setVersion(version);
						meta.setLogLever(logLever.trim());
						meta.setIntf(serviceIntf.getAttribute("value"));

						Map<String, CustomerServiceMeta> serviceMetaMap = bean.getModuleMetaConfig().get(moduleName);
						if (serviceMetaMap == null) {
							serviceMetaMap = new HashMap<String, CustomerServiceMeta>();
							bean.getModuleMetaConfig().put(moduleName, serviceMetaMap);
						}

						serviceMetaMap.put(meta.getIntf() + "#" + version, meta);
					}
				}
			}
			
			NodeList groupList = ((Element)rootElement.getElementsByTagName("clusters").item(0)).getElementsByTagName("module");
			for (int i = 0; i < groupList.getLength(); i++) {
				Element groupNode = (Element) groupList.item(i);
				String groupName = groupNode.getAttribute("name");
				
				NodeList addressList = groupNode.getElementsByTagName("address");
				for (int j = 0; j < addressList.getLength(); j++) {
					Element addressElement = (Element) addressList.item(j);
					String value = addressElement.getAttribute("value");
					if (value != null && value.length() > 0) {
						CustomerClusterMeta cMeta = new CustomerClusterMeta();
						cMeta.setIp(value);
						cMeta.setState(1);
						cMeta.setLastCheckTime(new Date());

						List<CustomerClusterMeta> list = bean.getClusterList().get(groupName);
						if (list == null) {
							list = new ArrayList<CustomerClusterMeta>();
							bean.getClusterList().put(groupName, list);
						}
						list.add(cMeta);
					}
				}
			}
			

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

}
