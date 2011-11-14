/*
*
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
* This file is part of jAPS software.
* jAPS is a free software;
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
*
* See the file License for the specific language governing permissions
* and limitations under the License
*
*
*
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package org.entando.entando.aps.system.services.api.server;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.entando.entando.aps.system.services.api.IApiCatalogManager;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.AbstractApiResponse;
import org.entando.entando.aps.system.services.api.model.ApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethodParameter;
import org.entando.entando.aps.system.services.api.model.ApiMethodResult;
import org.entando.entando.aps.system.services.api.model.BaseApiResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.renderer.IVelocityRenderer;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.aps.util.FileTextReader;

/**
 * @author E.Santoboni
 */
public class ResponseBuilder implements IResponseBuilder, BeanFactoryAware, ServletContextAware {

	@Override
	public Object createResponse(String methodName, Properties parameters) throws ApsSystemException {
		Object apiResponse = null;
		try {
			ApiMethod method = this.extractApi(methodName);
			apiResponse = this.createResponse(method, parameters);
		} catch (ApiException e) {
			ApsSystemUtils.logThrowable(e, this, "createResponse", "Error creating response for api method '" + methodName + "'");
			if (apiResponse == null) apiResponse = new BaseApiResponse();
			((AbstractApiResponse) apiResponse).addErrors(e.getErrors());
		}
		return apiResponse;
	}

	@Override
	public Object createResponse(ApiMethod method, Properties parameters) throws ApsSystemException {
		AbstractApiResponse apiResponse = null;
		try {
			this.checkParameter(method, parameters);
			Object bean = this.extractBean(method);
			Object masterResult = this.invokeMethod(method, bean, null, parameters, true);
			if (null == method.getResponseClassName()) {
				return masterResult;
			}
			if (masterResult instanceof String) {
				return masterResult;
			}
			apiResponse = this.buildApiResponseObject(method);
			String htmlResult = this.extractHtmlResult(masterResult, apiResponse, method, parameters, bean);
			if (masterResult instanceof ApiMethodResult) {
				apiResponse.addErrors(((ApiMethodResult) masterResult).getErrors());
				apiResponse.setResult(((ApiMethodResult) masterResult).getResult(), htmlResult);
			} else {
				apiResponse.setResult(masterResult, htmlResult);
			}
		} catch (ApiException e) {
			ApsSystemUtils.logThrowable(e, this, "createResponse", "Error creating response for api method '" + method.getMethodName() + "'");
			if (apiResponse == null) apiResponse = new BaseApiResponse();
			apiResponse.addErrors(e.getErrors());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createResponse", "Error creating response for api method '" + method.getMethodName() + "'");
			if (apiResponse == null) apiResponse = new BaseApiResponse();
			ApiError error = new ApiError(IApiErrorCodes.API_METHOD_ERROR, "Error creating response for api method '" + method.getMethodName() + "'");
			apiResponse.addError(error);
		}
		return apiResponse;
	}

	private String extractHtmlResult(Object masterResult, 
			AbstractApiResponse apiResponse, ApiMethod api, Properties parameters, Object bean) {
		String htmlResult = null;
		try {
			htmlResult = (String) this.invokeMethod(api, bean, "ToHtml", parameters, false);
			if (null != htmlResult) return htmlResult;
			String template = this.extractTemplate(api);
			if (null == template) return null;
			htmlResult = this.getVelocityRenderer().render(masterResult, template);
		} catch (ApiException t) {
			ApsSystemUtils.logThrowable(t, this, "extractHtmlResult",
					"Error creating html response for api method '" + api.getMethodName() + "'");
			if (null != t.getErrors()) {
				apiResponse.addErrors(t.getErrors());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractHtmlResult",
					"Error creating html response for api method '" + api.getMethodName() + "'");
		}
		return htmlResult;
	}
	
	protected String extractTemplate(ApiMethod api) throws Exception {
		String template = null;
		InputStream is = null;
		try {
			StringBuffer path = new StringBuffer("classpath*:/api/");
			if (null != api.getPluginCode()) {
				path.append("plugins/" + api.getPluginCode() + "/");
			} else if (!api.getSource().equalsIgnoreCase("core")) {
				path.append(api.getSource() + "/");
			}
			path.append("aps/get/" + api.getMethodName() + "/description-item.vm");
			Resource[] resources = ApsWebApplicationUtils.getResources(path.toString(), this.getServletContext());
			if (null != resources && resources.length == 1) {
				Resource resource = resources[0];
				is = resource.getInputStream();
			}
			if (null == is) {
				ApsSystemUtils.getLogger().severe("Null Input Stream - template file path " + path.toString());
				return null;
			}
			template = FileTextReader.getText(is);
		} catch (Throwable t) {
			String message = "Error template for api '" + api.getMethodName() + "'";
			ApsSystemUtils.logThrowable(t, this, "extractTemplate", message);
		} finally {
			if (null != is) {
				is.close();
			}
		}
		return template;
	}

