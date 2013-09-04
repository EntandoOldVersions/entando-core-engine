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
package com.agiletec.aps.system.services.showlettype;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.services.mock.MockWidgetTypeDAO;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsProperties;

/**
 * @author M.Diana - E.Santoboni
 */
public class TestWidgetTypeManager extends BaseTestCase {
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testGetShowletTypes() throws ApsSystemException {
		List<WidgetType> list = _showletTypeManager.getShowletTypes();
		Iterator<WidgetType> iter = list.iterator();
		Map<String, String> showletTypes = new HashMap<String, String>();
		while (iter.hasNext()) {
			WidgetType showletType = iter.next();
			showletTypes.put(showletType.getCode(), showletType.getTitles().getProperty("it"));
		}
		boolean containsKey = showletTypes.containsKey("content_viewer_list");
		boolean containsValue = showletTypes.containsValue("Contenuti - Pubblica una Lista di Contenuti");
		assertTrue(containsKey);
		assertTrue(containsValue);
		containsKey = showletTypes.containsKey("content_viewer");
		containsValue = showletTypes.containsValue("Contenuti - Pubblica un Contenuto");
		assertTrue(containsKey);
		assertTrue(containsValue);		
	}
    
    public void testGetShowletType_1() throws ApsSystemException {
    	WidgetType showletType = _showletTypeManager.getShowletType("content_viewer");
		assertEquals("content_viewer", showletType.getCode());
		assertEquals("Contenuti - Pubblica un Contenuto", showletType.getTitles().get("it"));
		assertTrue(showletType.isLocked());
		assertFalse(showletType.isLogic());
		assertFalse(showletType.isUserType());
		assertNull(showletType.getParentType());
		assertNull(showletType.getConfig());
		String action = showletType.getAction();
		assertEquals(action, "viewerConfig");
		List<WidgetTypeParameter> list = showletType.getTypeParameters();
		Iterator<WidgetTypeParameter> iter = list.iterator();
		Map<String, String> parameters = new HashMap<String, String>();
		while (iter.hasNext()) {
			WidgetTypeParameter parameter = (WidgetTypeParameter) iter.next();
			parameters.put(parameter.getName(), parameter.getDescr());
		}
		boolean containsKey = parameters.containsKey("contentId");
		boolean containsValue = parameters.containsValue("Identificativo del Contenuto");
		assertEquals(containsKey, true);
		assertEquals(containsValue, true);
		containsKey = parameters.containsKey("modelId");
		containsValue = parameters.containsValue("Identificativo del Modello di Contenuto");
		assertEquals(containsKey, true);
		assertEquals(containsValue, true);				
	}
	
    public void testGetShowletType_2() throws ApsSystemException {
    	WidgetType showletType = _showletTypeManager.getShowletType("90_events");
		assertEquals("90_events", showletType.getCode());
		assertEquals("Lista contenuti anni '90", showletType.getTitles().get("it"));
		assertFalse(showletType.isLocked());
		assertTrue(showletType.isLogic());
		assertTrue(showletType.isUserType());
		assertNull(showletType.getAction());
		assertNull(showletType.getTypeParameters());
		assertNotNull(showletType.getParentType());
		assertEquals("content_viewer_list", showletType.getParentType().getCode());
		assertNotNull(showletType.getConfig());
		String contentTypeParam = showletType.getConfig().getProperty("contentType");
		assertEquals("EVN", contentTypeParam);
		String filtersParam = showletType.getConfig().getProperty("filters");
		assertTrue(filtersParam.contains("start=01/01/1990"));			
	}
    
    public void testFailureDeleteShowletType_1() throws Throwable {
    	String showletTypeCode = "content_viewer";
    	assertNotNull(this._showletTypeManager.getShowletType(showletTypeCode));
    	try {
    		this._showletTypeManager.deleteShowletType(showletTypeCode);
		} catch (Throwable t) {
			
		}
		assertNotNull(this._showletTypeManager.getShowletType(showletTypeCode));
    }
    
