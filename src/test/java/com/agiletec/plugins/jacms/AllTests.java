/*
 *
 * Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
 * Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 */
package com.agiletec.plugins.jacms;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.agiletec.plugins.jacms.aps.system.TestApplicationContext;
import com.agiletec.plugins.jacms.aps.system.services.content.TestCategoryUtilizer;
import com.agiletec.plugins.jacms.aps.system.services.content.TestContentDAO;
import com.agiletec.plugins.jacms.aps.system.services.content.TestContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.TestGroupUtilizer;
import com.agiletec.plugins.jacms.aps.system.services.content.TestPublicContentSearcherDAO;
import com.agiletec.plugins.jacms.aps.system.services.content.TestValidateContent;
import com.agiletec.plugins.jacms.aps.system.services.content.authorization.TestContentAuthorization;
import com.agiletec.plugins.jacms.aps.system.services.content.entity.TestContentEntityManager;
import com.agiletec.plugins.jacms.aps.system.services.content.parse.TestContentDOM;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.TestContentListHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.TestContentViewerHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.util.TestContentAttributeIterator;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.TestContentModelDAO;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.TestContentModelManager;
import com.agiletec.plugins.jacms.aps.system.services.contentpagemapper.TestContentPageMapperManager;
import com.agiletec.plugins.jacms.aps.system.services.dispenser.TestContentDispenser;
import com.agiletec.plugins.jacms.aps.system.services.linkresolver.TestLinkResolverManager;
import com.agiletec.plugins.jacms.aps.system.services.page.TestCmsPageDAO;
import com.agiletec.plugins.jacms.aps.system.services.resource.TestResourceDAO;
import com.agiletec.plugins.jacms.aps.system.services.resource.TestResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.parse.TestResourceDOM;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.TestSearchEngineManager;
import com.agiletec.plugins.jacms.apsadmin.category.TestTrashReferencedCategory;
import com.agiletec.plugins.jacms.apsadmin.content.TestContentAction;
import com.agiletec.plugins.jacms.apsadmin.content.TestContentAdminAction;
import com.agiletec.plugins.jacms.apsadmin.content.TestContentFinderAction;
import com.agiletec.plugins.jacms.apsadmin.content.TestContentGroupAction;
import com.agiletec.plugins.jacms.apsadmin.content.TestContentInspectionAction;
import com.agiletec.plugins.jacms.apsadmin.content.TestContentPreviewAction;
import com.agiletec.plugins.jacms.apsadmin.content.TestIntroNewContentAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.TestContentLinkAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.TestExtendedResourceAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.TestExtendedResourceFinderAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.TestHypertextAttributeAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.TestLinkAttributeAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.TestListAttributeAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.TestPageLinkAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.TestResourceAttributeAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.TestUrlLinkAction;
import com.agiletec.plugins.jacms.apsadmin.content.model.TestContentModelAction;
import com.agiletec.plugins.jacms.apsadmin.content.model.TestContentModelFinderAction;
import com.agiletec.plugins.jacms.apsadmin.portal.TestPageAction;
import com.agiletec.plugins.jacms.apsadmin.portal.TestTrashReferencedPage;
import com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.listviewer.TestBaseFilterAction;
import com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.listviewer.TestContentListViewerShowletAction;
import com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.listviewer.TestDateAttributeFilterAction;
import com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.listviewer.TestNumberAttributeFilterAction;
import com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.listviewer.TestTextAttributeFilterAction;
import com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.viewer.TestContentFinderViewerAction;
import com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.viewer.TestContentViewerShowletAction;
import com.agiletec.plugins.jacms.apsadmin.resource.TestResourceAction;
import com.agiletec.plugins.jacms.apsadmin.resource.TestResourceFinderAction;
import com.agiletec.plugins.jacms.apsadmin.system.entity.TestJacmsEntityAttributeConfigAction;
import com.agiletec.plugins.jacms.apsadmin.system.entity.TestJacmsEntityManagersAction;
import com.agiletec.plugins.jacms.apsadmin.system.entity.TestJacmsEntityTypeConfigAction;
import com.agiletec.plugins.jacms.apsadmin.user.group.TestTrashReferencedGroup;
import org.entando.entando.plugins.jacms.apsadmin.content.TestValidateDateAttribute;
import org.entando.entando.plugins.jacms.apsadmin.content.TestValidateMonotextAttribute;
import org.entando.entando.plugins.jacms.apsadmin.content.TestValidateTextAttribute;