	private void checkParameter(ApiMethod api, Properties parameters) throws ApiException, Throwable {
		try {
			List<ApiMethodParameter> apiParameters = api.getParameters();
			if (null == apiParameters || apiParameters.isEmpty()) return;
			List<ApiError> errors = new ArrayList<ApiError>();
			for (int i = 0; i < apiParameters.size(); i++) {
				ApiMethodParameter apiParam = apiParameters.get(i);
				String paramName = apiParam.getKey();
				Object value = parameters.get(paramName);
				if (apiParam.isRequired() && (null == value || value.toString().trim().length() == 0)) {
					errors.add(new ApiError(IApiErrorCodes.API_PARAMETER_REQUIRED, "Parameter '" + paramName + "' is required"));
				}
			}
			if (!errors.isEmpty()) {
				throw new ApiException(errors);
			}
		} catch (ApiException t) {
			throw t;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "checkParameter", "Error checking api parameters");
			throw new ApsSystemException("Internal Error", t);
		}
	}

	private AbstractApiResponse buildApiResponseObject(ApiMethod api) throws ApiException {
		AbstractApiResponse apiResponse = null;
		try {
			Class responseClass = Class.forName(api.getResponseClassName());
			apiResponse = (AbstractApiResponse) responseClass.newInstance();
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "createResponse",
					"Error creating instance of response '" + api.getResponseClassName() + "'");
			throw new ApiException(IApiErrorCodes.INVALID_RESPONSE, "Invalid response class '" + api.getResponseClassName() + "'");
		}
		return apiResponse;
	}

	@Override
	public Object invoke(String methodName, Properties parameters) throws ApiException, ApsSystemException {
		Object result = null;
		try {
			ApiMethod api = this.extractApi(methodName);
			this.checkParameter(api, parameters);
			Object bean = this.extractBean(api);
			result = this.invokeMethod(api, bean, "", parameters, true);
		} catch (ApiException ae) {
			ApsSystemUtils.logThrowable(ae, this, "invoke", "Error invoking api method '" + methodName + "'");
			throw ae;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "invoke", "Error invoking api method '" + methodName + "'");
			throw new ApsSystemException("Error invoking api method '" + methodName + "'", t);
		}
		return result;
	}

	protected ApiMethod extractApi(String methodName) throws ApiException {
		ApiMethod api = null;
		try {
			api = this.getApiCatalogManager().getMethod(methodName);
			if (null == api) {
				throw new ApiException(IApiErrorCodes.API_INVALID, "Method '" + methodName + "' does not exist");
			}
			if (!api.isActive()) {
				throw new ApiException(IApiErrorCodes.API_ACTIVE_FALSE, "Method '" + methodName + "' is not active");
			}
		} catch (ApiException ae) {
			ApsSystemUtils.logThrowable(ae, this, "extractApi", "Error extracting api method '" + methodName + "'");
			throw ae;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractApi", "Error extracting api method '" + methodName + "'");
			throw new ApiException(IApiErrorCodes.SERVER_ERROR, "Method '" + methodName + "' is not supported");
		}
		return api;
	}

	protected Object extractBean(ApiMethod api) throws ApsSystemException, ApiException {
		Object bean = this.getBeanFactory().getBean(api.getSpringBean());
		if (null == bean) {
			ApsSystemUtils.getLogger().severe("Null bean '" + api.getSpringBean() + "' for api '" + api.getMethodName() + "'");
			throw new ApiException(IApiErrorCodes.SERVER_ERROR, "Method '" + api.getMethodName() + "' is not supported");
		}
		return bean;
	}

	protected Object invokeMethod(ApiMethod api, Object bean,
			String methodSuffix, Properties parameters, boolean throwException) throws ApiException, Throwable {
		String methodName = null;
		Object result = null;
		try {
			Class[] parameterTypes = new Class[] {Properties.class};
			Class beanClass = bean.getClass();
			methodName = (null != methodSuffix) ? api.getSpringBeanMethod() + methodSuffix.trim() : api.getSpringBeanMethod();
			Method method = beanClass.getDeclaredMethod(methodName, parameterTypes);
			result = method.invoke(bean, parameters);
		} catch (NoSuchMethodException e) {
			if (throwException) {
				ApsSystemUtils.logThrowable(e, this, "extractApi", "No such method '"
						+ methodName + "' of class '" + bean.getClass() + "'");
				throw new ApiException(IApiErrorCodes.API_METHOD_ERROR, "Method '" + api.getMethodName() + "' is not supported");
			}
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof ApiException) {
				throw (ApiException) e.getTargetException();
			} else if (throwException) {
				ApsSystemUtils.logThrowable(e.getTargetException(), this, "extractApi", "Error invoking method '"
						+ methodName + "' of class '" + bean.getClass() + "'");
				throw new ApiException(IApiErrorCodes.API_METHOD_ERROR, "Error invoking Method '" + api.getMethodName() + "'");
			}
		} catch (Throwable t) {
			if (throwException) {
				ApsSystemUtils.logThrowable(t, this, "extractApi", "Error invoking method '"
						+ methodName + "' of class '" + bean.getClass() + "'");
				throw t;
			}
		}
		return result;
	}
	
	protected IApiCatalogManager getApiCatalogManager() {
		return _apiCatalogManager;
	}
	public void setApiCatalogManager(IApiCatalogManager apiCatalogManager) {
		this._apiCatalogManager = apiCatalogManager;
	}

	protected IVelocityRenderer getVelocityRenderer() {
		return _velocityRenderer;
	}
	public void setVelocityRenderer(IVelocityRenderer velocityRenderer) {
		this._velocityRenderer = velocityRenderer;
	}

	protected BeanFactory getBeanFactory() {
		return this._beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}

	protected ServletContext getServletContext() {
		return this._servletContext;
	}
	@Override
	public void setServletContext(ServletContext servletContext) {
		this._servletContext = servletContext;
	}
	
	private IApiCatalogManager _apiCatalogManager;
	private IVelocityRenderer _velocityRenderer;
	private BeanFactory _beanFactory;
	private ServletContext _servletContext;
	
}