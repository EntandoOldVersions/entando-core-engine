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
package com.agiletec.plugins.jacms.aps.system.services.content;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.agiletec.aps.system.common.entity.AbstractEntityDAO;
import com.agiletec.aps.system.common.entity.model.ApsEntityRecord;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.util.EntityAttributeIterator;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.model.CmsAttributeReference;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentRecordVO;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.IReferenceableAttribute;

/**
 * DAO class for objects of type content. 
 * @author M.Diana - E.Santoboni - S.Didaci
 */
public class ContentDAO extends AbstractEntityDAO implements IContentDAO {
	
	@Override
	protected String getLoadEntityRecordQuery() {
		return LOAD_CONTENT_VO;
	}
	
	/**
	 * Search for a content given the ID. This returns null if no results are
	 * found otherwise the content is returned in an object of type 
	 * {@link ContentRecordVO}.
	 * @param id The ID of the content to search for.
	 * @return The content found.
	 * @deprecated From jAPS 2.0 version 2.0.9, use loadEntityRecord
	 */
	@Override
	public ContentRecordVO loadContentVO(String id) {
		return (ContentRecordVO) super.loadEntityRecord(id);
	}
	
	@Override
	protected ApsEntityRecord createEntityRecord(ResultSet res) throws Throwable {
		ContentRecordVO contentVo = new ContentRecordVO();
		contentVo.setId(res.getString(1));
		contentVo.setTypeCode(res.getString(2));
		contentVo.setDescr(res.getString(3));
		contentVo.setStatus(res.getString(4));
		String xmlWork = res.getString(5);
		contentVo.setCreate(DateConverter.parseDate(res.getString(6), JacmsSystemConstants.CONTENT_METADATA_DATE_FORMAT));
		contentVo.setModify(DateConverter.parseDate(res.getString(7), JacmsSystemConstants.CONTENT_METADATA_DATE_FORMAT));
		String xmlOnLine = res.getString(8);
		contentVo.setOnLine(null != xmlOnLine && xmlOnLine.length() > 0);
		contentVo.setSync(xmlWork.equals(xmlOnLine));
		String mainGroupCode = res.getString(9);
		contentVo.setMainGroupCode(mainGroupCode);
		contentVo.setXmlWork(xmlWork);
		contentVo.setXmlOnLine(xmlOnLine);
		contentVo.setVersion(res.getString(10));
		contentVo.setLastEditor(res.getString(11));
		return contentVo;
	}
	
