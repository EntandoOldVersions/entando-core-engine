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

import com.agiletec.aps.system.services.page.IPageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.ws.rs.core.Response;

import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author E.Santoboni
 */
public class ApiWidgetTypeInterface {
	
	private static final Logger _logger = LoggerFactory.getLogger(ApiWidgetTypeInterface.class);
	/*
	public List<String> getPageModels(Properties properties) throws Throwable {
		List<String> list = new ArrayList<String>();
		try {
			Collection<PageModel> pageModels = this.getPageModelManager().getPageModels();
			if (null != pageModels) {
				Iterator<PageModel> iter = pageModels.iterator();
				while (iter.hasNext()) {
					PageModel pageModel = iter.next();
					list.add(pageModel.getCode());
				}
			}
		} catch (Throwable t) {
			_logger.error("Error extracting list of models", t);
			throw t;
		}
		return list;
	}
	*/
	
    public JAXBWidgetType getWidgetType(Properties properties) throws ApiException, Throwable {
        String widgetTypeCode = properties.getProperty("code");
		JAXBWidgetType jaxbWidgetType = null;
		try {
			WidgetType widgetType = this.getWidgetTypeManager().getWidgetType(widgetTypeCode);
			if (null == widgetType) {
				throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "WidgetType with code '" + widgetTypeCode + "' does not exist", Response.Status.CONFLICT);
			}
			GuiFragment singleGuiFragment = null;
			List<GuiFragment> fragments = new ArrayList<GuiFragment>();
			if (!widgetType.isLogic()) {
				singleGuiFragment = this.getGuiFragmentManager().getUniqueGuiFragmentByWidgetType(widgetTypeCode);
			} else {
				List<String> fragmentCodes = this.getGuiFragmentManager().getGuiFragmentCodesByWidgetType(widgetTypeCode);
				if (null != fragmentCodes) {
					for (int i = 0; i < fragmentCodes.size(); i++) {
						String fragmentCode = fragmentCodes.get(i);
						GuiFragment fragment = this.getGuiFragmentManager().getGuiFragment(fragmentCode);
						if (null != fragment) {
							fragments.add(fragment);
						}
					}
				}
			}
			jaxbWidgetType = new JAXBWidgetType(widgetType, singleGuiFragment, fragments);
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			_logger.error("Error creating widget type - code '{}'", widgetTypeCode, t);
			throw t;
		}
        return jaxbWidgetType;
    }
	
    public void addWidgetType(JAXBWidgetType jaxbWidgetType) throws ApiException, Throwable {
		try {
			WidgetType widgetType = this.getWidgetTypeManager().getWidgetType(jaxbWidgetType.getCode());
			if (null != widgetType) {
				throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "WidgetType with code " + jaxbWidgetType.getCode() + " already exists", Response.Status.CONFLICT);
			}
			//this.getPageModelManager().addPageModel(pageModel);
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			_logger.error("Error adding new page model", t);
			throw t;
		}
    }
	
	/*
    public void updatePageModel(PageModel pageModel) throws ApiException, Throwable {
		try {
			if (null != this.getPageModelManager().getPageModel(pageModel.getCode())) {
				throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "PageModel with code '" + pageModel.getCode() + "' does not exist", Response.Status.CONFLICT);
			}
			this.getPageModelManager().updatePageModel(pageModel);
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			_logger.error("Error updating page model", t);
			throw t;
		}
    }
	*/
	/*
    public void deletePageModel(Properties properties) throws ApiException, Throwable {
        String code = properties.getProperty("code");
		try {
			PageModel pageModel = this.getPageModelManager().getPageModel(code);
			if (null == pageModel) {
				throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "PageModel with code '" + code + "' does not exist", Response.Status.CONFLICT);
			}
			Map<String, List<Object>> references = new HashMap<String, List<Object>>();
			ListableBeanFactory factory = (ListableBeanFactory) this.getBeanFactory();
			String[] defNames = factory.getBeanNamesForType(PageModelUtilizer.class);
			for (int i=0; i < defNames.length; i++) {
				Object service = null;
				try {
					service = this.getBeanFactory().getBean(defNames[i]);
				} catch (Throwable t) {
					_logger.error("error extracting bean with name '{}'", defNames[i], t);
					throw new ApsSystemException("error extracting bean with name '" + defNames[i] + "'", t);
				}
				if (service != null) {
					PageModelUtilizer pageModelUtilizer = (PageModelUtilizer) service;
					List<Object> utilizers = pageModelUtilizer.getPageModelUtilizers(code);
					if (utilizers != null && !utilizers.isEmpty()) {
						references.put(pageModelUtilizer.getName(), utilizers);
					}
				}
			}
			if (!references.isEmpty()) {
				throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "PageModel with code " + code + " has references with other object", Response.Status.CONFLICT);
			}
			this.getPageModelManager().deletePageModel(code);
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			_logger.error("Error deleting page model throw api", t);
			throw t;
		}
    }
	*/
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	protected IWidgetTypeManager getWidgetTypeManager() {
		return _widgetTypeManager;
	}
	public void setWidgetTypeManager(IWidgetTypeManager widgetTypeManager) {
		this._widgetTypeManager = widgetTypeManager;
	}
	
	protected IGuiFragmentManager getGuiFragmentManager() {
		return _guiFragmentManager;
	}
	public void setGuiFragmentManager(IGuiFragmentManager guiFragmentManager) {
		this._guiFragmentManager = guiFragmentManager;
	}
	
	private IPageManager _pageManager;
	private IWidgetTypeManager _widgetTypeManager;
	private IGuiFragmentManager _guiFragmentManager;
	
}