public class AllTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for jACMS");
		
		System.out.println("Test for jACMS plugin");
		
		// 
		suite.addTestSuite(TestContentAuthorization.class);
		suite.addTestSuite(TestContentEntityManager.class);
		suite.addTestSuite(TestContentDOM.class);
		suite.addTestSuite(TestContentListHelper.class);
		suite.addTestSuite(TestContentViewerHelper.class);
		suite.addTestSuite(TestContentAttributeIterator.class);
		suite.addTestSuite(TestContentDAO.class);
		suite.addTestSuite(TestContentManager.class);
		suite.addTestSuite(TestPublicContentSearcherDAO.class);
		suite.addTestSuite(TestValidateContent.class);
		//
		suite.addTestSuite(TestContentModelDAO.class);
		suite.addTestSuite(TestContentModelManager.class);
		//
		suite.addTestSuite(TestContentPageMapperManager.class);
		//
		suite.addTestSuite(TestContentDispenser.class);
		//
		suite.addTestSuite(TestLinkResolverManager.class);
		//
		suite.addTestSuite(TestCmsPageDAO.class);
		//
		suite.addTestSuite(TestResourceDOM.class);
		suite.addTestSuite(TestResourceDAO.class);
		suite.addTestSuite(TestResourceManager.class);
		//
		suite.addTestSuite(TestSearchEngineManager.class);
		suite.addTestSuite(TestApplicationContext.class);
		
		// Test cross utilizers
		suite.addTestSuite(TestCategoryUtilizer.class);
		suite.addTestSuite(TestGroupUtilizer.class);
		suite.addTestSuite(TestTrashReferencedCategory.class);
		
		// Content
		suite.addTestSuite(TestListAttributeAction.class);
		suite.addTestSuite(TestResourceAttributeAction.class);
		suite.addTestSuite(TestExtendedResourceAction.class);
		suite.addTestSuite(TestExtendedResourceFinderAction.class);
		suite.addTestSuite(TestHypertextAttributeAction.class);
		suite.addTestSuite(TestLinkAttributeAction.class);
		suite.addTestSuite(TestPageLinkAction.class);
		suite.addTestSuite(TestContentLinkAction.class);
		suite.addTestSuite(TestUrlLinkAction.class);
		suite.addTestSuite(TestContentModelAction.class);
		suite.addTestSuite(TestContentModelFinderAction.class);
		suite.addTestSuite(TestContentAction.class);
		suite.addTestSuite(TestValidateDateAttribute.class);
		suite.addTestSuite(TestValidateMonotextAttribute.class);
		suite.addTestSuite(TestValidateTextAttribute.class);
		suite.addTestSuite(TestContentAdminAction.class);
		suite.addTestSuite(TestContentFinderAction.class);
		suite.addTestSuite(TestContentGroupAction.class);
		suite.addTestSuite(TestContentInspectionAction.class);
		suite.addTestSuite(TestContentPreviewAction.class);
		suite.addTestSuite(TestIntroNewContentAction.class);
		
		// Page
		suite.addTestSuite(TestContentListViewerShowletAction.class);
		suite.addTestSuite(TestBaseFilterAction.class);
		suite.addTestSuite(TestDateAttributeFilterAction.class);
		suite.addTestSuite(TestNumberAttributeFilterAction.class);
		suite.addTestSuite(TestTextAttributeFilterAction.class);
		suite.addTestSuite(TestContentFinderViewerAction.class);
		suite.addTestSuite(TestContentViewerShowletAction.class);
		suite.addTestSuite(TestPageAction.class);
		suite.addTestSuite(TestTrashReferencedPage.class);
		
		//Resource
		suite.addTestSuite(TestResourceAction.class);
		suite.addTestSuite(TestResourceFinderAction.class);
		
		//Entity
		suite.addTestSuite(TestJacmsEntityAttributeConfigAction.class);
		suite.addTestSuite(TestJacmsEntityTypeConfigAction.class);
		suite.addTestSuite(TestJacmsEntityManagersAction.class);
		
		//Group
		suite.addTestSuite(TestTrashReferencedGroup.class);
		
		return suite;
	}

}
