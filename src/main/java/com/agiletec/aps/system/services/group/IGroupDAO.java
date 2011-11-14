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
package com.agiletec.aps.system.services.group;

import java.util.Map;

import com.agiletec.aps.system.services.authorization.authorizator.IApsAuthorityDAO;

/**
 * Interfaccia base per i Data Access Object degli oggetti Group. 
 * @author E.Santoboni
 */
public interface IGroupDAO extends IApsAuthorityDAO {
	
	/**
	 * Carica la mappa dei gruppi presenti nel sistema 
	 * indicizzandola in base al nome del gruppo.
	 * @return La mappa dei gruppi presenti nel sistema.
	 */
	public Map<String, Group> loadGroups();
	
	/**
	 * Aggiunge un gruppo nel db.
	 * @param group Il gruppo da aggiungere.
	 */
	public void addGroup(Group group);
	
	/**
	 * Aggiorna un gruppo nel db.
	 * @param group Il gruppo da aggiornare.
	 */
	public void updateGroup(Group group);
	
	/**
	 * Rimuove un gruppo dal db.
	 * @param group Il gruppo da rimuovere.
	 */
	public void deleteGroup(Group group);
	
	/**
	 * Rimuove un gruppo dal sistema.
	 * @param groupName Il nome del gruppo da rimuovere.
	 */
	public void deleteGroup(String groupName);
	
}
