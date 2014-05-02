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
package org.entando.entando.aps.system.services.guifragment;

import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.exception.ApsSystemException;

import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.cache.ICacheInfoManager;
import org.entando.entando.aps.system.services.guifragment.api.JAXBGuiFragment;
import org.entando.entando.aps.system.services.guifragment.event.GuiFragmentChangedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author E.Santoboni
 */
public class GuiFragmentManager extends AbstractService implements IGuiFragmentManager {
	
	private static final Logger _logger =  LoggerFactory.getLogger(GuiFragmentManager.class);
	
	@Override
	public void init() throws Exception {
		_logger.debug("{} ready.", this.getClass().getName());
	}
	
	@Override
	@Cacheable(value = ICacheInfoManager.CACHE_NAME, key = "'GuiFragment_'.concat(#code)")
	public GuiFragment getGuiFragment(String code) throws ApsSystemException {
		GuiFragment guiFragment = null;
		try {
			guiFragment = this.getGuiFragmentDAO().loadGuiFragment(code);
		} catch (Throwable t) {
			_logger.error("Error loading guiFragment with code '{}'", code,  t);
			throw new ApsSystemException("Error loading guiFragment with code: " + code, t);
		}
		return guiFragment;
	}
	
	@Override
	public List<String> getGuiFragments() throws ApsSystemException {
		List<String> guiFragments = null;
		try {
			guiFragments = this.getGuiFragmentDAO().loadGuiFragments();
		} catch (Throwable t) {
			_logger.error("Error loading GuiFragment list",  t);
			throw new ApsSystemException("Error loading GuiFragment ", t);
		}
		return guiFragments;
	}
	
	@Override
	public List<String> searchGuiFragments(FieldSearchFilter filters[]) throws ApsSystemException {
		List<String> guiFragments = null;
		try {
			guiFragments = this.getGuiFragmentDAO().searchGuiFragments(filters);
		} catch (Throwable t) {
			_logger.error("Error searching GuiFragments", t);
			throw new ApsSystemException("Error searching GuiFragments", t);
		}
		return guiFragments;
	}
	
	@Override
	@CacheEvict(value = ICacheInfoManager.CACHE_NAME, key = "'GuiFragment_'.concat(#guiFragment.code)")
	public void addGuiFragment(GuiFragment guiFragment) throws ApsSystemException {
		try {
			this.getGuiFragmentDAO().insertGuiFragment(guiFragment);
			this.notifyGuiFragmentChangedEvent(guiFragment, GuiFragmentChangedEvent.INSERT_OPERATION_CODE);
		} catch (Throwable t) {
			_logger.error("Error adding GuiFragment", t);
			throw new ApsSystemException("Error adding GuiFragment", t);
		}
	}
	
	@Override
	@CacheEvict(value = ICacheInfoManager.CACHE_NAME, key = "'GuiFragment_'.concat(#guiFragment.code)")
	public void updateGuiFragment(GuiFragment guiFragment) throws ApsSystemException {
		try {
			this.getGuiFragmentDAO().updateGuiFragment(guiFragment);
			this.notifyGuiFragmentChangedEvent(guiFragment, GuiFragmentChangedEvent.UPDATE_OPERATION_CODE);
		} catch (Throwable t) {
			_logger.error("Error updating GuiFragment", t);
			throw new ApsSystemException("Error updating GuiFragment " + guiFragment, t);
		}
	}
	
	@Override
	@CacheEvict(value = ICacheInfoManager.CACHE_NAME, key = "'GuiFragment_'.concat(#code)")
	public void deleteGuiFragment(String code) throws ApsSystemException {
		try {
			GuiFragment guiFragment = this.getGuiFragment(code);
			this.getGuiFragmentDAO().removeGuiFragment(code);
			this.notifyGuiFragmentChangedEvent(guiFragment, GuiFragmentChangedEvent.REMOVE_OPERATION_CODE);
		} catch (Throwable t) {
			_logger.error("Error deleting GuiFragment with code {}", code, t);
			throw new ApsSystemException("Error deleting GuiFragment with code:" + code, t);
		}
	}
	