    public void testFailureDeleteShowletType_2() throws Throwable {
    	String showletTypeCode = "test_showletType";
    	assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(showletTypeCode);
			type.setLocked(true);
			this._showletTypeManager.addShowletType(type);
			assertNotNull(this._showletTypeManager.getShowletType(showletTypeCode));
			try {
				this._showletTypeManager.deleteShowletType(showletTypeCode);
				fail();
			} catch (Throwable t) {
				assertNotNull(this._showletTypeManager.getShowletType(showletTypeCode));
			}
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != this._showletTypeManager.getShowletType(showletTypeCode)) {
				this._mockShowletTypeDAO.deleteShowletType(showletTypeCode);
			}
			((IManager) this._showletTypeManager).refresh();
			assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
		}
    }
    
    public void testAddDeleteShowletType() throws Throwable {
    	String showletTypeCode = "test_showletType";
    	assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
    	try {
    		this._showletTypeManager.deleteShowletType(showletTypeCode);
			WidgetType type = this.createNewShowletType(showletTypeCode);
			this._showletTypeManager.addShowletType(type);
			assertNotNull(this._showletTypeManager.getShowletType(showletTypeCode));
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != this._showletTypeManager.getShowletType(showletTypeCode)) {
				this._showletTypeManager.deleteShowletType(showletTypeCode);
			}
			assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
		}
    }

    public void testUpdateTitles() throws Throwable {
    	String showletTypeCode = "test_showletType";
    	assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(showletTypeCode);
			this._showletTypeManager.addShowletType(type);
			WidgetType extracted = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(extracted);
			assertEquals("Titolo", extracted.getTitles().get("it"));
			assertEquals("Title", extracted.getTitles().get("en"));
			ApsProperties newTitles = new ApsProperties();
			newTitles.put("it", "Titolo modificato");
			newTitles.put("en", "Modified title");
	    	this._showletTypeManager.updateShowletTypeTitles(showletTypeCode, newTitles);
	    	extracted = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(extracted);
			assertEquals("Titolo modificato", extracted.getTitles().get("it"));
			assertEquals("Modified title", extracted.getTitles().get("en"));
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != this._showletTypeManager.getShowletType(showletTypeCode)) {
				this._showletTypeManager.deleteShowletType(showletTypeCode);
			}
			assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
		}
    }
    
    public void testUpdate() throws Throwable {
    	String showletTypeCode = "test_showletType";
    	assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(showletTypeCode);
			this._showletTypeManager.addShowletType(type);
			WidgetType extracted = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(extracted);
			assertEquals("content_viewer", extracted.getParentType().getCode());
			assertEquals("ART112", extracted.getConfig().get("contentId"));
			
			ApsProperties newProperties = new ApsProperties();
	    	this._showletTypeManager.updateShowletType(showletTypeCode, extracted.getTitles(), newProperties);
	    	extracted = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(extracted);
			assertNotNull(extracted.getConfig());
			assertEquals(0, extracted.getConfig().size());
			
			newProperties.put("contentId", "EVN103");
			this._showletTypeManager.updateShowletType(showletTypeCode, extracted.getTitles(), newProperties);
			extracted = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(extracted);
			assertEquals("EVN103", extracted.getConfig().get("contentId"));
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != this._showletTypeManager.getShowletType(showletTypeCode)) {
				this._showletTypeManager.deleteShowletType(showletTypeCode);
			}
			assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
		}
    }
    
    private WidgetType createNewShowletType(String code) {
    	WidgetType type = new WidgetType();
    	type.setCode(code);
    	ApsProperties titles = new ApsProperties();
    	titles.put("it", "Titolo");
    	titles.put("en", "Title");
    	type.setTitles(titles);
    	WidgetType parent = this._showletTypeManager.getShowletType("content_viewer");
    	assertNotNull(parent);
    	type.setParentType(parent);
    	type.setPluginCode("jacms");
    	ApsProperties config = new ApsProperties();
    	config.put("contentId", "ART112");
    	type.setConfig(config);
    	return type;
    }
    
    private void init() throws Exception {
		try {
			this._showletTypeManager = (IWidgetTypeManager) this.getService(SystemConstants.WIDGET_TYPE_MANAGER);
			DataSource dataSource = (DataSource) this.getApplicationContext().getBean("portDataSource");
			this._mockShowletTypeDAO = new MockWidgetTypeDAO();
			this._mockShowletTypeDAO.setDataSource(dataSource);
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}
    
    private IWidgetTypeManager _showletTypeManager = null;
    private MockWidgetTypeDAO _mockShowletTypeDAO;
    
}