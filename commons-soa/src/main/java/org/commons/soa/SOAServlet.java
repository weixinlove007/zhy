package org.commons.soa;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import com.caucho.services.server.ServiceContext;

/**
 * Servlet for serving Hessian services.
 */
public class SOAServlet extends GenericServlet {

	private static final Logger logger = LoggerFactory
			.getLogger(SOAServlet.class);

	private SerializerFactory _serializerFactory;

	public SOAServlet() {

	}

	@Override
	public void init(ServletConfig config) throws ServletException {

		String ClientConfigPath = config.getInitParameter("ClientConfigPath");
		String ServerConfigPath = config.getInitParameter("ServerConfigPath");

		if (ClientConfigPath != null && ClientConfigPath.length() > 0) {
			InputStream clientInputStream = Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream(ClientConfigPath);
			CustomerClientFactory.init(clientInputStream);
		}

		if (ServerConfigPath != null && ServerConfigPath.length() > 0) {
			InputStream serverInputStream = Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream(ServerConfigPath);
			ProducterServer.init(serverInputStream);
		}

	}

	public String getServletInfo() {
		return "Hessian Servlet";
	}

	/**
	 * Sets the serializer factory.
	 */
	public void setSerializerFactory(SerializerFactory factory) {
		_serializerFactory = factory;
	}

	/**
	 * Gets the serializer factory.
	 */
	public SerializerFactory getSerializerFactory() {
		if (_serializerFactory == null)
			_serializerFactory = new SerializerFactory();

		return _serializerFactory;
	}

	/**
	 * Sets the serializer send collection java type.
	 */
	public void setSendCollectionType(boolean sendType) {
		getSerializerFactory().setSendCollectionType(sendType);
	}

	private Class loadClass(String className) throws ClassNotFoundException {
		ClassLoader loader = getContextClassLoader();

		if (loader != null)
			return Class.forName(className, false, loader);
		else
			return Class.forName(className);
	}

	protected ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * Execute a request. The path-info of the request selects the bean. Once
	 * the bean's selected, it will be applied.
	 */
	public void service(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String serviceId = req.getPathInfo();
		String objectId = req.getParameter("id");
		String v = req.getParameter("v");
		String m = req.getParameter("m");

		ServiceContext.begin(req, serviceId, objectId);
		InputStream is = request.getInputStream();
		ServletOutputStream os = response.getOutputStream();
		try {
			
			response.setContentType("application/x-hessian");
			SerializerFactory serializerFactory = getSerializerFactory();

			invoke(is, os, objectId, m, v, serializerFactory);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			writeError(req, res, os,e.getMessage());
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			writeError(req, res, os,e.getMessage());
		} finally {
			ServiceContext.end();
		}
	}

	private void writeError(HttpServletRequest req, HttpServletResponse res,ServletOutputStream os,
			String message) {

		try {
			res.setStatus(500, message);
			res.setContentType("text/html");
			os.println("<h1" + message + "</h1>");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

	protected void invoke(InputStream is, OutputStream os, String objectId,
			String module, String version, SerializerFactory serializerFactory)
			throws Exception {

		if (Monitor.class.getName().equals(objectId)) {
			HessianSkeleton _objectSkeleton = new HessianSkeleton(
					new DefaultMonitor(), Monitor.class);
			_objectSkeleton.invoke(is, os, serializerFactory);
			return;
		}

		ServiceRegister register = ProducterServer.getHolder()
				.getRegistService(module, objectId + "#" + version);
		HessianSkeleton _objectSkeleton = new HessianSkeleton(
				register.getImplObj(), register.getIntf());
		_objectSkeleton.invoke(is, os, serializerFactory);
	}

	protected Hessian2Input createHessian2Input(InputStream is) {
		return new Hessian2Input(is);
	}
}