	/**
	 * Insert a content in the DB.
	 * @param content The content to record in the DB.
	 * @deprecated From jAPS 2.0 version 2.0.9, use addEntity
	 */
	@Override
	public void addContent(Content content) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.addContentRecord(content, conn);
			this.addEntitySearchRecord(content.getId(), content, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in aggiunta contenuto", "addContent");
		} finally {
			closeConnection(conn);
		}
	}
	
	/**
	 * @deprecated From jAPS 2.0 version 2.0.9
	 */
	protected void addContentRecord(Content content, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(ADD_CONTENT);
			stat.setString(1, content.getId());
			stat.setString(2, content.getTypeCode());
			stat.setString(3, content.getDescr());
			stat.setString(4, content.getStatus());
			stat.setString(5, content.getXML());
			String currentDate = DateConverter.getFormattedDate(new Date(), JacmsSystemConstants.CONTENT_METADATA_DATE_FORMAT);
			stat.setString(6, currentDate);
			stat.setString(7, currentDate);
			stat.setString(8, content.getMainGroup());
			stat.setString(9, content.getVersion());
			stat.setString(10, content.getLastEditor());
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Errore in aggiunta contenuto",
					"addContentRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
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
			this.addWorkContentRelationsRecord((Content) entity, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error adding new content", "addEntity");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	protected String getAddEntityRecordQuery() {
		return ADD_CONTENT;
	}
	
	@Override
	protected void buildAddEntityStatement(IApsEntity entity, PreparedStatement stat) throws Throwable {
		Content content = (Content) entity;
		stat.setString(1, content.getId());
		stat.setString(2, content.getTypeCode());
		stat.setString(3, content.getDescr());
		stat.setString(4, content.getStatus());
		stat.setString(5, content.getXML());
		String currentDate = DateConverter.getFormattedDate(new Date(), JacmsSystemConstants.CONTENT_METADATA_DATE_FORMAT);
		stat.setString(6, currentDate);
		stat.setString(7, currentDate);
		stat.setString(8, content.getMainGroup());
		stat.setString(9, content.getVersion());
		stat.setString(10, content.getLastEditor());
	}
	
	/**
	 * Updates the given content in a database.
	 * @param content The content to update in the DB.
	 * @deprecated From jAPS 2.0 version 2.0.9, use updateEntity
	 */
	@Override
	public void updateContent(Content content){
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteEntitySearchRecord(content.getId(), conn);
			this.deleteRecordsByEntityId(content.getId(), DELETE_WORK_CONTENT_REL_RECORD, conn);
			this.updateContentRecord(content, conn);
			this.addEntitySearchRecord(content.getId(), content, conn);
			this.addWorkContentRelationsRecord(content, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in aggiornamento contenuto", "updateContent");
		} finally {
			closeConnection(conn);
		}
	}
	
	/**
	 * @deprecated From jAPS 2.0 version 2.0.9
	 */
	protected void updateContentRecord(Content content, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(UPDATE_CONTENT);
			stat.setString(1, content.getTypeCode());
			stat.setString(2, content.getDescr());
			stat.setString(3, content.getStatus());
			stat.setString(4, content.getXML());
			stat.setString(5, DateConverter.getFormattedDate(new Date(), JacmsSystemConstants.CONTENT_METADATA_DATE_FORMAT));
			stat.setString(6, content.getMainGroup());
			stat.setString(7, content.getVersion());
			stat.setString(8, content.getLastEditor());
			stat.setString(9, content.getId());
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Errore in aggiornamento contenuto - " + content.getId(),
					"updateContentRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	@Override
	public void updateEntity(IApsEntity entity) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteEntitySearchRecord(entity.getId(), conn);
			this.deleteRecordsByEntityId(entity.getId(), DELETE_WORK_CONTENT_REL_RECORD, conn);
			stat = conn.prepareStatement(this.getUpdateEntityRecordQuery());
			this.buildUpdateEntityStatement(entity, stat);
			stat.executeUpdate();
			this.addEntitySearchRecord(entity.getId(), entity, conn);
			this.addWorkContentRelationsRecord((Content) entity, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore updating content " + entity.getId(), "updateEntity");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	protected String getUpdateEntityRecordQuery() {
		return UPDATE_CONTENT;
	}
	
	@Override
	protected void buildUpdateEntityStatement(IApsEntity entity, PreparedStatement stat) throws Throwable {
		Content content = (Content) entity;
		stat.setString(1, content.getTypeCode());
		stat.setString(2, content.getDescr());
		stat.setString(3, content.getStatus());
		stat.setString(4, content.getXML());
		stat.setString(5, DateConverter.getFormattedDate(new Date(), JacmsSystemConstants.CONTENT_METADATA_DATE_FORMAT));
		stat.setString(6, content.getMainGroup());
		stat.setString(7, content.getVersion());
		stat.setString(8, content.getLastEditor());
		stat.setString(9, content.getId());
	}
	
	/**
	 * This publishes a content.
	 * @param content the content to publish.
	 */
	@Override
	public void insertOnLineContent(Content content) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			super.deleteRecordsByEntityId(content.getId(), DELETE_WORK_CONTENT_REL_RECORD, conn);
			super.deleteRecordsByEntityId(content.getId(), DELETE_CONTENT_SEARCH_RECORD, conn);
			super.deleteEntitySearchRecord(content.getId(), conn);
			super.deleteRecordsByEntityId(content.getId(), DELETE_CONTENT_REL_RECORD, conn);
			this.updateContentRecordForInsertOnLine(content, conn);
			this.addPublicContentSearchRecord(content.getId(), content, conn);
			super.addEntitySearchRecord(content.getId(), content, conn);
			this.addWorkContentRelationsRecord(content, conn);
			this.addContentRelationsRecord(content, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in inserimento contenuto online - " + content.getId(),
					"insertOnLineContent");
		} finally {
			this.closeConnection(conn);
		}
	}
	
	@Deprecated
	protected void deletePublicContentSearchRecord(String id, Connection conn) throws ApsSystemException {
		super.deleteRecordsByEntityId(id, DELETE_CONTENT_SEARCH_RECORD, conn);
	}
	
	protected void addPublicContentSearchRecord(String id, IApsEntity entity, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(ADD_CONTENT_SEARCH_RECORD);
			this.addEntitySearchRecord(id, entity, stat);
		} catch (Throwable t) {
			processDaoException(t, "Error on adding public content search records", "addEntitySearchRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	protected void updateContentRecordForInsertOnLine(Content content, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(INSERT_ONLINE_CONTENT);
			stat.setString(1, content.getTypeCode());
			stat.setString(2, content.getDescr());
			stat.setString(3, content.getStatus());
			String xml = content.getXML();
			stat.setString(4, xml);
			stat.setString(5, DateConverter.getFormattedDate(new Date(), JacmsSystemConstants.CONTENT_METADATA_DATE_FORMAT));
			stat.setString(6, xml);
			stat.setString(7, content.getMainGroup());
			stat.setString(8, content.getVersion());
			stat.setString(9, content.getLastEditor());
			stat.setString(10, content.getId());
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Errore in aggiornamento contenuto per inserimento onLine - " + content.getId(),
					"updateContentRecordForInsertOnLine");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	/**
	 * Updates the references of a published content
	 * @param content the published content
	 */
	@Override
	public void reloadPublicContentReferences(Content content) {
		if (content.isOnLine()) {
			Connection conn = null;
			try {
				conn = this.getConnection();
				conn.setAutoCommit(false);
				super.deleteRecordsByEntityId(content.getId(), DELETE_CONTENT_SEARCH_RECORD, conn);
				super.deleteRecordsByEntityId(content.getId(), DELETE_CONTENT_REL_RECORD, conn);
				this.addPublicContentSearchRecord(content.getId(), content, conn);
				this.addContentRelationsRecord(content, conn);
				conn.commit();
			} catch (Throwable t) {
				this.executeRollback(conn);
				processDaoException(t, "Errore in reloading references - Content " + content.getId(),
						"reloadPublicContentReferences");
			} finally {
				this.closeConnection(conn);
			}
		}
	}
	
	/**
	 * Updates the references of a content
	 * @param content the content
	 */
	@Override
	public void reloadWorkContentReferences(Content content) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			super.deleteRecordsByEntityId(content.getId(), DELETE_WORK_CONTENT_REL_RECORD, conn);
			super.deleteEntitySearchRecord(content.getId(), conn);
			super.addEntitySearchRecord(content.getId(), content, conn);
			this.addWorkContentRelationsRecord(content, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in reloading references - Content on work " + content.getId(),
					"reloadWorkContentReferences");
		} finally {
			this.closeConnection(conn);
		}
	}
	
	/**
	 * Unpublish a content, preventing it from being displayed in the portal. Obviously the content itslef is not deleted.
	 * @param content the content to unpublish.
	 */
	@Override
	public void removeOnLineContent(Content content) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.removeOnLineContent(content, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in rimozione contenuto online - " + content.getId(),
				"removeOnLineContent");
		} finally {
			this.closeConnection(conn);
		}
	}
	
	/**
	 * Unpublish a content, preventing it from being displayed in the portal. Obviously
	 * the content itslef is not deleted.
	 * @param content the content to unpublish.
	 * @param conn the connection to the DB.
	 * @throws ApsSystemException when connection errors to the database are detected.
	 */
	private void removeOnLineContent(Content content, Connection conn) throws ApsSystemException {
		super.deleteRecordsByEntityId(content.getId(), DELETE_CONTENT_SEARCH_RECORD, conn);
		super.deleteRecordsByEntityId(content.getId(), DELETE_CONTENT_REL_RECORD, conn);
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(REMOVE_ONLINE_CONTENT);
			stat.setString(1, null);
			stat.setString(2, content.getStatus());
			stat.setString(3, content.getXML());
			stat.setString(4, DateConverter.getFormattedDate(new Date(), JacmsSystemConstants.CONTENT_METADATA_DATE_FORMAT));
			stat.setString(5, content.getVersion());
			stat.setString(6, content.getLastEditor());
			stat.setString(7, content.getId());
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Errore in rimozione contenuto online - " + content.getId(),
					"removeOnLineContent");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	/**
	 * Delete permanently a content in the DB. Such operation is not reversible,
	 * so that the content won't be accessible in any manner.
	 * @param content The content to delete permanently.
	 * @deprecated From jAPS 2.0 version 2.0.9, use deleteEntity
	 */
	@Override
	public void deleteContent(Content content) {
		if (null != content) {
			this.deleteEntity(content.getId());
		}
	}
	
	@Override
	protected String getDeleteEntityRecordQuery() {
		return DELETE_CONTENT;
	}
	
	@Override
	public void deleteEntity(String entityId) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			super.deleteRecordsByEntityId(entityId, DELETE_CONTENT_SEARCH_RECORD, conn);
			super.deleteEntitySearchRecord(entityId, conn);
			super.deleteRecordsByEntityId(entityId, DELETE_CONTENT_REL_RECORD, conn);
			super.deleteRecordsByEntityId(entityId, DELETE_WORK_CONTENT_REL_RECORD, conn);
			super.deleteRecordsByEntityId(entityId, this.getDeleteEntityRecordQuery(), conn);
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Errore on delete entity by id", "deleteEntity");
		} finally {
			closeConnection(conn);
		}
	}
	
	/**
	 * Service method.
	 */
	private void addCategoryRelationsRecord(Content content, boolean isPublicRelations, PreparedStatement stat) throws ApsSystemException {
		if (content.getCategories().size()>0) {
			try {
				Set<String> codes = new HashSet<String>();
				Iterator<Category> categoryIter = content.getCategories().iterator();
				while (categoryIter.hasNext()) {
					Category category = (Category) categoryIter.next();
					this.addCategoryCode(category, codes);
				}
				Iterator<String> codeIter = codes.iterator();
				while (codeIter.hasNext()) {
					String code = codeIter.next();
					int i = 1;
					stat.setString(i++, content.getId());
					if (isPublicRelations) {
						stat.setString(i++, null);
						stat.setString(i++, null);
						stat.setBigDecimal(i++, null);
					}
					stat.setString(i++, code);
					if (isPublicRelations) {
						stat.setString(i++, null);
					}
					stat.addBatch();
					stat.clearParameters();
				}
			} catch (SQLException e) {
				processDaoException(e.getNextException(), "Errore in aggiunta record tabella contentrelations - " + content.getId(), "addCategoryRelationsRecord");
			}
		}
	}
	
	private void addCategoryCode(Category category, Set<String> codes) {
		codes.add(category.getCode());
		Category parentCategory = (Category) category.getParent();
		if (null != parentCategory && !parentCategory.getCode().equals(parentCategory.getParentCode())) {
			this.addCategoryCode(parentCategory, codes);
		}
	}
	
	private void addGroupRelationsRecord(Content content, PreparedStatement stat) throws ApsSystemException {
		try {
			content.addGroup(content.getMainGroup());
			Iterator<String> groupIter = content.getGroups().iterator();
			while (groupIter.hasNext()) {
				String groupName = groupIter.next();
				stat.setString(1, content.getId());
				stat.setString(2, null);
				stat.setString(3, null);
				stat.setBigDecimal(4, null);
				stat.setString(5, null);
				stat.setString(6, groupName);
				stat.addBatch();
				stat.clearParameters();
			}
		} catch (Throwable e) {
			processDaoException(e, "Errore in aggiunta record tabella contentrelations - " + content.getId(), 
					"addGroupRelationsRecord");
		}
	}
	
	/**
	 * Add a record in the table 'contentrelations' for every resource, page, other content,
	 * role and category associated to the given content).
	 * @param content The current content.
	 * @param conn The connection to the database.
	 * @throws ApsSystemException when connection error are detected.
	 */
	protected void addContentRelationsRecord(Content content, Connection conn) throws ApsSystemException{
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(ADD_CONTENT_REL_RECORD);
			this.addCategoryRelationsRecord(content, true, stat);
			this.addGroupRelationsRecord(content, stat);
			EntityAttributeIterator attributeIter = new EntityAttributeIterator(content);
			while (attributeIter.hasNext()) {
				AttributeInterface currAttribute = (AttributeInterface) attributeIter.next();
				if (currAttribute instanceof IReferenceableAttribute) {
					IReferenceableAttribute cmsAttribute = (IReferenceableAttribute) currAttribute;
					List<CmsAttributeReference> refs = cmsAttribute.getReferences(this.getLangManager().getLangs());
					for (int i=0; i<refs.size(); i++) {
						CmsAttributeReference ref = refs.get(i);
						stat.setString(1, content.getId());
						stat.setString(2, ref.getRefPage());
						stat.setString(3, ref.getRefContent());
						stat.setString(4, ref.getRefResource());
						stat.setString(5, null);
						stat.setString(6, null);
						stat.addBatch();
						stat.clearParameters();
					}
				}
			}
			stat.executeBatch();
		} catch (BatchUpdateException e) {
			processDaoException(e.getNextException(), "Errore in aggiunta record tabella contentrelations - " + content.getId(), 
					"addContentRelationsRecord");
		} catch (Throwable t) {
			processDaoException(t, "Errore in aggiunta record tabella contentrelations - " + content.getId(), 
					"addContentRelationsRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	protected void addWorkContentRelationsRecord(Content content, Connection conn) throws ApsSystemException{
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(ADD_WORK_CONTENT_REL_RECORD);
			this.addCategoryRelationsRecord(content, false, stat);
			stat.executeBatch();
		} catch (BatchUpdateException e) {
			processDaoException(e.getNextException(), "Errore in aggiunta record tabella workcontentrelations - " + content.getId(), 
					"addContentRelationsRecord");
		} catch (Throwable t) {
			processDaoException(t, "Errore in aggiunta record tabella workcontentrelations - " + content.getId(), 
					"addContentRelationsRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	@Override
	public List<String> getContentUtilizers(String contentId) {
		List<String> contentIds = null;
		try {
			contentIds = this.getUtilizers(contentId, LOAD_REFERENCED_CONTENTS_FOR_CONTENT);
		} catch (Throwable t) {
			processDaoException(t, "Errore in caricamento lista contenuti " +
					"referenziati con contenuto " + contentId, "getContentUtilizers");
		}
		return contentIds;
	}
	
	@Override
	public List<String> getPageUtilizers(String pageCode) {
		List<String> contentIds = null;
		try {
			contentIds = this.getUtilizers(pageCode, LOAD_REFERENCED_CONTENTS_FOR_PAGE);
		} catch (Throwable t) {
			processDaoException(t, "Errore in caricamento lista contenuti referenziati pagina " + pageCode, 
					"getPageUtilizers");
		}
		return contentIds;
	}
	
	@Override
	public List<String> getGroupUtilizers(String groupName) {
		List<String> contentIds = null;
		try {
			contentIds = this.getUtilizers(groupName, LOAD_REFERENCED_CONTENTS_FOR_GROUP);
		} catch (Throwable t) {
			processDaoException(t, "Errore in caricamento lista contenuti referenziati gruppo " + groupName, 
					"getGroupUtilizers");
		}
		return contentIds;
	}
	
	@Override
	public List<String> getResourceUtilizers(String resourceId) {
		List<String> contentIds = null;
		try {
			contentIds = this.getUtilizers(resourceId, LOAD_REFERENCED_CONTENTS_FOR_RESOURCE);
		} catch (Throwable t) {
			processDaoException(t, "Errore in caricamento lista contenuti referenziati gruppo " + resourceId, 
					"getResourceUtilizers");
		}
		return contentIds;
	}
	
	@Override
	public List<String> getCategoryUtilizers(String categoryCode) {
		List<String> contentIds = null;
		try {
			contentIds = this.getUtilizers(categoryCode, LOAD_REFERENCED_CONTENTS_FOR_CATEGORY);
		} catch (Throwable t) {
			processDaoException(t, "Errore in caricamento lista contenuti referenziati categoria " + categoryCode, 
					"getCategoryUtilizers");
		}
		return contentIds;
	}
	
	protected List<String> getUtilizers(String referencedObjectCode, String query) throws Throwable {
		Connection conn = null;
		List<String> contentIds = new ArrayList<String>();
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(query);
			stat.setString(1, referencedObjectCode);
			res = stat.executeQuery();
			while (res.next()) {
				String id = res.getString(1);
				contentIds.add(id);
			}
		} catch (Throwable t) {
			throw t;
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return contentIds;
	}
	
	@Override
	protected String getAddingSearchRecordQuery() {
		return ADD_WORK_CONTENT_SEARCH_RECORD;
	}
	
	@Override
	protected String getRemovingSearchRecordQuery() {
		return DELETE_WORK_CONTENT_SEARCH_RECORD;
	}
	
	@Override
	protected String getExtractingAllEntityIdQuery() {
		return LOAD_ALL_CONTENTS_ID;
	}
	
	@Deprecated
	protected final String DATE_FORMAT = JacmsSystemConstants.CONTENT_METADATA_DATE_FORMAT;
	
	private final String DELETE_CONTENT =
		"DELETE FROM contents WHERE contentid = ? ";
	
	private final String DELETE_CONTENT_REL_RECORD =
		"DELETE FROM contentrelations WHERE contentid = ? ";
	
	private final String DELETE_WORK_CONTENT_REL_RECORD =
		"DELETE FROM workcontentrelations WHERE contentid = ? ";

	private final String ADD_CONTENT_SEARCH_RECORD =
		"INSERT INTO contentsearch (contentid, attrname, textvalue, datevalue, numvalue, langcode) " +
		"VALUES ( ? , ? , ? , ? , ? , ? )";
	
	private final String DELETE_CONTENT_SEARCH_RECORD =
		"DELETE FROM contentsearch WHERE contentid = ? ";
	
	private final String ADD_WORK_CONTENT_SEARCH_RECORD =
		"INSERT INTO workcontentsearch (contentid, attrname, textvalue, datevalue, numvalue, langcode) " +
		"VALUES ( ? , ? , ? , ? , ? , ? )";
	
	private final String DELETE_WORK_CONTENT_SEARCH_RECORD =
		"DELETE FROM workcontentsearch WHERE contentid = ? ";
	
	private final String ADD_CONTENT_REL_RECORD =
		"INSERT INTO contentrelations " +
		"(contentid, refpage, refcontent, refresource, refcategory, refgroup) " +
		"VALUES ( ? , ? , ? , ? , ? , ? )";
	
	private final String ADD_WORK_CONTENT_REL_RECORD =
		"INSERT INTO workcontentrelations (contentid, refcategory) VALUES ( ? , ? )";
	
	private final String LOAD_CONTENTS_ID_MAIN_BLOCK = 
		"SELECT DISTINCT contents.contentid FROM contents ";
	
	private final String LOAD_REFERENCED_CONTENTS_FOR_PAGE = 
		LOAD_CONTENTS_ID_MAIN_BLOCK + 
		" RIGHT JOIN contentrelations ON contents.contentid = contentrelations.contentid WHERE refpage = ? " +
		"ORDER BY contents.contentid";
	
	private final String LOAD_REFERENCED_CONTENTS_FOR_CONTENT = 
		LOAD_CONTENTS_ID_MAIN_BLOCK + 
		" RIGHT JOIN contentrelations ON contents.contentid = contentrelations.contentid WHERE refcontent = ? " +
		"ORDER BY contents.contentid";
	
	private final String LOAD_REFERENCED_CONTENTS_FOR_GROUP = 
		LOAD_CONTENTS_ID_MAIN_BLOCK + 
		" RIGHT JOIN contentrelations ON contents.contentid = contentrelations.contentid WHERE refgroup = ? " +
		"ORDER BY contents.contentid";
	
	private final String LOAD_REFERENCED_CONTENTS_FOR_RESOURCE = 
		LOAD_CONTENTS_ID_MAIN_BLOCK + 
		" RIGHT JOIN contentrelations ON contents.contentid = contentrelations.contentid WHERE refresource = ? " +
		"ORDER BY contents.contentid";
	
	private final String LOAD_REFERENCED_CONTENTS_FOR_CATEGORY = 
		LOAD_CONTENTS_ID_MAIN_BLOCK + 
		" RIGHT JOIN contentrelations ON contents.contentid = contentrelations.contentid WHERE refcategory = ? " +
		"ORDER BY contents.contentid";
	
	private final String LOAD_CONTENTS_VO_MAIN_BLOCK = 
		"SELECT contents.contentid, contents.contenttype, contents.descr, contents.status, " +
		"contents.workxml, contents.created, contents.lastmodified, contents.onlinexml, contents.maingroup, contents.currentversion, contents.lasteditor " +
		"FROM contents ";
	
	private final String LOAD_CONTENT_VO = 
		LOAD_CONTENTS_VO_MAIN_BLOCK + " WHERE contents.contentid = ? ";
	
	private final String ADD_CONTENT =
		"INSERT INTO contents (contentid, contenttype, descr, status, " +
		"workxml, created, lastmodified, maingroup, currentversion, lasteditor) " +
		"VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ?)";
	
	private final String INSERT_ONLINE_CONTENT =
		"UPDATE contents SET contenttype = ? , descr = ? , status = ? , " +
		"workxml = ? , lastmodified = ? , onlinexml = ? , maingroup = ? , currentversion = ? , lasteditor = ? " +
		"WHERE contentid = ? ";
	
	private final String REMOVE_ONLINE_CONTENT = 
		"UPDATE contents SET onlinexml = ? , status = ? , " +
		"workxml = ? , lastmodified = ? , currentversion = ? , lasteditor = ? WHERE contentid = ? ";
	
	private final String UPDATE_CONTENT =
		"UPDATE contents SET contenttype = ? , descr = ? , status = ? , " +
		"workxml = ? , lastmodified = ? , maingroup = ? , currentversion = ? , lasteditor = ? " +
		"WHERE contentid = ? ";
	
	private final String LOAD_ALL_CONTENTS_ID = 
		"SELECT contentid FROM contents";
	
}
