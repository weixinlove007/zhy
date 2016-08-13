
>基于hessian实现的rpc远程调用框架
支持负载均衡，集群调用，比传统的http调用更加安全和方便

---

接入方式
---

1.第一步： 在主工程POM添加依赖文件
```
       <dependency>
           <groupId>com.caucho</groupId>
           <artifactId>hessian</artifactId>
           <version>4.0.7</version>
       </dependency>
       <dependency>
           <groupId>com.aeye</groupId>
           <artifactId>commons-soa</artifactId>
           <version>1.0.0</version>
       </dependency>
```

2.修改WEB.xml->消费者：

```
<servlet>
    <display-name>SOAServlet</display-name>
    <servlet-name>SOAServlet</servlet-name>
    <servlet-class>org.commons.soa.SOAServlet</servlet-class>
    <init-param>
           <param-name>ClientConfigPath</param-name>
           <param-value>/config/soa-consumer.xml</param-value>
    </init-param>
    <load-on-startup>0</load-on-startup>
</servlet>
```

3.修改WEB.xml->生产者：
```
<servlet>
		<display-name>SOAServlet</display-name>
		<servlet-name>SOAServlet</servlet-name>
		<servlet-class>org.commons.soa.SOAServlet</servlet-class>
		<init-param>
			<param-name>ServerConfigPath</param-name>
			<param-value>/config/soa-producer.xml</param-value>
		</init-param>
		<load-on-startup>0</load-on-startup>	
	</servlet>

	<servlet-mapping>
		<servlet-name>SOAServlet</servlet-name>
		<url-pattern>/rpc</url-pattern>
	</servlet-mapping>
```

4.生产者config文件
```
<service-producer>
	<module name="pam">
		<service name="模块描述" logLever="error" version="模块版本号">
			<service-impl value="接口实现类路径" />
			<service-intf value="接口类路径" />
		</service>
	</module>
</service-producer>
```

5.消费者config文件
```
<service-consumer>
	<clusters>
	<!-- 此处是集群配置 -->
		<module name="pro">
		  <address value="http://10.111.11.111:7008/pbs" />
			<address value="http://10.111.11.112:7008/pbs" />
			<address value="http://10.111.11.113:7008/pbs" />
		</module>
	</clusters>
	
	<module name="pro">
		<service name="" logLever="error" version="1.0">
			<service-intf value="接口类路径" />
		</service>
	</module>
</service-consumer>
```

6.远程调用
```
CustomerClientFactory.getHolder().getService(clz, module, version);
```






