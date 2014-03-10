/*
*
* Copyright 2014 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2014 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/

package org.entando.entando.apsadmin.filebrowser;

/**
 *
 * @author S.Loru
 */
public interface IFileBrowserAction {
	
	public String edit();
	
	public String list();
	
	public String delete();
	
	public String save();
	
	public String upload();
	
	public String createDir();
	
}
