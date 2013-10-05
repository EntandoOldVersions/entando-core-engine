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
package org.entando.entando.aps.system.services.actionlog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.IApsAuthority;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.keygenerator.IKeyGeneratorManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.DateConverter;
import static org.entando.entando.aps.system.services.actionlog.IActionLogManager.LOG_APPENDER_THREAD_NAME_PREFIX;

import org.entando.entando.aps.system.services.actionlog.model.ActionLogRecord;
import org.entando.entando.aps.system.services.actionlog.model.ActivityStreamLikeInfo;
import org.entando.entando.aps.system.services.actionlog.model.IActionLogRecordSearchBean;
import org.entando.entando.aps.system.services.actionlog.model.ManagerConfiguration;
import org.entando.entando.aps.system.services.cache.ICacheInfoManager;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author E.Santoboni - S.Puddu
 */
public class ActionLogManager extends AbstractService implements IActionLogManager {
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": ready");
	}
	
	@Override
	public void addActionRecord(ActionLogRecord actionRecord) throws ApsSystemException {
		try {
			ActionLogAppenderThread thread = new ActionLogAppenderThread(actionRecord, this);
			String threadName = LOG_APPENDER_THREAD_NAME_PREFIX + DateConverter.getFormattedDate(new Date(), "yyyyMMddHHmmss");
			thread.setName(threadName);
			thread.start();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addActionRecord");
			throw new ApsSystemException("Error adding an actionlogger record", t);
		}
	}
	
	protected synchronized void addActionRecordByThread(ActionLogRecord actionRecord) throws ApsSystemException {
		try {
			Integer key = null;
			List<Integer> ids = null;
			do {
				key = this.getKeyGeneratorManager().getUniqueKeyCurrentValue();
				FieldSearchFilter filter = new FieldSearchFilter("id", key, true);
				FieldSearchFilter[] filters = {filter};
				ids = this.getActionLogDAO().getActionRecords(filters);
			} while (!ids.isEmpty());
			actionRecord.setId(key);
			actionRecord.setActionDate(new Date());
			this.getActionLogDAO().addActionRecord(actionRecord);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addActionRecordByThread");
			throw new ApsSystemException("Error adding an actionlogger record", t);
		}
	}
	
	@Override
	@CacheEvict(value = ICacheInfoManager.CACHE_NAME, key = "'ActionLogRecord_'.concat(#id)")
	public void deleteActionRecord(int id) throws ApsSystemException {
		try {
			this.getActionLogDAO().deleteActionRecord(id);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteActionRecord");
			throw new ApsSystemException("Error deleting the actionlogger record: " + id, t);
		}
	}
	
	@Override
	public List<Integer> getActionRecords(IActionLogRecordSearchBean searchBean) throws ApsSystemException {
		List<Integer> records = new ArrayList<Integer>();
		try {
			records = this.getActionLogDAO().getActionRecords(searchBean);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getActionRecords");
			throw new ApsSystemException("Error loading actionlogger records", t);
		}
		return records;
	}
	
	@Override
	public ActionLogRecord getActionRecord(int id) throws ApsSystemException {
		ActionLogRecord record = null;
		try {
			record = this.getActionLogDAO().getActionRecord(id);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getActionRecords");
			throw new ApsSystemException("Error loading actionlogger record with id: " + id, t);
		}
		return record;
	}
	
	@Override
	public List<Integer> getActivityStream(List<String> userGroupCodes) throws ApsSystemException {
		List<Integer> recordIds = null;
		try {
			recordIds = this.getActionLogDAO().getActivityStream(userGroupCodes);
			ManagerConfiguration config = this.getManagerConfiguration();
			if (null != recordIds && null != config && 
					config.getCleanOldActivities() && config.getMaxActivitySizeByGroup() < recordIds.size()) {
				ActivityStreamCleanerThread thread = new ActivityStreamCleanerThread(config.getMaxActivitySizeByGroup(), this.getActionLogDAO());
				String threadName = LOG_CLEANER_THREAD_NAME_PREFIX + DateConverter.getFormattedDate(new Date(), "yyyyMMddHHmmss");
    			thread.setName(threadName);
    			thread.start();
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getActivityStream");
			throw new ApsSystemException("Error loading activity stream records", t);
		}
		return recordIds;
	}
	
	@Override
	@CacheEvict(value = ICacheInfoManager.CACHE_NAME, key = "'ActivityStreamLikeRecords_'.concat(#id)")
	public void editActionLikeRecord(int id, String username, boolean add) throws ApsSystemException {
		try {
			this.getActionLogDAO().editActionLikeRecord(id, username, add);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "editActionLikeRecord");
			throw new ApsSystemException("Error editing activity stream like records", t);
		}
	}
	
	@Override
	@Cacheable(value = ICacheInfoManager.CACHE_NAME, key = "'ActivityStreamLikeRecords_'.concat(#id)")
	public List<ActivityStreamLikeInfo> getActionLikeRecords(int id) throws ApsSystemException {
		List<ActivityStreamLikeInfo> infos = null;
		try {
			infos = this.getActionLogDAO().getActionLikeRecords(id);
			if (null != infos) {
				for (int i = 0; i < infos.size(); i++) {
					ActivityStreamLikeInfo asli = infos.get(i);
					String username = asli.getUsername();
					IUserProfile profile = this.getUserProfileManager().getProfile(username);
					String displayName = (null != profile) ? profile.getDisplayName() : username;
					asli.setDisplayName(displayName);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getActionLikeRecords");
			throw new ApsSystemException("Error extracting activity stream like records", t);
		}
		return infos;
	}
	
	@Override
	public List<Integer> getActivityStream(UserDetails loggedUser) throws ApsSystemException {
		List<String> userGroupCodes = this.extractUserGroupCodes(loggedUser);
		return this.getActivityStream(userGroupCodes);
	}
	
	private List<String> extractUserGroupCodes(UserDetails loggedUser) {
		List<String> codes = new ArrayList<String>();
		IApsAuthority[] autorities = loggedUser.getAuthorities();
		if (null != autorities) {
			for (int i = 0; i < autorities.length; i++) {
				IApsAuthority autority = autorities[i];
				if (autority instanceof Group) {
					codes.add(autority.getAuthority());
				}
			}
		}
		if (!codes.contains(Group.FREE_GROUP_NAME)) {
			codes.add(Group.FREE_GROUP_NAME);
		}
		return codes;
	}
	
	protected ManagerConfiguration getManagerConfiguration() {
		return _managerConfiguration;
	}
	public void setManagerConfiguration(ManagerConfiguration managerConfiguration) {
		this._managerConfiguration = managerConfiguration;
	}
	
	protected IActionLogDAO getActionLogDAO() {
		return _actionLogDAO;
	}
	public void setActionLogDAO(IActionLogDAO actionLogDAO) {
		this._actionLogDAO = actionLogDAO;
	}
	
	protected IKeyGeneratorManager getKeyGeneratorManager() {
		return _keyGeneratorManager;
	}
	public void setKeyGeneratorManager(IKeyGeneratorManager keyGeneratorManager) {
		this._keyGeneratorManager = keyGeneratorManager;
	}
	
	protected IUserProfileManager getUserProfileManager() {
		return _userProfileManager;
	}
	public void setUserProfileManager(IUserProfileManager userProfileManager) {
		this._userProfileManager = userProfileManager;
	}
	
	private ManagerConfiguration _managerConfiguration;
	
	private IActionLogDAO _actionLogDAO;
	private IKeyGeneratorManager _keyGeneratorManager;
	private IUserProfileManager _userProfileManager;
	
}