/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software. 
* Entando is a free software; 
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.apsadmin.system.dispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import com.agiletec.apsadmin.system.dispatcher.mapper.ExtendedDefaultActionMapper;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

/**
 * Servlet a servizio della showlet "Internal Servlet".
 * La servlet ha lo scopo di permettere la erogazione nel Front-End di jAPS (all'interno dei frames di pagine) 
 * di funzionalità implementate su base Struts2.
 * @author E.Santoboni
 */
public class Struts2ServletDispatcher extends HttpServlet implements StrutsStatics {
	
	@Override
	public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
		ActionMapper actionMapper = new ExtendedDefaultActionMapper();
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        ServletContext servletContext = getServletContext();
        String timerKey = "FilterDispatcher_doFilter: ";
        try {
            UtilTimerStack.push(timerKey);
            request = prepareDispatcherAndWrapRequest(request, response);
            ActionMapping mapping;
            try {
				String entandoActionName = EntandoActionUtils.extractEntandoActionName(request);
                mapping = actionMapper.getMapping(request, dispatcher.getConfigurationManager());
				if (null != entandoActionName) {
					mapping.setName(entandoActionName);
				}
            } catch (Exception ex) {
                LOG.error("error getting ActionMapping", ex);
                dispatcher.sendError(request, response, servletContext, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
                return;
            }
            
            if (mapping == null) {
            	// there is no action in this request, should we look for a static resource?
                String resourcePath = RequestUtils.getServletPath(request);

                if ("".equals(resourcePath) && null != request.getPathInfo()) {
                    resourcePath = request.getPathInfo();
                }

                if (serveStatic && resourcePath.startsWith("/struts")) {
                    String name = resourcePath.substring("/struts".length());
                    findStaticResource(name, request, response);
                }
                
                return;
            }
            
            dispatcher.serviceAction(request, response, servletContext, mapping);
            
        } finally {
            try {
                cleanUp(req);
            } finally {
                UtilTimerStack.pop(timerKey);
            }
        }
	}
	
	
	/**
     * Provide a logging instance.
     */
    private static final Log LOG = LogFactory.getLog(Struts2ServletDispatcher.class);
    
    /**
     * Store set of path prefixes to use with static resources.
     */
    private String[] pathPrefixes;

    /**
     * Provide a formatted date for setting heading information when caching static content.
     */
    private final Calendar lastModifiedCal = Calendar.getInstance();

    /**
     * Store state of StrutsConstants.STRUTS_SERVE_STATIC_CONTENT setting.
     */
    private static boolean serveStatic;

    /**
     * Store state of StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE setting.
     */
    private static boolean serveStaticBrowserCache;

    /**
     * Store state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     */
    private static String encoding;
    
    /**
     * Expose Dispatcher instance to subclass.
     */
    protected Dispatcher dispatcher;
    
    /**
     * Calls dispatcher.cleanup,
     * which in turn releases local threads and destroys any DispatchListeners.
     *
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        if (dispatcher == null) {
            LOG.warn("something is seriously wrong, Dispatcher is not initialized (null) ");
        } else {
            dispatcher.cleanup();
        }
    }
    
    /**
     * Modify state of StrutsConstants.STRUTS_SERVE_STATIC_CONTENT setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_CONTENT)
    public static void setServeStaticContent(String val) {
        serveStatic = "true".equals(val);
    }
    
    /**
     * Modify state of StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE)
    public static void setServeStaticBrowserCache(String val) {
        serveStaticBrowserCache = "true".equals(val);
    }
    
    /**
     * Modify state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public static void setEncoding(String val) {
        encoding = val;
    }
    
    /**
     * Wrap and return the given request, if needed, so as to to transparently
     * handle multipart data as a wrapped class around the given request.
     *
     * @param request Our ServletRequest object
     * @param response Our ServerResponse object
     * @return Wrapped HttpServletRequest object
     * @throws ServletException on any error
     */
    protected HttpServletRequest prepareDispatcherAndWrapRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	
        Dispatcher du = Dispatcher.getInstance();
        
        // Prepare and wrap the request if the cleanup filter hasn't already, cleanup filter should be
        // configured first before struts2 dispatcher filter, hence when its cleanup filter's turn,
        // static instance of Dispatcher should be null.
        if (du == null) {
        	if (this.dispatcher == null) {
        		Properties props = new Properties();
            	InputStream is = this.getClass().getClassLoader().getResourceAsStream("struts.properties");
            	try {
        			props.load(is);
        		} catch (Throwable t) {
        			throw new ServletException("Errore in caricamento struts.properties", t);
        		}
        		
        		String struts2Config = this.getServletContext().getInitParameter(ApsAdminSystemConstants.STRUTS2_CONFIG_INIT_PARAM_NAME);
                if (null != struts2Config) {
                	props.setProperty("config", struts2Config);
                } else {
                	props.setProperty("config", "struts-default.xml,struts-plugin.xml,struts.xml");
                }
                
        		Map params = new HashMap(props);
        		dispatcher = new Dispatcher(this.getServletContext(), params);
        		dispatcher.init();
        		Dispatcher.setInstance(dispatcher);
        		dispatcher.prepare(request, response);
        	} else {
        		Dispatcher.setInstance(dispatcher);
        	}
        	
        } else {
        	dispatcher = du;
        }
        
