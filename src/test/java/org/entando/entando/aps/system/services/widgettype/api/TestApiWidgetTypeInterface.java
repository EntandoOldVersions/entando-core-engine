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
package org.entando.entando.aps.system.services.widgettype.api;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;

import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;

/**
 * @author E.Santoboni
 */
public class TestApiWidgetTypeInterface extends BaseTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.init();
	}
	
	public void testJaxbWidgetType() throws Throwable {
		this.testJaxbWidgetType("login_form");
		this.testJaxbWidgetType("formAction");
		this.testJaxbWidgetType("content_viewer");
		this.testJaxbWidgetType("logic_type");
		this.testJaxbWidgetType("entando_apis");
	}
	
	private void testJaxbWidgetType(String widgetTypeCode) throws Throwable {
		WidgetType widgetType = this._widgetTypeManager.getWidgetType(widgetTypeCode);
		JAXBWidgetType jaxbWidgetType = this.getJaxbWidgetType(widgetType);
		String body = this.getMarshalledObject(jaxbWidgetType);
		assertNotNull(body);
	}
	
	protected String getMarshalledObject(Object object) throws Throwable {
		JAXBContext context = JAXBContext.newInstance(object.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter writer = new StringWriter();
		marshaller.marshal(object, writer);
		return writer.toString();
	}
	
	public void testGetJaxbWidgetType() throws Throwable {
		this.testInvokeGetJaxbWidgetType("login_form");
		this.testInvokeGetJaxbWidgetType("formAction");
		this.testInvokeGetJaxbWidgetType("content_viewer");
		this.testInvokeGetJaxbWidgetType("logic_type");
		this.testInvokeGetJaxbWidgetType("entando_apis");
	}
	
	private void testInvokeGetJaxbWidgetType(String widgetTypeCode) throws Throwable {
		Properties properties = new Properties();
		properties.put(SystemConstants.API_USER_PARAMETER, super.getUser("admin"));
		properties.put("code", widgetTypeCode);
		WidgetType widgetType = this._widgetTypeManager.getWidgetType(widgetTypeCode);
		try {
			JAXBWidgetType jaxbwt = this._apiWidgetTypeInterface.getWidgetType(properties);
			assertNotNull(jaxbwt);
			assertEquals(widgetTypeCode, jaxbwt.getCode());
			assertEquals(widgetType.getTitles(), jaxbwt.getTitles());
		} catch (ApiException ae) {
			if (null != widgetType) {
				fail();
			}
		} catch (Throwable t) {
			throw t;
		}
	}
	
	public void testUpdateJaxbWidgetType() throws Throwable {
		this.testInvokeAddJaxbWidgetType("testjaxb_login_form", "login_form", null, false);
		this.testInvokeAddJaxbWidgetType("testjaxb_login_form", "login_form", "**testjaxb_login_form** gui", true);
		this.testInvokeAddJaxbWidgetType("testjaxb_formAction", "formAction", null, false);
		this.testInvokeAddJaxbWidgetType("testjaxb_formAction", "formAction", "**testjaxb_formAction** gui", true);
		this.testInvokeAddJaxbWidgetType("testjaxb_content_viewer", "content_viewer", null, false);
		this.testInvokeAddJaxbWidgetType("testjaxb_content_viewer", "content_viewer", "**testjaxb_formAction** gui", true);
		this.testInvokeAddJaxbWidgetType("testjaxb_logic_type", "logic_type", null, true);
		this.testInvokeAddJaxbWidgetType("testjaxb_logic_type", "logic_type", "**testjaxb_logic_type** gui", false);
		this.testInvokeAddJaxbWidgetType("testjaxb_entando_apis", "entando_apis", null, false);
	}
	
	private void testInvokeAddJaxbWidgetType(String newWidgetTypeCode, String widgetToClone, String customSingleGui, boolean expectedSuccess) throws Throwable {
		WidgetType widgetType = this._widgetTypeManager.getWidgetType(widgetToClone);
		assertNotNull(widgetType);
		WidgetType newWidgetType = widgetType.clone();
		assertNull(this._widgetTypeManager.getWidgetType(newWidgetTypeCode));
		newWidgetType.setCode(newWidgetTypeCode);
		try {
			JAXBWidgetType jaxbWidgetType = this.getJaxbWidgetType(newWidgetType);
			if (null != customSingleGui) {
				jaxbWidgetType.setGui(customSingleGui);
			}
			this._apiWidgetTypeInterface.addWidgetType(jaxbWidgetType);
			if (!expectedSuccess) {
				fail();
			}
			WidgetType extractedWidgetType = this._widgetTypeManager.getWidgetType(newWidgetTypeCode);
			assertNotNull(extractedWidgetType);
			assertEquals(newWidgetType.getConfig(), extractedWidgetType.getConfig());
			assertEquals(newWidgetType.getMainGroup(), extractedWidgetType.getMainGroup());
			assertEquals(newWidgetType.getTitles(), extractedWidgetType.getTitles());
			assertEquals(newWidgetType.getTypeParameters(), extractedWidgetType.getTypeParameters());
			assertFalse(extractedWidgetType.isLocked());
		} catch (ApiException ae) {
			if (expectedSuccess) {
				fail();
			}
		} catch (Throwable t) {
			throw t;
		} finally {
			List<String> codes = this._guiFragmentManager.getGuiFragmentCodesByWidgetType(newWidgetTypeCode);
			if (null != codes) {
				for (int i = 0; i < codes.size(); i++) {
					String code = codes.get(i);
					this._guiFragmentManager.deleteGuiFragment(code);
				}
			}
			this._widgetTypeManager.deleteWidgetType(newWidgetTypeCode);
		}
	}
	
	private JAXBWidgetType getJaxbWidgetType(WidgetType widgetType) throws Throwable {
		assertNotNull(widgetType);
		GuiFragment singleGuiFragment = null;
		List<GuiFragment> fragments = new ArrayList<GuiFragment>();
		if (!widgetType.isLogic()) {
			singleGuiFragment = this._guiFragmentManager.getUniqueGuiFragmentByWidgetType(widgetType.getCode());
		} else {
			List<String> fragmentCodes = this._guiFragmentManager.getGuiFragmentCodesByWidgetType(widgetType.getCode());
			if (null != fragmentCodes) {
				for (int i = 0; i < fragmentCodes.size(); i++) {
					String fragmentCode = fragmentCodes.get(i);
					GuiFragment fragment = this._guiFragmentManager.getGuiFragment(fragmentCode);
					if (null != fragment) {
						fragments.add(fragment);
					}
				}
			}
		}
		return new JAXBWidgetType(widgetType, singleGuiFragment, fragments);
	}
	
	private void init() throws Exception {
		try {
			this._widgetTypeManager = (IWidgetTypeManager) this.getApplicationContext().getBean(SystemConstants.WIDGET_TYPE_MANAGER);
			this._guiFragmentManager = (IGuiFragmentManager) this.getApplicationContext().getBean(SystemConstants.GUI_FRAGMENT_MANAGER);
			this._apiWidgetTypeInterface = (ApiWidgetTypeInterface) this.getApplicationContext().getBean("ApiWidgetTypeInterface");
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}
	
	private IWidgetTypeManager _widgetTypeManager;
	private IGuiFragmentManager _guiFragmentManager;
	private ApiWidgetTypeInterface _apiWidgetTypeInterface;
	
}
