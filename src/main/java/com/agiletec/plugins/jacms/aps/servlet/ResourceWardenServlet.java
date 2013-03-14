/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.aps.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.AbstractResourceAttribute;
import com.agiletec.plugins.jacms.aps.system.services.dispenser.ContentAuthorizationInfo;
import com.agiletec.plugins.jacms.aps.system.services.dispenser.IContentDispenser;
import com.agiletec.plugins.jacms.aps.system.services.resource.IResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.AbstractMonoInstanceResource;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.AbstractMultiInstanceResource;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInstance;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * This servlet handles the requests for protected resources. 
 * @author E.Santoboni
 */
public class ResourceWardenServlet extends HttpServlet {
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Logger log = ApsSystemUtils.getLogger();
		if (log.isLoggable(Level.FINEST)) {
			log.finest("Request:" + request.getRequestURI());
		}
		//Sintassi /<RES_ID>/<SIZE>/<LANG_CODE>/
		String[] uriSegments = request.getRequestURI().split("/");
		int segments = uriSegments.length;
		//CONTROLLO ASSOCIAZIONE RISORSA A CONTENUTO
		int indexGuardian = 0;
		String checkContentAssociazion = uriSegments[segments-2];
		if (checkContentAssociazion.equals(AbstractResourceAttribute.REFERENCED_RESOURCE_INDICATOR)) {
			// LA Sintassi /<RES_ID>/<SIZE>/<LANG_CODE>/<REFERENCED_RESOURCE_INDICATOR>/<CONTENT_ID>
			indexGuardian = 2;
		}
		String resId = uriSegments[segments-3-indexGuardian];
		UserDetails currentUser = (UserDetails) request.getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
		if (currentUser == null) {
			IUserManager userManager = (IUserManager) ApsWebApplicationUtils.getBean(SystemConstants.USER_MANAGER, request);
			currentUser = userManager.getGuestUser();
		}
		boolean isAuthForProtectedRes = false;
		if (indexGuardian != 0) {
			if (this.isAuthOnProtectedRes(currentUser, resId, uriSegments[segments-1], request)) {
				isAuthForProtectedRes = true;
			} else {
				this.executeLoginRedirect(request, response);
				return;
			}
		}
		IResourceManager resManager = (IResourceManager) ApsWebApplicationUtils.getBean(JacmsSystemConstants.RESOURCE_MANAGER, request);
		IAuthorizationManager authManager = (IAuthorizationManager) ApsWebApplicationUtils.getBean(SystemConstants.AUTHORIZATION_SERVICE, request);
		try {
			ResourceInterface resource = resManager.loadResource(resId);
			if (resource == null) {
				return;
			}
			if (isAuthForProtectedRes 
					|| authManager.isAuthOnGroup(currentUser, resource.getMainGroup()) 
					|| authManager.isAuthOnGroup(currentUser, Group.ADMINS_GROUP_NAME)) {
				ResourceInstance instance = null;
				if (resource.isMultiInstance()) {
					String sizeStr = uriSegments[segments-2-indexGuardian];
					if (!this.isValidNumericString(sizeStr)) {
						return;
					}
					int size = Integer.parseInt(sizeStr);
					String langCode = uriSegments[segments-1-indexGuardian];
					instance = ((AbstractMultiInstanceResource) resource).getInstance(size, langCode);
				} else {
					instance = ((AbstractMonoInstanceResource) resource).getInstance();
				}
				this.createResponse(response, resource, instance);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "service");
			throw new ServletException("Error extracting protected resource", t);
		}
	}
	
	private boolean isAuthOnProtectedRes(UserDetails currentUser, String resourceId, String contentId, HttpServletRequest request) {
		IContentDispenser dispender = (IContentDispenser) ApsWebApplicationUtils.getBean(JacmsSystemConstants.CONTENT_DISPENSER_MANAGER, request);
		ContentAuthorizationInfo authInfo = dispender.getAuthorizationInfo(contentId);
		IAuthorizationManager authManager = (IAuthorizationManager) ApsWebApplicationUtils.getBean(SystemConstants.AUTHORIZATION_SERVICE, request);
		return (authInfo.isProtectedResourceReference(resourceId) && authInfo.isUserAllowed(authManager.getUserGroups(currentUser)));
	}
	
	private void createResponse(HttpServletResponse resp, ResourceInterface resource, 
			ResourceInstance instance) throws IOException, ServletException {
		resp.setContentType(instance.getMimeType());
		resp.setHeader("Content-Disposition","inline; filename=" + instance.getFileName());
		ServletOutputStream out = resp.getOutputStream();
		try {
			File fileTemp = new File(resource.getDiskFolder() + instance.getFileName());
			if (fileTemp.exists()) {
				InputStream is = new FileInputStream(fileTemp);
				byte[] buffer = new byte[2048];
				int length = -1;
			    // Transfer the data
			    while ((length = is.read(buffer)) != -1) {
			    	out.write(buffer, 0, length);
			    	out.flush();
			    }
				is.close();
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createResponse");
			throw new ServletException("Error extracting protected resource", t);
		} finally {
			out.close();
		}
	}
	
	private void executeLoginRedirect(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		ConfigInterface configManager = (ConfigInterface) ApsWebApplicationUtils.getBean(SystemConstants.BASE_CONFIG_MANAGER, request);
		IURLManager urlManager = (IURLManager) ApsWebApplicationUtils.getBean(SystemConstants.URL_MANAGER, request);
		IPageManager pageManager = (IPageManager) ApsWebApplicationUtils.getBean(SystemConstants.PAGE_MANAGER, request);
		ILangManager langManager = (ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, request);
		try {
			StringBuilder targetUrl = new StringBuilder(request.getRequestURL());
			Map<String, String> params = new HashMap<String, String>();
			params.put("returnUrl", URLEncoder.encode(targetUrl.toString(), "UTF-8"));
			String loginPageCode = configManager.getParam(SystemConstants.CONFIG_PARAM_LOGIN_PAGE_CODE);
			IPage page = pageManager.getPage(loginPageCode);
			String url = urlManager.createUrl(page, langManager.getDefaultLang(), params);
			response.sendRedirect(url);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeLoginRedirect");
			throw new ServletException("Error executing redirect login page", t);
		}
	}
	
	private boolean isValidNumericString(String integerNumber) {
		return (integerNumber.trim().length() > 0 && integerNumber.matches("\\d+"));
	}
	
}