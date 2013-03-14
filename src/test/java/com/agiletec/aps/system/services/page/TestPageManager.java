/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.system.services.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.services.mock.MockShowletsDAO;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.util.ApsProperties;

/**
 * @author M.Diana
 */
public class TestPageManager extends BaseTestCase {
	
	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }

    public void testAddUpdateMoveDeletePage() throws Throwable {
		try {
			_pageManager.deletePage("temp");
			_pageManager.deletePage("temp1");
			_pageManager.deletePage("temp2");
		} catch (Throwable t) {
			throw t;
		}
		Page page = new Page();
		page.setCode("temp");
		IPage parentPage = _pageManager.getPage("service");
		assertNotNull(parentPage);
		page.setParent(parentPage);
		page.setParentCode(parentPage.getCode());
		PageModel pageModel = parentPage.getModel();
		assertNotNull(pageModel);
		page.setModel(pageModel);		
		page.setGroup("free");
		page.setShowable(true);	
		page.setTitle("it", "temptitle");
		ApsProperties titles = new ApsProperties();
		titles.setProperty("it", "pagina temporanea");
		page.setTitles(titles);
		Showlet showlet = new Showlet();
		ApsProperties config = new ApsProperties();
		config.setProperty("temp", "temp");		
		showlet.setConfig(config);
		showlet.setPublishedContent("ART1");
		ShowletType showletType = new ShowletType();
		showletType.setCode("content_viewer");
		showlet.setType(showletType);
		Showlet[] showlets = {showlet};
		page.setShowlets(showlets);
		_pageManager.addPage(page);
		parentPage = _pageManager.getPage("service");
		page.setParent(parentPage);		
		page.setCode("temp1");	
		_pageManager.addPage(page);
		parentPage = _pageManager.getPage("service");
		page.setParent(parentPage);		
		page.setCode("temp2");	
		_pageManager.addPage(page);
		
		IPage extractedPage = _pageManager.getPage("temp");
		assertNotNull(extractedPage);
		assertEquals(extractedPage.getCode(), "temp");
		assertEquals(extractedPage.getGroup(), "free");
		assertEquals(extractedPage.getTitle("it"), "pagina temporanea");
		assertEquals(extractedPage.getModel().getCode(), "service");
		assertEquals(extractedPage.isShowable(), true);
		showlets = extractedPage.getShowlets();
		boolean contains = showlets[0].getConfig().contains("temp");
		assertEquals(contains, true);
		assertEquals(showlets[0].getPublishedContent(), "ART1");
		assertEquals(showlets[0].getType().getCode(), "content_viewer");
		this.updatePage();
		this.movePage();
		this.deletePage();
	}

	private void updatePage() throws Exception {
		Page page = new Page();
		page.setCode("temp");
		IPage parentPage = _pageManager.getPage("service");
		assertNotNull(parentPage);
		page.setParent(parentPage);
		page.setParentCode(parentPage.getCode());
		PageModel pageModel = parentPage.getModel();
		assertNotNull(pageModel);
		page.setModel(pageModel);		
		page.setGroup("free");
		page.setShowable(false);	
		page.setTitle("en", "temptitle1");
		ApsProperties titles = new ApsProperties();
		titles.setProperty("it", "pagina temporanea1");
		page.setTitles(titles);
		Showlet showlet = new Showlet();
		ApsProperties config = new ApsProperties();
		config.setProperty("temp1", "temp1");		
		showlet.setConfig(config);
		showlet.setPublishedContent("ART1");
		ShowletType showletType = new ShowletType();
		showletType.setCode("content_viewer");
		showlet.setType(showletType);
		Showlet[] showlets = {showlet};
		page.setShowlets(showlets);
		_pageManager.updatePage(page);
		
		IPage extractedPage = _pageManager.getPage("temp");
		assertNotNull(extractedPage);
		assertEquals(extractedPage.getCode(), "temp");
		assertEquals(extractedPage.getGroup(), "free");
		assertEquals(extractedPage.getTitle("it"), "pagina temporanea1");
		assertEquals(extractedPage.getModel().getCode(), "service");
		assertEquals(extractedPage.isShowable(), false);
		showlets = extractedPage.getShowlets();
		boolean contains = showlets[0].getConfig().contains("temp1");
		assertEquals(contains, true);
		assertEquals(showlets[0].getPublishedContent(), "ART1");
		assertEquals(showlets[0].getType().getCode(), "content_viewer");
	}	
	
	private void movePage() throws Exception {
		_pageManager.deletePage("temp1");
		boolean moveUp = true;
		_pageManager.movePage("temp2", moveUp);
		IPage page = _pageManager.getPage("temp2");
		IPage[] pages = page.getParent().getChildren();
		int len = pages.length;
		page = pages[len - 2];
		assertEquals(page.getCode(), "temp2");
		moveUp = false;
		_pageManager.movePage("temp2", moveUp);
		page = _pageManager.getPage("temp2");
		pages = page.getParent().getChildren();
		page = pages[len - 1];
		assertEquals(page.getCode(), "temp2");
	}
	
	private void deletePage() throws Throwable {
		DataSource dataSource = (DataSource) this.getApplicationContext().getBean("portDataSource");
		MockShowletsDAO mockShowletsDAO = new MockShowletsDAO();
		mockShowletsDAO.setDataSource(dataSource);
        _pageManager.deletePage("temp");
		_pageManager.deletePage("temp2");
		IPage page = _pageManager.getPage("temp");
		assertNull(page);
        boolean exists = true;
        try {
            exists = mockShowletsDAO.exists("temp");
            assertEquals(exists, false);
            exists = mockShowletsDAO.exists("temp2");
            assertEquals(exists, false);
        } catch (Throwable e) {
            throw e;
        }
	}
	
	public void testFailureJoinShowlet_1() throws Throwable {
		String pageCode = "wrongPageCode";
		int frame = 2;
		try {
			Showlet showlet = this.getShowletForTest("login", null);
			this._pageManager.joinShowlet(pageCode, showlet, frame);
			fail();
		} catch (ApsSystemException e) {
			//Errore per pagina inesistente
		} catch (Throwable t) {
			throw t;
		}
	}
	
	public void testFailureJoinShowlet_2() throws Throwable {
		String pageCode = "pagina_1";
		int frame = 6;
		IPage pagina_1 = this._pageManager.getPage(pageCode);
		assertTrue(pagina_1.getShowlets().length<=frame);
		try {
			Showlet showlet = this.getShowletForTest("login", null);
			this._pageManager.joinShowlet(pageCode, showlet, frame);
			fail();
		} catch (ApsSystemException e) {
			//Errore per frame errato in modello
		} catch (Throwable t) {
			throw t;
		} finally {
			this._pageManager.updatePage(pagina_1);
		}
	}
	
	public void testFailureRemoveShowlet_1() throws Throwable {
		String pageCode = "wrongPageCode";
		int frame = 2;
		try {
			this._pageManager.removeShowlet(pageCode, frame);
			fail();
		} catch (ApsSystemException e) {
			//Errore per pagina inesistente
		} catch (Throwable t) {
			throw t;
		}
	}
	
	public void testFailureRemoveShowlet_2() throws Throwable {
		String pageCode = "pagina_1";
		int frame = 6;
		IPage pagina_1 = this._pageManager.getPage(pageCode);
		assertTrue(pagina_1.getShowlets().length<=frame);
		try {
			this._pageManager.removeShowlet(pageCode, frame);
			fail();
		} catch (ApsSystemException e) {
			//Errore per frame errato in modello
		} catch (Throwable t) {
			throw t;
		}
	}
	
	public void testJoinRemoveShowlet() throws Throwable {
		String pageCode = "pagina_1";
		int frame = 1;
		IPage pagina_1 = this._pageManager.getPage(pageCode);
		assertNull(pagina_1.getShowlets()[frame]);
		try {
			Showlet showlet = this.getShowletForTest("login_form", null);
			this._pageManager.joinShowlet(pageCode, showlet, frame);
			pagina_1 = this._pageManager.getPage(pageCode);
			Showlet extracted = pagina_1.getShowlets()[frame];
			assertNotNull(extracted);
			assertEquals("login_form", extracted.getType().getCode());
			
			this._pageManager.removeShowlet(pageCode, frame);
			pagina_1 = this._pageManager.getPage(pageCode);
			extracted = pagina_1.getShowlets()[frame];
			assertNull(extracted);
		} catch (Throwable t) {
			pagina_1.getShowlets()[frame] = null;
			this._pageManager.updatePage(pagina_1);
			throw t;
		}
	}
	
	public void testSearchPage() throws Throwable {
		List<String> allowedGroupCodes = new ArrayList<String>();
		allowedGroupCodes.add(Group.ADMINS_GROUP_NAME);
		try {
			List<IPage> pagesFound = this._pageManager.searchPages("aGIna_", allowedGroupCodes);
			assertNotNull(pagesFound);
			assertEquals(4, pagesFound.size());
			String pageCodeToken = "agina";
			pagesFound = this._pageManager.searchPages(pageCodeToken, allowedGroupCodes);
			// verify the result found
			assertNotNull(pagesFound);
			Iterator<IPage> itr = pagesFound.iterator();
			assertEquals(5, pagesFound.size());
			while (itr.hasNext()) {
				IPage currentCode = itr.next();
				assertTrue(currentCode.getCode().contains(pageCodeToken));
			}
			pagesFound = this._pageManager.searchPages("", allowedGroupCodes);
			assertNotNull(pagesFound);
			assertEquals(16, pagesFound.size());
			pagesFound = this._pageManager.searchPages(null, allowedGroupCodes);
			assertNotNull(pagesFound);
			assertEquals(16, pagesFound.size());
		} catch (Throwable t) {
			throw t;
		}
	}
	
	public void testGetShowletUtilizers() throws Throwable {
		List<IPage> pageUtilizers1 = this._pageManager.getShowletUtilizers(null);
		assertNotNull(pageUtilizers1);
		assertEquals(0, pageUtilizers1.size());
		
		List<IPage> pageUtilizers2 = this._pageManager.getShowletUtilizers("logic_type");
		assertNotNull(pageUtilizers2);
		assertEquals(0, pageUtilizers2.size());
		
		List<IPage> pageUtilizers3 = this._pageManager.getShowletUtilizers("leftmenu");
		assertNotNull(pageUtilizers3);
		assertEquals(1, pageUtilizers3.size());
		assertEquals("pagina_1", pageUtilizers3.get(0).getCode());
		
		List<IPage> pageUtilizers4 = this._pageManager.getShowletUtilizers("content_viewer");
		assertNotNull(pageUtilizers4);
		assertEquals(7, pageUtilizers4.size());
		assertEquals("homepage", pageUtilizers4.get(0).getCode());
		assertEquals("contentview", pageUtilizers4.get(1).getCode());
		assertEquals("pagina_11", pageUtilizers4.get(2).getCode());
		assertEquals("pagina_2", pageUtilizers4.get(3).getCode());
		assertEquals("coach_page", pageUtilizers4.get(4).getCode());
		assertEquals("customers_page", pageUtilizers4.get(5).getCode());
		assertEquals("customer_subpage_2", pageUtilizers4.get(6).getCode());
	}
	
	private Showlet getShowletForTest(String showletTypeCode, ApsProperties config) throws Throwable {
		ShowletType type = this._showletTypeManager.getShowletType(showletTypeCode);
		Showlet showlet = new Showlet();
		showlet.setType(type);
		if (null != config) {
			showlet.setConfig(config);
		}
		return showlet;
	}
	
	private void init() throws Exception {
    	try {
    		this._pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
    		this._showletTypeManager = (IShowletTypeManager) this.getService(SystemConstants.SHOWLET_TYPE_MANAGER);
    	} catch (Throwable t) {
            throw new Exception(t);
        }
    }
    
	private IPageManager _pageManager = null;
    private IShowletTypeManager _showletTypeManager;
	
}
