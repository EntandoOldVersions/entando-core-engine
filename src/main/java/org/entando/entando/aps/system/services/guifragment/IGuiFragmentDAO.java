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

import com.agiletec.aps.system.common.FieldSearchFilter;

public interface IGuiFragmentDAO {
	
	public List<String> searchGuiFragments(FieldSearchFilter[] filters);
	
	public GuiFragment loadGuiFragment(String code);
	
	public List<String> loadGuiFragments();
	
	public void removeGuiFragment(String code);
	
	public void updateGuiFragment(GuiFragment guiFragment);
	
	public void insertGuiFragment(GuiFragment guiFragment);
	
}