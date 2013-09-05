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
package com.agiletec.aps.system.common.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.common.AbstractDAO;
import com.agiletec.aps.system.common.entity.model.ApsEntityRecord;
import com.agiletec.aps.system.common.entity.model.AttributeSearchInfo;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.util.EntityAttributeIterator;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.lang.ILangManager;

/**
 * Abstract DAO class used for the management of the ApsEntities.
 * @author E.Santoboni
 */
public abstract class AbstractEntityDAO extends AbstractDAO implements IEntityDAO {
	
	@Override
	public void addEntity(IApsEntity entity) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(this.getAddEntityRecordQuery());
			this.buildAddEntityStatement(entity, stat);
			stat.executeUpdate();
			this.addEntitySearchRecord(entity.getId(), entity, conn);
			this.addEntityAttributeRoleRecord(entity.getId(), entity, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error adding entity", "addEntity");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	protected abstract String getAddEntityRecordQuery();
	
	protected abstract void buildAddEntityStatement(IApsEntity entity, PreparedStatement stat) throws Throwable;
	
	@Override
	public void deleteEntity(String entityId) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteRecordsByEntityId(entityId, this.getRemovingSearchRecordQuery(), conn);
			this.deleteRecordsByEntityId(entityId, this.getRemovingAttributeRoleRecordQuery(), conn);
			this.deleteRecordsByEntityId(entityId, this.getDeleteEntityRecordQuery(), conn);
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Error deleting the entity by id", "deleteEntity");
		} finally {
			closeConnection(conn);
		}
	}
	
	protected abstract String getDeleteEntityRecordQuery();
	
	@Override
	public void updateEntity(IApsEntity entity) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteRecordsByEntityId(entity.getId(), this.getRemovingSearchRecordQuery(), conn);
			this.deleteRecordsByEntityId(entity.getId(), this.getRemovingAttributeRoleRecordQuery(), conn);
			stat = conn.prepareStatement(this.getUpdateEntityRecordQuery());
			this.buildUpdateEntityStatement(entity, stat);
			stat.executeUpdate();
			this.addEntitySearchRecord(entity.getId(), entity, conn);
			this.addEntityAttributeRoleRecord(entity.getId(), entity, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore updating entity", "updateEntity");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	protected abstract String getUpdateEntityRecordQuery();
	
	protected abstract void buildUpdateEntityStatement(IApsEntity entity, PreparedStatement stat) throws Throwable;
	
	@Override
	public ApsEntityRecord loadEntityRecord(String id) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		ApsEntityRecord entityRecord = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(this.getLoadEntityRecordQuery());
			stat.setString(1, id);
			res = stat.executeQuery();
			if (res.next()) {
				entityRecord = this.createEntityRecord(res);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading entity record", "loadEntityRecord");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return entityRecord;
	}
	
	protected abstract String getLoadEntityRecordQuery();
	
	protected abstract ApsEntityRecord createEntityRecord(ResultSet res) throws Throwable;
	
	@Override
	public void reloadEntitySearchRecords(String id, IApsEntity entity) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteRecordsByEntityId(id, this.getRemovingSearchRecordQuery(), conn);
			this.deleteRecordsByEntityId(id, this.getRemovingAttributeRoleRecordQuery(), conn);
			this.addEntitySearchRecord(id, entity, conn);
			this.addEntityAttributeRoleRecord(id, entity, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error detected while reloading references", "reloadEntitySearchRecords");
		} finally {
			this.closeConnection(conn);
		}
	}
	
	/**
	 * 'Utility' method.
	 * Add a record in the 'contentSearch' table for every indexed attribute.
	 */
	protected void addEntitySearchRecord(String id, IApsEntity entity, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(this.getAddingSearchRecordQuery());
			this.addEntitySearchRecord(id, entity, stat);
		} catch (Throwable t) {
			processDaoException(t, "Error while adding a new record", "addEntitySearchRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	protected void addEntitySearchRecord(String id, IApsEntity entity, PreparedStatement stat) throws Throwable {
		EntityAttributeIterator attributeIter = new EntityAttributeIterator(entity);
		while (attributeIter.hasNext()) {
			AttributeInterface currAttribute = (AttributeInterface) attributeIter.next();
			List<AttributeSearchInfo> infos = currAttribute.getSearchInfos(this.getLangManager().getLangs());
			if (currAttribute.isSearcheable() && null != infos) {
				for (int i=0; i<infos.size(); i++) {
					AttributeSearchInfo searchInfo = infos.get(i);
					stat.setString(1, id);
					stat.setString(2, currAttribute.getName());
					stat.setString(3, searchInfo.getString());
					if (searchInfo.getDate() != null) {
						stat.setTimestamp(4, new java.sql.Timestamp(searchInfo.getDate().getTime()));
					} else {
						stat.setDate(4, null);
					}
					stat.setBigDecimal(5, searchInfo.getBigDecimal());
					stat.setString(6, searchInfo.getLangCode());
					stat.addBatch();
					stat.clearParameters();
				}
			}
		}
		stat.executeBatch();
	}
	
	protected void addEntityAttributeRoleRecord(String id, IApsEntity entity, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(this.getAddingAttributeRoleRecordQuery());
			this.addEntityAttributeRoleRecord(id, entity, stat);
		} catch (Throwable t) {
			processDaoException(t, "Error while adding a new attribute role record", "addEntityAttributeRoleRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	protected void addEntityAttributeRoleRecord(String id, IApsEntity entity, PreparedStatement stat) throws Throwable {
		List<AttributeInterface> attributes = entity.getAttributeList();
		for (int i = 0; i < attributes.size(); i++) {
			AttributeInterface currAttribute = attributes.get(i);
			String[] roleNames = currAttribute.getRoles();
			if (null != roleNames && roleNames.length > 0) {
				for (int j = 0; j < roleNames.length; j++) {
					String roleName = roleNames[j];
					stat.setString(1, id);
					stat.setString(2, currAttribute.getName());
					stat.setString(3, roleName);
					stat.addBatch();
					stat.clearParameters();
				}
			}
		}
		stat.executeBatch();
	}
	
	/**
	 * 'Utility' method.
	 * Delete the records in the support table (the table used to perform search the entities)
	 */
	protected void deleteEntitySearchRecord(String id, Connection conn) throws ApsSystemException {
		this.deleteRecordsByEntityId(id, this.getRemovingSearchRecordQuery(), conn);
	}
	
	/**
	 * 'Utility' method. Delete entity records by entity id
	 * @param entityId the entity id to use for deleting records.
	 * @param query The sql query
	 * @param conn The connection.
	 */
	protected void deleteRecordsByEntityId(String entityId, String query, Connection conn) {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(query);
			stat.setString(1, entityId);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error deleting entity records by id " + entityId, 
					"deleteRecordsByEntityId");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	/**
	 * @deprecated deprecated from jAPS 2.0 version 2.0.9
	 */
	@Override
	public List<String> getAllEntityId() {
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		List<String> entitiesId = new ArrayList<String>();
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(this.getExtractingAllEntityIdQuery());
			while (res.next()) {
				entitiesId.add(res.getString(1));
			}
		} catch (Throwable t) {
			processDaoException(t, "Error retrieving the list of entity IDs", "getAllEntityId");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return entitiesId;
	}
	
	/**
	 * Return the specific query to add a new record of informations in the
	 * support database.
	 * The query must respect the following positions of the elements:<br />
	 * Position 1: entity ID<br />
	 * Position 2: attribute name<br />
	 * Position 3: searchable string<br />
	 * Position 4: searchable data<br />
	 * Position 5: searchable number<br />
	 * Position 6: Language code
	 * @return the query to add a look up record for the entity search. 
	 */
	protected abstract String getAddingSearchRecordQuery();
	
	protected abstract String getAddingAttributeRoleRecordQuery();
	
	/**
	 * Return the query to delete the record associated to an entity. The returned query will only need
	 * the declaration of the ID of the entity to delete.
	 * @return  The query to delete the look up record of a single entity.
	 */
	protected abstract String getRemovingSearchRecordQuery();
	
	protected abstract String getRemovingAttributeRoleRecordQuery();
	
	/**
	 * Return the query that extracts the list of entity IDs.
	 * @return The query that extracts the list of entity IDs.
	 * @deprecated As of jAPS 2.0 version 2.0.9
	 */
	protected abstract String getExtractingAllEntityIdQuery();
	
	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}
	
	private ILangManager _langManager;
	
}