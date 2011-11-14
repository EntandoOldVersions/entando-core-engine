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
package com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.listviewer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.util.FilterUtils;
import com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.listviewer.ContentListViewerShowletAction;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestContentListViewerShowletAction extends ApsAdminBaseTestCase {
	
	public void testInitConfigListViewer_1() throws Throwable {
		String result = this.executeConfigListViewer("admin", "homepage", "1", "content_viewer_list");
		assertEquals(Action.SUCCESS, result);
		ContentListViewerShowletAction action = (ContentListViewerShowletAction) this.getAction();
		Showlet showlet = action.getShowlet();
		assertNotNull(showlet);
		assertEquals(0, showlet.getConfig().size());
		List<Properties> filtersProperties = action.getFiltersProperties();
		assertNotNull(filtersProperties);
		assertEquals(0, filtersProperties.size());
	}
	
	public void testInitConfigListViewer_2() throws Throwable {
		String result = this.executeConfigListViewer("admin", "homepage", "0", null);
		assertEquals(Action.SUCCESS, result);
		ContentListViewerShowletAction action = (ContentListViewerShowletAction) this.getAction();
		Showlet showlet = action.getShowlet();
		assertNotNull(showlet);
		assertEquals("content_viewer_list", showlet.getType().getCode());
		ApsProperties props = showlet.getConfig();
		assertEquals(4, props.size());
		assertEquals("NEW", props.getProperty("contentType"));
		assertEquals("5", props.getProperty("maxElemForItem"));
		assertEquals("11", props.getProperty("modelId"));
		assertEquals("(order=DESC;attributeFilter=true;likeOption=false;key=Date)+(order=ASC;attributeFilter=true;likeOption=false;key=Title)", props.getProperty("filters"));
		List<Properties> filtersProperties = action.getFiltersProperties();
		assertEquals(2, filtersProperties.size());
		Properties firstFilter = filtersProperties.get(0);
		assertEquals("DESC", firstFilter.getProperty(EntitySearchFilter.ORDER_PARAM));
		assertEquals("true", firstFilter.getProperty(EntitySearchFilter.FILTER_TYPE_PARAM));
		assertEquals("false", firstFilter.getProperty(EntitySearchFilter.LIKE_OPTION_PARAM));
		assertEquals("Date", firstFilter.getProperty(EntitySearchFilter.KEY_PARAM));
	}
	
	public void testFailureConfigContentType_1() throws Throwable {
		String result = this.executeConfigContentType("admin", "homepage", "1", "content_viewer_list", "");
		assertEquals(Action.INPUT, result);
		ContentListViewerShowletAction action = (ContentListViewerShowletAction) this.getAction();
		Showlet showlet = action.getShowlet();
		assertNotNull(showlet);
		ApsProperties props = showlet.getConfig();
		assertEquals(0, props.size());
	}
	
	public void testFailureConfigContentType_2() throws Throwable {
		String result = this.executeConfigContentType("admin", "homepage", "1", "content_viewer_list", "WRO");//Tipo contenuto inesistente
		assertEquals(Action.INPUT, result);
		ContentListViewerShowletAction action = (ContentListViewerShowletAction) this.getAction();
		Showlet showlet = action.getShowlet();
		assertNotNull(showlet);
		ApsProperties props = showlet.getConfig();
		assertEquals(0, props.size());
	}
	
	public void testConfigContentType() throws Throwable {
		String result = this.executeConfigContentType("admin", "homepage", "1", "content_viewer_list", "ART");
		assertEquals(Action.SUCCESS, result);
		ContentListViewerShowletAction action = (ContentListViewerShowletAction) this.getAction();
		Showlet showlet = action.getShowlet();
		assertNotNull(showlet);
		assertEquals("content_viewer_list", showlet.getType().getCode());
		ApsProperties props = showlet.getConfig();
		assertEquals(1, props.size());
		assertEquals("ART", props.getProperty("contentType"));
	}
	
	private String executeConfigListViewer(String userName, String pageCode, String frame, String showletTypeCode) throws Throwable {
		this.setUserOnSession(userName);
		this.initAction("/do/Page/SpecialShowlet", "listViewerConfig");
		this.addParameter("pageCode", pageCode);
		this.addParameter("frame", frame);
		if (null != showletTypeCode && showletTypeCode.trim().length()>0) {
			this.addParameter("showletTypeCode", showletTypeCode);
		}
		return this.executeAction();
	}
	
	private String executeConfigContentType(String userName, String pageCode, String frame, String showletTypeCode, String contentType) throws Throwable {
		this.setUserOnSession(userName);
		this.initAction("/do/jacms/Page/SpecialShowlet/ListViewer", "configListViewer");
		this.addParameter("pageCode", pageCode);
		this.addParameter("frame", frame);
		this.addParameter("showletTypeCode", showletTypeCode);
		if (null != contentType && contentType.trim().length()>0) {
			this.addParameter("contentType", contentType);
		}
		return this.executeAction();
	}
	
	public void testMoveFilter() throws Throwable {
		Map<String, String> paramsUp = new HashMap<String, String>();
		paramsUp.put("movement","UP");
		paramsUp.put("filterIndex","1");
		paramsUp.put("filters", "(order=DESC;attributeFilter=true;likeOption=false;key=Date)+(order=ASC;attributeFilter=true;likeOption=false;key=Title)");
		String result = this.executeMovement("admin", "homepage", "0", "content_viewer_list", paramsUp);
		assertEquals(Action.SUCCESS, result);
		ContentListViewerShowletAction action = (ContentListViewerShowletAction) this.getAction();
		List<Properties> filtersProperties = action.getFiltersProperties();
		assertEquals(2, filtersProperties.size());
		Properties firstFilter = filtersProperties.get(1);
		assertEquals("DESC", firstFilter.getProperty(EntitySearchFilter.ORDER_PARAM));
		assertEquals("true", firstFilter.getProperty(EntitySearchFilter.FILTER_TYPE_PARAM));
		assertEquals("false", firstFilter.getProperty(EntitySearchFilter.LIKE_OPTION_PARAM));
		assertEquals("Date", firstFilter.getProperty(EntitySearchFilter.KEY_PARAM));
		
		Map<String, String> paramsDown = new HashMap<String, String>();
		paramsDown.put("movement","DOWN");
		paramsDown.put("filterIndex","0");
		paramsDown.put("filters", "(order=ASC;attributeFilter=true;likeOption=false;key=Title)+(order=DESC;attributeFilter=true;likeOption=false;key=Date)");
		result = this.executeMovement("admin", "homepage", "0", "content_viewer_list",paramsDown);
		assertEquals(Action.SUCCESS, result);
		action = (ContentListViewerShowletAction) this.getAction();
		filtersProperties = action.getFiltersProperties();
		assertEquals(2, filtersProperties.size());
		firstFilter = filtersProperties.get(0);
		assertEquals("DESC", firstFilter.getProperty(EntitySearchFilter.ORDER_PARAM));
		assertEquals("true", firstFilter.getProperty(EntitySearchFilter.FILTER_TYPE_PARAM));
		assertEquals("false", firstFilter.getProperty(EntitySearchFilter.LIKE_OPTION_PARAM));
		assertEquals("Date", firstFilter.getProperty(EntitySearchFilter.KEY_PARAM));
	}
	
	private String executeMovement(String username, String pageCode, String frame, String showletCode, Map<String, String> params) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/jacms/Page/SpecialShowlet/ListViewer", "moveFilter?movement=UP;filterIndex=1");
		this.addParameters(params);
		this.addParameter("pageCode", pageCode);
		this.addParameter("frame", frame);
		this.addParameter("showletTypeCode", showletCode);
		return this.executeAction();
	}
	
	private String executeDelFilter(String username, String pageCode, String frame, String showletCode, Map<String, String> params) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/jacms/Page/SpecialShowlet/ListViewer", "removeFilter");
		this.addParameters(params);
		this.addParameter("pageCode", pageCode);
		this.addParameter("frame", frame);
		this.addParameter("showletTypeCode", showletCode);
		return this.executeAction();
	}
	
	public void testDeleteFilter() throws Throwable {
		Map<String, String> params = new HashMap<String, String>();
		params.put("filterIndex","1");
		params.put("filters", "(order=DESC;attributeFilter=true;likeOption=false;key=Date)+(order=ASC;attributeFilter=true;likeOption=false;key=Title)");
		String result = this.executeDelFilter("admin", "homepage", "0", "content_viewer_list", params);
		assertEquals(Action.SUCCESS, result);
		ContentListViewerShowletAction action = (ContentListViewerShowletAction) this.getAction();
		List<Properties> filtersProperties = action.getFiltersProperties();
		assertEquals(1, filtersProperties.size());
		Properties firstFilter = filtersProperties.get(0);
		assertEquals("DESC", firstFilter.getProperty(EntitySearchFilter.ORDER_PARAM));
		assertEquals("true", firstFilter.getProperty(EntitySearchFilter.FILTER_TYPE_PARAM));
		assertEquals("false", firstFilter.getProperty(EntitySearchFilter.LIKE_OPTION_PARAM));
		assertEquals("Date", firstFilter.getProperty(EntitySearchFilter.KEY_PARAM));
	}
	
	public void testAddFilter() throws Throwable {
		this.setUserOnSession("admin");
		Map<String, String> params = new HashMap<String, String>();
		params.put("pageCode", "homepage");
		params.put("frame", "1");
		params.put("showletTypeCode", "content_viewer_list");
		params.put("contentType", "NEW");
		params.put("filters", "(order=DESC;attributeFilter=true;likeOption=false;key=Date)");
		List<Properties> temp = FilterUtils.getFiltersProperties("(order=ASC;attributeFilter=true;likeOption=false;key=Title)");
		assertEquals(1, temp.size());
		Properties newFilter = temp.get(0);
		this.initAction("/do/jacms/Page/SpecialShowlet/ListViewer", "addFilter");
		this.addParameters(params);
		this.addParameter("newFilter", newFilter);
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		ContentListViewerShowletAction action = (ContentListViewerShowletAction) this.getAction();
		Showlet showlet = action.getShowlet();
		assertNotNull(showlet);
		assertEquals("content_viewer_list", showlet.getType().getCode());
		ApsProperties props = showlet.getConfig();
		assertEquals(2, props.size());
		assertEquals("NEW", props.getProperty("contentType"));
		List<Properties> filtersProperties = action.getFiltersProperties();
		assertEquals(2, filtersProperties.size());
		Properties firstFilter = filtersProperties.get(0);
		assertEquals("DESC", firstFilter.getProperty(EntitySearchFilter.ORDER_PARAM));
		assertEquals("true", firstFilter.getProperty(EntitySearchFilter.FILTER_TYPE_PARAM));
		assertEquals("false", firstFilter.getProperty(EntitySearchFilter.LIKE_OPTION_PARAM));
		assertEquals("Date", firstFilter.getProperty(EntitySearchFilter.KEY_PARAM));
		
		Properties secondFilter = filtersProperties.get(1);
		assertEquals("ASC", secondFilter.getProperty(EntitySearchFilter.ORDER_PARAM));
		assertEquals("true", secondFilter.getProperty(EntitySearchFilter.FILTER_TYPE_PARAM));
		assertEquals("false", secondFilter.getProperty(EntitySearchFilter.LIKE_OPTION_PARAM));
		assertEquals("Title", secondFilter.getProperty(EntitySearchFilter.KEY_PARAM));
	}
	
}
