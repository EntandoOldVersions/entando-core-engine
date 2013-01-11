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
package org.entando.entando.aps.system.init;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import java.io.StringWriter;

import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.entando.entando.aps.system.init.model.IPostProcess;
import org.entando.entando.aps.system.init.model.SelfRestCallPostProcess;
import org.entando.entando.aps.system.services.api.UnmarshalUtils;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.entando.entando.aps.system.services.api.server.IResponseBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author E.Santoboni
 */
public class SelfRestCaller implements IPostProcessor, BeanFactoryAware {
	
	@Override
	public int executePostProcess(IPostProcess postProcess) throws ApsSystemException {
		if (!(postProcess instanceof SelfRestCallPostProcess)) {
			return 0;
		}
		SelfRestCallPostProcess selfRestCall = (SelfRestCallPostProcess) postProcess;
		IResponseBuilder responseBuilder = this.getResponseBuilder();
		ILangManager langManager = this.getLangManager();
        try {
			Object result = null;
			//ApiMethod.HttpMethod method = selfRestCall.getMethod();
			ApiMethod method = responseBuilder.extractApiMethod(selfRestCall.getMethod(), selfRestCall.getNamespace(), selfRestCall.getResourceName());
			String langCode = selfRestCall.getLangCode();
			if (null == langCode || null == langManager.getLang(langCode)) {
				langCode = langManager.getDefaultLang().getCode();
			}
			Properties properties = new Properties();
			if (null != selfRestCall.getQueryParameters()) {
				properties.putAll(selfRestCall.getQueryParameters());
			}
            properties.put(SystemConstants.API_LANG_CODE_PARAMETER, langCode);
			if (method.getHttpMethod().equals(ApiMethod.HttpMethod.GET) || method.getHttpMethod().equals(ApiMethod.HttpMethod.DELETE)) {
				result = responseBuilder.createResponse(method, properties);
			} else {
				Object bodyObject = UnmarshalUtils.unmarshal(method, selfRestCall.getContentBody(), selfRestCall.getContentType());
				result = responseBuilder.createResponse(method, bodyObject, properties);
			}
			this.printResponse(result, method, selfRestCall.isPrintResponse());
        } catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executePostProcess", "Error invoking api method");
			throw new ApsSystemException("Error invoking api method", t);
        }
		return 1;
	}
	
	private void printResponse(Object result, ApiMethod method, boolean printResponse) throws Throwable {
		if (printResponse) {
			String responseClassName = method.getResponseClassName();
			Class responseClass = (null != responseClassName) ? Class.forName(responseClassName) : StringApiResponse.class;
			JAXBContext context = JAXBContext.newInstance(responseClass);
            Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringBuilder log = new StringBuilder();
			StringWriter writer = new StringWriter();
			marshaller.marshal(result, writer);
			log.append("*************** Self Rest Call - response ***************\n");
			log.append(writer.toString()).append("\n");
			log.append("*********************************************************\n");
			ApsSystemUtils.getLogger().info(log.toString());
			System.out.println(log.toString());
		}
	}
	
	protected IResponseBuilder getResponseBuilder() {
		return (IResponseBuilder) this.getBeanFactory().getBean(SystemConstants.API_RESPONSE_BUILDER);
	}
	
	protected ConfigInterface getConfigManager() {
		return (ConfigInterface) this.getBeanFactory().getBean(SystemConstants.BASE_CONFIG_MANAGER);
	}
	
	protected ILangManager getLangManager() {
		return (ILangManager) this.getBeanFactory().getBean(SystemConstants.LANGUAGE_MANAGER);
	}
	
	protected IAuthenticationProviderManager getAuthenticationProvider() {
		return (IAuthenticationProviderManager) this.getBeanFactory().getBean(SystemConstants.AUTHENTICATION_PROVIDER_MANAGER);
	}
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	private BeanFactory _beanFactory;
	
}
