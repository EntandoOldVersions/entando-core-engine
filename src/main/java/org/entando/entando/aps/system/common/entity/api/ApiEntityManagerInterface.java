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
package org.entando.entando.aps.system.common.entity.api;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.BaseApiResponse;
import org.entando.entando.aps.system.services.api.server.IResponseBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.IEntityTypesConfigurer;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * @author E.Santoboni
 */
public class ApiEntityManagerInterface implements BeanFactoryAware {
    /*
    public List<JAXBEntity> getEntities(Properties properties) throws Throwable {
        return null;
    }
    
    public JAXBEntity getEntity(Properties properties) throws Throwable {
        JAXBEntity jaxbEntity = null;
        String id = properties.getProperty("id");
        try {
            IEntityManager manager = this.extractEntityManager(properties.getProperty("entityManagerName"));
            String langCode = properties.getProperty(SystemConstants.API_LANG_CODE_PARAMETER);
            IApsEntity mainEntity = manager.getEntity(id);
            jaxbEntity = new JAXBEntity(mainEntity, langCode);
        } catch (ApiException ae) {
            throw ae;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getEntity");
            throw new ApsSystemException("Error into API method", t);
        }
        return jaxbEntity;
    }
    
    public void addEntity(JAXBEntity entity) throws Throwable {
        //
    }
    
    public void updateEntity(JAXBEntity entity) throws Throwable {
        //
    }
    
    public void deleteEntity(Properties properties) throws Throwable {
        //
    }
    */
    public List<JAXBEntityType> getEntityTypes(Properties properties) throws Throwable {
        return null;
    }
    
    public JAXBEntityType getEntityType(Properties properties) throws Throwable {
        JAXBEntityType jaxbEntityType = null;
        try {
            IEntityManager manager = this.extractEntityManager(properties.getProperty("entityManagerName"));
            String typeCode = properties.getProperty("entityTypeCode");
            IApsEntity masterEntityType = manager.getEntityPrototype(typeCode);
            if (null == masterEntityType) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Entity type with code '" + typeCode + "' does not exist");
            }
            jaxbEntityType = new JAXBEntityType(masterEntityType);
        } catch (ApiException ae) {
            throw ae;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getEntityType");
            throw new ApsSystemException("Error extracting entity type", t);
        }
        return jaxbEntityType;
    }
    
    public BaseApiResponse addEntityType(JAXBEntityType jaxbEntityType) throws Throwable {
        BaseApiResponse response = new BaseApiResponse();
        try {
            IEntityManager manager = this.extractEntityManager(jaxbEntityType.getEntityManagerName());
            String typeCode = jaxbEntityType.getTypeCode();
            IApsEntity masterEntityType = manager.getEntityPrototype(typeCode);
            if (null != masterEntityType) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Entity type with code '" + typeCode + "' already exists");
            }
            if (typeCode == null || typeCode.length() != 3) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Invalid type code - '" + typeCode + "'");
            }
            Map<String, AttributeInterface> attributes = manager.getEntityAttributePrototypes();
            IApsEntity entityType = jaxbEntityType.buildEntityType(manager.getEntityClass(), attributes);
            ((IEntityTypesConfigurer) manager).addEntityPrototype(entityType);
            response.setResult(IResponseBuilder.SUCCESS, null);
        } catch (ApiException ae) {
            response.addErrors(ae.getErrors());
            response.setResult(IResponseBuilder.FAILURE, null);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getEntityType");
            throw new ApsSystemException("Error extracting entity type", t);
        }
        return response;
    }
    
    public void updateEntityType(JAXBEntityType jaxbEntityType) throws Throwable {
        //
    }
    
    public void deleteEntityType(Properties properties) throws Throwable {
        //
    }
    /*
    protected IApsEntity extractEntityType(String typeCode, IEntityManager entityManager) {
        return entityManager.getEntityPrototype(typeCode);
    }
    */
    protected IEntityManager extractEntityManager(String entityManagerName) throws ApiException {
        IEntityManager manager = (IEntityManager) this.getBeanFactory().getBean(entityManagerName, IEntityManager.class);
        if (null == manager) {
            throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Invalid Entity Manager '" + entityManagerName + "'");
        }
        return manager;
    }
    
    protected BeanFactory getBeanFactory() {
        return this._beanFactory;
    }
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this._beanFactory = beanFactory;
    }
    
    private BeanFactory _beanFactory;
    
}
