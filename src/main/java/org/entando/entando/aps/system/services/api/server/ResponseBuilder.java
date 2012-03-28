/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software;
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
*
* See the file License for the specific language governing permissions
* and limitations under the License
*
*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
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
    
    @Deprecated
    public Object createResponse(String resourceName, Properties parameters) throws ApsSystemException {
        Object apiResponse = null;
        try {
            ApiMethod method = this.extractApiMethod(ApiMethod.HttpMethod.GET, resourceName);
            apiResponse = this.createResponse(method, parameters);
        } catch (ApiException e) {
            ApsSystemUtils.logThrowable(e, this, "createResponse", "Error creating response for method GET, resource '" + resourceName + "'");
            if (apiResponse == null) {
                apiResponse = new StringApiResponse();
            }
            ((AbstractApiResponse) apiResponse).addErrors(e.getErrors());
        }
        return apiResponse;
    }
    
    public Object createResponse(ApiMethod method, Properties parameters) throws ApsSystemException {
        return createResponse(method, null, parameters);
    }
    
    public Object createResponse(ApiMethod method, Object bodyObject, Properties parameters) throws ApsSystemException {
        AbstractApiResponse response = null;
        try {
            this.checkParameter(method, parameters);
            Object bean = this.extractBean(method);
            Object masterResult = null;
            if (method.getHttpMethod().equals(ApiMethod.HttpMethod.GET)) {
                masterResult = this.invokeGetMethod(method, bean, null, parameters, true);
                if (null == masterResult) {
                    throw new ApiException(IApiErrorCodes.API_INVALID_RESPONSE, "Invalid or null Response");
                }
            } else {
                masterResult = this.invokePutPostDeleteMethod(method, bean, parameters, bodyObject);
            }
            if (null == method.getResponseClassName()) {
                return masterResult;
            }
            response = this.buildApiResponseObject(method);
            if (null == response && (masterResult instanceof String)) {
                return masterResult;
            }
            String htmlResult = this.extractHtmlResult(masterResult, response, method, parameters, bean);
            if (masterResult instanceof ApiMethodResult) {
                response.addErrors(((ApiMethodResult) masterResult).getErrors());
                response.setResult(((ApiMethodResult) masterResult).getResult(), htmlResult);
            } else {
                response.setResult(masterResult, htmlResult);
            }
        } catch (ApiException e) {
            if (response == null) {
                response = new StringApiResponse();
            }
            response.addErrors(e.getErrors());
            response.setResult(FAILURE, null);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "createResponse", "Error creating response - " + this.buildApiSignature(method));
            if (response == null) {
                response = new StringApiResponse();
            }
            ApiError error = new ApiError(IApiErrorCodes.API_METHOD_ERROR, "Error creating response - " + this.buildApiSignature(method));
            response.addError(error);
            response.setResult(FAILURE, null);
        }
        return response;
    }

    private String extractHtmlResult(Object masterResult,
            AbstractApiResponse apiResponse, ApiMethod apiMethod, Properties parameters, Object bean) {
        String htmlResult = null;
        try {
            htmlResult = (String) this.invokeGetMethod(apiMethod, bean, "ToHtml", parameters, false);
            if (null != htmlResult) {
                return htmlResult;
            }
            String template = this.extractTemplate(apiMethod);
            if (null == template) {
                return null;
            }
            htmlResult = this.getVelocityRenderer().render(masterResult, template);
        } catch (ApiException t) {
            ApsSystemUtils.logThrowable(t, this, "extractHtmlResult",
                    "Error creating html response - " + this.buildApiSignature(apiMethod));
            if (null != t.getErrors()) {
                apiResponse.addErrors(t.getErrors());
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "extractHtmlResult",
                    "Error creating html response - " + this.buildApiSignature(apiMethod));
        }
        return htmlResult;
    }

    protected String extractTemplate(ApiMethod apiMethod) throws Exception {
        String template = null;
        InputStream is = null;
        try {
            StringBuffer path = new StringBuffer("classpath*:/api/");
            if (null != apiMethod.getPluginCode()) {
                path.append("plugins/" + apiMethod.getPluginCode() + "/");
            } else if (!apiMethod.getSource().equalsIgnoreCase("core")) {
                path.append(apiMethod.getSource() + "/");
            }
            path.append("aps/get/" + apiMethod.getResourceName() + "/description-item.vm");
            Resource[] resources = ApsWebApplicationUtils.getResources(path.toString(), this.getServletContext());
            if (null != resources && resources.length == 1) {
                Resource resource = resources[0];
                is = resource.getInputStream();
            }
            if (null == is) {
                ApsSystemUtils.getLogger().info("Null Input Stream - template file path " + path.toString());
                return null;
            }
            template = FileTextReader.getText(is);
        } catch (Throwable t) {
            String message = "Error extracting template - " + this.buildApiSignature(apiMethod);
            ApsSystemUtils.logThrowable(t, this, "extractTemplate", message);
        } finally {
            if (null != is) {
                is.close();
            }
        }
        return template;
    }

    private void checkParameter(ApiMethod apiMethod, Properties parameters) throws ApiException, Throwable {
        try {
            List<ApiMethodParameter> apiParameters = apiMethod.getParameters();
            if (null == apiParameters || apiParameters.isEmpty()) {
                return;
            }
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

    private AbstractApiResponse buildApiResponseObject(ApiMethod apiMethod) throws ApiException {
        AbstractApiResponse apiResponse = null;
        try {
            Class responseClass = Class.forName(apiMethod.getResponseClassName());
            apiResponse = (AbstractApiResponse) responseClass.newInstance();
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "createResponse",
                    "Error creating instance of response '" + apiMethod.getResponseClassName() + "'");
            //throw new ApiException(IApiErrorCodes.INVALID_RESPONSE, "Invalid response class '" + api.getResponseClassName() + "'");
        }
        return apiResponse;
    }
    
    @Deprecated
    public Object invoke(String resourceName, Properties parameters) throws ApiException, ApsSystemException {
        Object result = null;
        try {
            ApiMethod api = this.extractApi(resourceName);
            this.checkParameter(api, parameters);
            Object bean = this.extractBean(api);
            result = this.invokeGetMethod(api, bean, "", parameters, true);
        } catch (ApiException ae) {
            ApsSystemUtils.logThrowable(ae, this, "invoke", "Error invoking method GET for resource '" + resourceName + "'");
            throw ae;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "invoke", "Error invoking method GET for resource '" + resourceName + "'");
            throw new ApsSystemException("Error invoking method GET for resource '" + resourceName + "'", t);
        }
        return result;
    }
    
    @Deprecated
    protected ApiMethod extractApi(String resourceName) throws ApiException {
        return this.extractApiMethod(ApiMethod.HttpMethod.GET, resourceName);
    }

    public ApiMethod extractApiMethod(ApiMethod.HttpMethod httpMethod, String resourceName) throws ApiException {
        ApiMethod api = null;
        try {
            api = this.getApiCatalogManager().getMethod(httpMethod, resourceName);
            if (null == api) {
                throw new ApiException(IApiErrorCodes.API_INVALID, this.buildApiSignature(httpMethod, resourceName) + " does not exists");
            }
            if (!api.isActive()) {
                throw new ApiException(IApiErrorCodes.API_ACTIVE_FALSE, this.buildApiSignature(httpMethod, resourceName) + " is not active");
            }
        } catch (ApiException ae) {
            ApsSystemUtils.logThrowable(ae, this, "extractApi", "Error extracting api method " + this.buildApiSignature(httpMethod, resourceName));
            throw ae;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "extractApi", "Error extracting api method - " + this.buildApiSignature(httpMethod, resourceName));
            throw new ApiException(IApiErrorCodes.SERVER_ERROR, this.buildApiSignature(httpMethod, resourceName) + " is not supported");
        }
        return api;
    }
    
    private String buildApiSignature(ApiMethod apiMethod) {
        return this.buildApiSignature(apiMethod.getHttpMethod(), apiMethod.getResourceName());
    }
    
    private String buildApiSignature(ApiMethod.HttpMethod httpMethod, String resourceName) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Method '").append(httpMethod.toString()).append("' Resource '").append(resourceName).append("'");
        return buffer.toString();
    }
    
    protected Object extractBean(ApiMethod api) throws ApsSystemException, ApiException {
        Object bean = this.getBeanFactory().getBean(api.getSpringBean());
        if (null == bean) {
            ApsSystemUtils.getLogger().severe("Null bean '" + api.getSpringBean() + "' for api " + this.buildApiSignature(api));
            throw new ApiException(IApiErrorCodes.SERVER_ERROR, this.buildApiSignature(api) + " is not supported");
        }
        return bean;
    }
    
    @Deprecated
    protected Object invokeMethod(ApiMethod api, Object bean,
            String methodSuffix, Properties parameters, boolean throwException) throws ApiException, Throwable {
        return this.invokeGetMethod(api, bean, methodSuffix, parameters, throwException);
    }
    
    protected Object invokeGetMethod(ApiMethod apiMethod, Object bean,
            String methodSuffix, Properties parameters, boolean throwException) throws ApiException, Throwable {
        String methodName = null;
        Object result = null;
        try {
            Class[] parameterTypes = new Class[]{Properties.class};
            Class beanClass = bean.getClass();
            methodName = (null != methodSuffix) ? apiMethod.getSpringBeanMethod() + methodSuffix.trim() : apiMethod.getSpringBeanMethod();
            Method method = beanClass.getDeclaredMethod(methodName, parameterTypes);
            result = method.invoke(bean, parameters);
        } catch (NoSuchMethodException e) {
            if (throwException) {
                ApsSystemUtils.logThrowable(e, this, "invokeGetMethod", "No such method '"
                        + methodName + "' of class '" + bean.getClass() + "'");
                throw new ApiException(IApiErrorCodes.API_METHOD_ERROR, "Method not supported - " + this.buildApiSignature(apiMethod));
            }
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof ApiException) {
                throw (ApiException) e.getTargetException();
            } else if (throwException) {
                ApsSystemUtils.logThrowable(e.getTargetException(), this, "invokeGetMethod", "Error invoking method '"
                        + methodName + "' of class '" + bean.getClass() + "'");
                throw new ApiException(IApiErrorCodes.API_METHOD_ERROR, "Error invoking Method - " + this.buildApiSignature(apiMethod));
            }
        } catch (Throwable t) {
            if (throwException) {
                ApsSystemUtils.logThrowable(t, this, "invokeGetMethod", "Error invoking method - " + this.buildApiSignature(apiMethod)
                        + methodName + "' of class '" + bean.getClass() + "'");
                throw t;
            }
        }
        return result;
    }
    
    protected Object invokePutPostDeleteMethod(ApiMethod apiMethod, Object bean,
            Properties parameters, Object bodyObject) throws ApiException, Throwable {
        Object result = null;
        try {
            if (apiMethod.getHttpMethod().equals(ApiMethod.HttpMethod.DELETE)) {
                result = this.invokeDeleteMethod(apiMethod, bean, parameters);
            } else {
                result = this.invokePutPostMethod(apiMethod, bean, parameters, bodyObject);
            }
            if (null != result) return result;
            StringApiResponse response = new StringApiResponse();
            response.setResult(SUCCESS, null);
            result = response;
        } catch (NoSuchMethodException e) {
            ApsSystemUtils.logThrowable(e, this, "invokePutPostDeleteMethod", "No such method '"
                    + apiMethod.getSpringBeanMethod() + "' of class '" + bean.getClass() + "'");
            throw new ApiException(IApiErrorCodes.API_METHOD_ERROR, "Method not supported - " + this.buildApiSignature(apiMethod));
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof ApiException) {
                throw (ApiException) e.getTargetException();
            } else {
                ApsSystemUtils.logThrowable(e.getTargetException(), this, "invokePutPostDeleteMethod", "Error invoking method '"
                        + apiMethod.getSpringBeanMethod() + "' of class '" + bean.getClass() + "'");
                throw new ApiException(IApiErrorCodes.API_METHOD_ERROR, "Error invoking Method - " + this.buildApiSignature(apiMethod));
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "invokePutPostDeleteMethod", "Error invoking method '"
                    + apiMethod.getSpringBeanMethod() + "' of class '" + bean.getClass() + "'");
            throw t;
        }
        return result;
    }
    
    private Object invokePutPostMethod(ApiMethod api, Object bean, 
            Properties parameters, Object bodyObject) throws NoSuchMethodException, InvocationTargetException, Throwable {
        Object result = null;
        Class beanClass = bean.getClass();
        String methodName = api.getSpringBeanMethod();
        try {
            Class[] parameterTypes = new Class[]{bodyObject.getClass(), Properties.class};
            Method method = beanClass.getDeclaredMethod(methodName, parameterTypes);
            result = method.invoke(bean, bodyObject, parameters);
        } catch (NoSuchMethodException e) {
            //the first exception of type "NoSuchMethodException" will not catched... the second yes
            Class[] parameterTypes = new Class[]{bodyObject.getClass()};
            Method method = beanClass.getDeclaredMethod(methodName, parameterTypes);
            result = method.invoke(bean, bodyObject);
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Throwable t) {
            throw t;
        }
        return result;
    }
    
    private Object invokeDeleteMethod(ApiMethod api, Object bean, 
            Properties parameters) throws NoSuchMethodException, InvocationTargetException, Throwable {
        Class[] parameterTypes = new Class[]{Properties.class};
        Class beanClass = bean.getClass();
        String methodName = api.getSpringBeanMethod();
        Method method = beanClass.getDeclaredMethod(methodName, parameterTypes);
        return method.invoke(bean, parameters);
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
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this._beanFactory = beanFactory;
    }

    protected ServletContext getServletContext() {
        return this._servletContext;
    }
    public void setServletContext(ServletContext servletContext) {
        this._servletContext = servletContext;
    }
    
    private IApiCatalogManager _apiCatalogManager;
    private IVelocityRenderer _velocityRenderer;
    private BeanFactory _beanFactory;
    private ServletContext _servletContext;
    
}