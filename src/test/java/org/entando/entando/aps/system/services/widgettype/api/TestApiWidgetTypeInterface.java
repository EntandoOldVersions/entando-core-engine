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
		assertNotNull(widgetType);
		GuiFragment singleGuiFragment = null;
		List<GuiFragment> fragments = new ArrayList<GuiFragment>();
		if (!widgetType.isLogic()) {
			singleGuiFragment = this._guiFragmentManager.getUniqueGuiFragmentByWidgetType(widgetTypeCode);
		} else {
			List<String> fragmentCodes = this._guiFragmentManager.getGuiFragmentCodesByWidgetType(widgetTypeCode);
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
		JAXBWidgetType jaxbWidgetType = new JAXBWidgetType(widgetType, singleGuiFragment, fragments);
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
