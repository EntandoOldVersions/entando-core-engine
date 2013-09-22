/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.apsadmin.common.currentuser;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;

/**
 * @author E.Santoboni
 */
public class CurrentAvatarAction extends BaseAction /* implements ServletResponseAware */{
	
	public String returnAvatarStream() {
		try {
			UserDetails currentUser = super.getCurrentUser();
			IUserProfile profile = (null != currentUser && null != currentUser.getProfile()) 
					? (IUserProfile) currentUser.getProfile() 
					: null;
			if (null == profile) {
				return this.extractDefaultAvatarStream();
			}
			String email = (String) profile.getValueByRole(SystemConstants.USER_PROFILE_ATTRIBUTE_ROLE_MAIL);
			if (null == email) {
				return this.extractDefaultAvatarStream();
			}
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(this.createGravatarUri(email));
			HttpResponse response = httpclient.execute(httpGet);
			Integer i = response.getStatusLine().getStatusCode();
			if (i == 404) {
				return this.extractDefaultAvatarStream();
			}
			this.setInputStream(response.getEntity().getContent());
		} catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "returnAvatarStream");
			return this.extractDefaultAvatarStream();
        }
		return SUCCESS;
	}
	
	private String createGravatarUri(String emailAddress) throws Throwable {
		StringBuilder sb = new StringBuilder(this.getGravatarUrl());
		sb.append(this.createMd5Hex(emailAddress));
		sb.append(".").append(this.getGravatarExtension()).append("?d=404&s=").append(this.getGravatarSize());
		return sb.toString();
	}
	
	private String createMd5Hex(String emailAddress) throws Throwable {
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(emailAddress.getBytes("CP1252"));
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));        
			}
		} catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "createMd5Hex");
			throw new RuntimeException("Error creating Md5 Hex", t);
		}
		return sb.toString();
	}
	
	protected String extractDefaultAvatarStream() {
		try {
			ConfigInterface configManager = (ConfigInterface) ApsWebApplicationUtils.getBean(SystemConstants.BASE_CONFIG_MANAGER, this.getRequest());
			String url = configManager.getParam(SystemConstants.PAR_RESOURCES_ROOT_URL) + this.getDefaultAvatarSubPath();
			InputStream is = new URL(url).openStream();
			if (null == is) {
				this.setInputStream(new ByteArrayInputStream(url.getBytes("UTF-8")));
			}
			this.setInputStream(is);
		} catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "extractDefaultAvatarStream");
            return FAILURE;
        }
		return SUCCESS;
	}
	
	protected String getGravatarUrl() {
		return _gravatarUrl;
	}
	public void setGravatarUrl(String gravatarUrl) {
		this._gravatarUrl = gravatarUrl;
	}
	
	protected String getDefaultAvatarSubPath() {
		return _defaultAvatarSubPath;
	}
	public void setDefaultAvatarSubPath(String defaultAvatarSubPath) {
		this._defaultAvatarSubPath = defaultAvatarSubPath;
	}
	
	public String getGravatarExtension() {
		return _gravatarExtension;
	}
	public void setGravatarExtension(String gravatarExtension) {
		this._gravatarExtension = gravatarExtension;
	}
	
	public String getGravatarSize() {
		return _gravatarSize;
	}
	public void setGravatarSize(String gravatarSize) {
		this._gravatarSize = gravatarSize;
	}
	
	public InputStream getInputStream() {
		return _inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this._inputStream = inputStream;
	}
	
	protected ConfigInterface getConfigManager() {
		return _configManager;
	}
	public void setConfigManager(ConfigInterface configManager) {
		this._configManager = configManager;
	}
	
	private String _gravatarUrl;
	private String _defaultAvatarSubPath;
	
	private String _gravatarExtension = "png";
	private String _gravatarSize = "34";
	
	private InputStream _inputStream;
	
	private ConfigInterface _configManager;
	
}