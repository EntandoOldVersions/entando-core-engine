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
package org.entando.entando.aps.system.services.guifragment;

import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;

import com.agiletec.aps.system.common.FieldSearchFilter;

public interface IGuiFragmentManager {
	
	public GuiFragment getGuiFragment(String code) throws ApsSystemException;
	
	public List<String> getGuiFragments() throws ApsSystemException;
	
	public List<String> searchGuiFragments(FieldSearchFilter filters[]) throws ApsSystemException;
	
	public void addGuiFragment(GuiFragment guiFragment) throws ApsSystemException;
	
	public void updateGuiFragment(GuiFragment guiFragment) throws ApsSystemException;
	
	public void deleteGuiFragment(String code) throws ApsSystemException;
	
}