	/**
	 * GET http://localhost:8080/<portal>/api/rs/en/guiFragments?
	 * @param properties
	 * @return
	 * @throws Throwable
	 */
	/*
	public List<JAXBGuiFragment> getGuiFragmentsForApi(Properties properties) throws Throwable {
		List<JAXBGuiFragment> list = new ArrayList<JAXBGuiFragment>();
		List<String> idList = this.getGuiFragments();
		if (null != idList && !idList.isEmpty()) {
			Iterator<String> guiFragmentIterator = idList.iterator();
			while (guiFragmentIterator.hasNext()) {
				int currentid = guiFragmentIterator.next();
				GuiFragment guiFragment = this.getGuiFragment(currentid);
				if (null != guiFragment) {
					list.add(new JAXBGuiFragment(guiFragment));
				}
			}
		}
		return list;
	}
	*/
	/**
	 * GET http://localhost:8080/<portal>/api/rs/en/guiFragment?id=1
	 * @param properties
	 * @return
	 * @throws Throwable
	 */
    public JAXBGuiFragment getGuiFragmentForApi(Properties properties) throws Throwable {
        String code = properties.getProperty("code");
        //int id = 0;
		JAXBGuiFragment jaxbGuiFragment = null;
        /*
		try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new ApiException(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, "Invalid Integer format for 'id' parameter - '" + idString + "'", Response.Status.CONFLICT);
        }
		*/
        GuiFragment guiFragment = this.getGuiFragment(code);
        if (null == guiFragment) {
            throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "GuiFragment with id '" + code + "' does not exist", Response.Status.CONFLICT);
        }
        jaxbGuiFragment = new JAXBGuiFragment(guiFragment);
        return jaxbGuiFragment;
    }

    /**
     * POST Content-Type: application/xml http://localhost:8080/<portal>/api/rs/en/guiFragment 
     * @param jaxbGuiFragment
     * @throws ApiException
     * @throws ApsSystemException
     */
    public void addGuiFragmentForApi(JAXBGuiFragment jaxbGuiFragment) throws ApiException, ApsSystemException {
        if (null != this.getGuiFragment(jaxbGuiFragment.getCode())) {
            throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "GuiFragment with code " + jaxbGuiFragment.getCode() + " already exists", Response.Status.CONFLICT);
        }
        GuiFragment guiFragment = jaxbGuiFragment.getGuiFragment();
        this.addGuiFragment(guiFragment);
    }

    /**
     * PUT Content-Type: application/xml http://localhost:8080/<portal>/api/rs/en/guiFragment 
     * @param jaxbGuiFragment
     * @throws ApiException
     * @throws ApsSystemException
     */
    public void updateGuiFragmentForApi(JAXBGuiFragment jaxbGuiFragment) throws ApiException, ApsSystemException {
        if (null == this.getGuiFragment(jaxbGuiFragment.getCode())) {
            throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "GuiFragment with code " + jaxbGuiFragment.getCode() + " does not exist", Response.Status.CONFLICT);
        }
        GuiFragment guiFragment = jaxbGuiFragment.getGuiFragment();
        this.updateGuiFragment(guiFragment);
    }

    /**
     * DELETE http://localhost:8080/<portal>/api/rs/en/guiFragment?id=1
	 * @param properties
     * @throws ApiException
     * @throws ApsSystemException
     */
    public void deleteGuiFragmentForApi(Properties properties) throws Throwable {
        String code = properties.getProperty("code");
        this.deleteGuiFragment(code);
    }
	
	private void notifyGuiFragmentChangedEvent(GuiFragment guiFragment, int operationCode) {
		GuiFragmentChangedEvent event = new GuiFragmentChangedEvent();
		event.setGuiFragment(guiFragment);
		event.setOperationCode(operationCode);
		this.notifyEvent(event);
	}
	
	public void setGuiFragmentDAO(IGuiFragmentDAO guiFragmentDAO) {
		 this._guiFragmentDAO = guiFragmentDAO;
	}
	protected IGuiFragmentDAO getGuiFragmentDAO() {
		return _guiFragmentDAO;
	}
	
	private IGuiFragmentDAO _guiFragmentDAO;
	
}