        try {
            // Wrap request first, just in case it is multipart/form-data
            // parameters might not be accessible through before encoding (ww-1278)
            request = dispatcher.wrapRequest(request, this.getServletContext());
        } catch (IOException e) {
            String message = "Could not wrap servlet request with MultipartRequestWrapper!";
            LOG.error(message, e);
            throw new ServletException(message, e);
        }
        return request;
    }
   
    protected static void cleanUp(ServletRequest req) {
        // should we clean up yet?
        Integer count = (Integer) req.getAttribute(COUNTER);
        if (count != null && count > 0 ) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("skipping cleanup counter="+count);
            }
            return;
        }
        // always dontClean up the thread request, even if an action hasn't been executed
        ActionContext.setContext(null);
        Dispatcher.setInstance(null);
    }
    
    private static final String COUNTER = "__cleanup_recursion_counter";

    /**
     * Locate a static resource and copy directly to the response,
     * setting the appropriate caching headers. 
     *
     * @param name The resource name
     * @param request The request
     * @param response The response
     * @throws IOException If anything goes wrong
     */
    protected void findStaticResource(String name, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!name.endsWith(".class")) {
            for (String pathPrefix : pathPrefixes) {
                InputStream is = findInputStream(name, pathPrefix);
                if (is != null) {
                    Calendar cal = Calendar.getInstance();
                    
                    // check for if-modified-since, prior to any other headers
                    long ifModifiedSince = 0;
                    try {
                    	ifModifiedSince = request.getDateHeader("If-Modified-Since");
                    } catch (Exception e) {
                    	LOG.warn("Invalid If-Modified-Since header value: '" + request.getHeader("If-Modified-Since") + "', ignoring");
                    }
    				long lastModifiedMillis = lastModifiedCal.getTimeInMillis();
    				long now = cal.getTimeInMillis();
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    long expires = cal.getTimeInMillis();
                    
    				if (ifModifiedSince > 0 && ifModifiedSince <= lastModifiedMillis) {
    					// not modified, content is not sent - only basic headers and status SC_NOT_MODIFIED
                        response.setDateHeader("Expires", expires);
    					response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    					is.close();
    					return;
    				}
                	
                	// set the content-type header
                    String contentType = getContentType(name);
                    if (contentType != null) {
                        response.setContentType(contentType);
                    }

                    if (serveStaticBrowserCache) {
                    	// set heading information for caching static content
                        response.setDateHeader("Date", now);
                        response.setDateHeader("Expires", expires);
                        response.setDateHeader("Retry-After", expires);
                        response.setHeader("Cache-Control", "public");
                        response.setDateHeader("Last-Modified", lastModifiedMillis);
                    } else {
                        response.setHeader("Cache-Control", "no-cache");
                        response.setHeader("Pragma", "no-cache");
                        response.setHeader("Expires", "-1");
                    }

                    try {
                        copy(is, response.getOutputStream());
                    } finally {
                        is.close();
                    }
                    return;
                }
            }
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Determine the content type for the resource name.
     *
     * @param name The resource name
     * @return The mime type
     */
    protected String getContentType(String name) {
        // NOT using the code provided activation.jar to avoid adding yet another dependency
        // this is generally OK, since these are the main files we server up
        if (name.endsWith(".js")) {
            return "text/javascript";
        } else if (name.endsWith(".css")) {
            return "text/css";
        } else if (name.endsWith(".html")) {
            return "text/html";
        } else if (name.endsWith(".txt")) {
            return "text/plain";
        } else if (name.endsWith(".gif")) {
            return "image/gif";
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (name.endsWith(".png")) {
            return "image/png";
        } else {
            return null;
        }
    }

    /**
     * Copy bytes from the input stream to the output stream.
     *
     * @param input The input stream
     * @param output The output stream
     * @throws IOException If anything goes wrong
     */
    protected void copy(InputStream input, OutputStream output) throws IOException {
        final byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.flush(); // WW-1526
    }

    /**
     * Look for a static resource in the classpath.
     *
     * @param name The resource name
     * @param packagePrefix The package prefix to use to locate the resource
     * @return The inputstream of the resource
     * @throws IOException If there is a problem locating the resource
     */
    protected InputStream findInputStream(String name, String packagePrefix) throws IOException {
        String resourcePath;
        if (packagePrefix.endsWith("/") && name.startsWith("/")) {
            resourcePath = packagePrefix + name.substring(1);
        } else {
            resourcePath = packagePrefix + name;
        }

        resourcePath = URLDecoder.decode(resourcePath, encoding);

        return ClassLoaderUtil.getResourceAsStream(resourcePath, getClass());
    }
	
}
