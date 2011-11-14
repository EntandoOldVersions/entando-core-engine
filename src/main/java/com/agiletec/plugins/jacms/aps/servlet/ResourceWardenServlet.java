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
import com.agiletec.aps.system.services.group.Group;
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
		if (currentUser == null) return;
		
		boolean isAuthForProtectedRes = false;
		if (indexGuardian != 0) {
			if (this.isAuthOnProtectedRes(currentUser, resId, uriSegments[segments-1], request)) {
				isAuthForProtectedRes = true;
			} else {
				return;
			}
		}
		IResourceManager resManager = (IResourceManager) ApsWebApplicationUtils.getBean(JacmsSystemConstants.RESOURCE_MANAGER, request);
		IAuthorizationManager authManager = (IAuthorizationManager) ApsWebApplicationUtils.getBean(SystemConstants.AUTHORIZATION_SERVICE, request);
		try {
			ResourceInterface resource = resManager.loadResource(resId);
			if (resource == null) return;
			if (isAuthForProtectedRes 
					|| authManager.isAuthOnGroup(currentUser, resource.getMainGroup()) 
					|| authManager.isAuthOnGroup(currentUser, Group.ADMINS_GROUP_NAME)) {
				ResourceInstance instance = null;
				if (resource.isMultiInstance()) {
					String sizeStr = uriSegments[segments-2-indexGuardian];
					if (!this.isValidNumericString(sizeStr)) return;
					int size = Integer.parseInt(sizeStr);
					String langCode = uriSegments[segments-1-indexGuardian];
					instance = ((AbstractMultiInstanceResource) resource).getInstance(size, langCode);
				} else {
					instance = ((AbstractMonoInstanceResource) resource).getInstance();
				}
				this.createResponse(response, resource, instance);
			}
		} catch (Throwable t) {
			throw new ServletException("Errore in erogazione risorsa protetta", t);
		}
	}
	
	private boolean isAuthOnProtectedRes(UserDetails currentUser, String resourceId, String contentId, HttpServletRequest request) {
		IContentDispenser dispender = (IContentDispenser) ApsWebApplicationUtils.getBean(JacmsSystemConstants.CONTENT_DISPENSER_MANAGER, request);
		ContentAuthorizationInfo authInfo = dispender.getAuthorizationInfo(contentId);
		IAuthorizationManager authManager = (IAuthorizationManager) ApsWebApplicationUtils.getBean(SystemConstants.AUTHORIZATION_SERVICE, request);
		return (authInfo.isProtectedResourceReference(resourceId) && authInfo.isUserAllowed(authManager.getGroupsOfUser(currentUser)));
	}
	
	private void createResponse(HttpServletResponse resp, ResourceInterface resource, 
			ResourceInstance instance) throws IOException, ServletException {
		resp.setContentType(instance.getMimeType());
		resp.setHeader("Content-Disposition","inline; filename="+instance.getFileName());
		ServletOutputStream out = resp.getOutputStream();
		try {
			File fileTemp = new File(resource.getDiskFolder() + instance.getFileName());
			if (fileTemp.exists()) {
				InputStream is = new FileInputStream(fileTemp);
				byte[] buffer = new byte[8789];
				int length = -1;
				
			    // Transfer the data
			    while ((length = is.read(buffer)) != -1) {
			    	out.write(buffer, 0, length);
			    	out.flush();
			    }
				is.close();
			}
		} catch (Throwable t) {
			throw new ServletException("Errore in erogazione risorsa protetta", t);
		} finally {
			out.close();
		}
	}
	
	private boolean isValidNumericString(String integerNumber) {
		return (integerNumber.trim().length() > 0 && integerNumber.matches("\\d+"));
	}

}
