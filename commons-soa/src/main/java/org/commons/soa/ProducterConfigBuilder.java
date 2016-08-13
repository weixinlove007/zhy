package org.commons.soa;

import java.io.InputStream;
import java.util.HashMap;
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
public class ProducterConfigBuilder {

	private static final Logger logger = LoggerFactory.getLogger(ProducterConfigBuilder.class);

	public static final Object lock = new Object();

	private ProducterConfig bean = null;

	private InputStream config;

	public ProducterConfigBuilder(InputStream config) {
		this.config = config;
	}

	public void build() {
		buildClass();
	}

	public ProducterConfig getObject() {
		return this.bean;
	}

	private void buildClass() {

		bean = new ProducterConfig();

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

				NodeList serviceNodelList = moduleElement.getElementsByTagName("service");
				for (int i = 0; i < serviceNodelList.getLength(); i++) {
					Element node = (Element) serviceNodelList.item(i);
					String name = node.getAttribute("name");
					String version = node.getAttribute("version");
					String logLever = node.getAttribute("logLever");

					NodeList serviceIntfList = node.getElementsByTagName("service-intf");
					NodeList serviceImplList = node.getElementsByTagName("service-impl");
					if (serviceIntfList != null && serviceIntfList.getLength() > 0 && serviceImplList != null && serviceImplList.getLength() > 0) {

						Element serviceIntf = (Element) serviceIntfList.item(0);
						Element serviceImpl = (Element) serviceImplList.item(0);
						ProducterServiceMeta meta = new ProducterServiceMeta();
						meta.setName(name);
						meta.setVersion(version);
						meta.setLogLever(logLever.trim());

						String serviceIntfClz = serviceIntf.getAttribute("value");
						String serviceImplClz = serviceImpl.getAttribute("value");
						meta.setIntf(serviceIntfClz);
						meta.setImpl(serviceImplClz);

						meta.setIntfClz(Class.forName(serviceIntfClz));
						meta.setImplClz(Class.forName(serviceImplClz));
						meta.setImplObj(meta.getImplClz().newInstance());

						Map<String, ProducterServiceMeta> serviceMetaMap = bean.getModuleMetaConfig().get(moduleName);
						if (serviceMetaMap == null) {
							serviceMetaMap = new HashMap<String, ProducterServiceMeta>();
							bean.getModuleMetaConfig().put(moduleName, serviceMetaMap);
						}
						serviceMetaMap.put(meta.getIntf() + "#" + version, meta);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
 
	}

}
