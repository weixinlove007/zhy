    
 监控程序接入文档说明
 ---
        
  >随着互联网的快速发展，用户在网上差生了很多购买行为，但是很多场景都需要在现场来完成购买物品的提取
  例如：网上购票 火车站提票，网上购买电影票在电影院提票，网上购物，如果人没在家，快递可以临时存放在速递易，
  可以看出，互联网的繁荣也带来了线下自助终端机昌盛，那么问题了，这么多分布在不同地方的自助终端机，其中有
  windows系统、android系统、linux系统，怎么把他们有效的管理起来，并对他们做一些远程的操作和监控，所以本文
  局势针对这个问题作出的解决方案！
  
  * 需求点1：监控设备是否在线
  * 需求点2：监控设备基础参数和业务参数是否正常
  * 需求点2：对监控设备进行一些远程的操作或者说是通讯
  
  * 注意点1：每台自助终端机要有一个唯一的设备编号
  * 注意点2：每台设备要和服务器端建立一个tcp长连接，该框架的通讯的的规则是，一条消息站一行，也就是以“\n”结尾
  * 注意点3：在web程序的classpath下面创建xml文件，以下配置文件代表web监听9009端口，50s超时时间，对口对应的处理器是GgfwSocketHandlerImpl
  
  ```
  <socket-all>
    <!--该类必须继承AbstractSocketHandler -->
	  <socket-config port="9009" idle="50" bean="com.aeye.pam.manage.impl.GgfwSocketHandlerImpl">
	    <!--该类必须实现BaseSocketInterceptor -->
		  <socket-interceptor role="pre" bean="com.aeye.pam.manage.impl.DeviceSocketInterceptor" />
		  <socket-interceptor role="after" bean="com.aeye.pam.manage.impl.GgfwAfterInterceptor" />
	  </socket-config>
  </socket-all>
  
  ```
  
  
  * 注意点4：在web程序启动后，调用下面的方法，程序就可以开始监听9009端口了
  ```
  SocketInit.getInstance().init();
  ```
  
  * 注意点5：程序端口处理类，必须继承AbstractSocketHandler
  
// web接收到那台设备发送的消息
```
@Override
public abstract void onMessage(String deviceCode, String message);
```
// 哪台设备终端已经下线了
```
@Override
public abstract void onCLose(String deviceCode);
```

// 哪台设备第一次连上服务器
```
@Override
public abstract void firstConn(String deviceCode);
```
  
  
