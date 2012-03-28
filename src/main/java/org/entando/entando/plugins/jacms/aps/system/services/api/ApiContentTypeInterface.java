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
package org.entando.entando.plugins.jacms.aps.system.services.api;

import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContentType;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.entando.entando.aps.system.services.api.server.IResponseBuilder;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.IEntityTypesConfigurer;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;

/**
 * @author E.Santoboni
 */
public class ApiContentTypeInterface extends AbstractCmsApiInterface {
    
    public JAXBContentType getContentType(Properties properties) throws ApiException, Throwable {
        JAXBContentType jaxbContentType = null;
        try {
            String typeCode = properties.getProperty("code");
            Content masterContentType = (Content) this.getContentManager().getEntityPrototype(typeCode);
            if (null == masterContentType) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Content type with code '" + typeCode + "' does not exist");
            }
            jaxbContentType = new JAXBContentType(masterContentType);
            jaxbContentType.setDefaultModelId(this.extractModelId(masterContentType.getDefaultModel()));
            jaxbContentType.setListModelId(this.extractModelId(masterContentType.getListModel()));
        } catch (ApiException ae) {
            throw ae;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getContentType");
            throw new ApsSystemException("Error extracting content type", t);
        }
        return jaxbContentType;
    }
    
    private Integer extractModelId(String stringModelId) {
        if (null == stringModelId) return null;
        Integer modelId = null;
        try {
            modelId = Integer.parseInt(stringModelId);
        } catch (Throwable t) {
            //nothing to catch
        }
        return modelId;
    }
    
    public StringApiResponse addContentType(JAXBContentType jaxbContentType) throws Throwable {
        StringApiResponse response = new StringApiResponse();
        try {
            String typeCode = jaxbContentType.getTypeCode();
            Content masterContentType = (Content) this.getContentManager().getEntityPrototype(typeCode);
            if (null != masterContentType) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Content type with code '" + typeCode + "' already exists");
            }
            if (typeCode == null || typeCode.length() != 3) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Invalid type code - '" + typeCode + "'");
            }
            Map<String, AttributeInterface> attributes = this.getContentManager().getEntityAttributePrototypes();
            Content contentType = (Content) jaxbContentType.buildEntityType(this.getContentManager().getEntityClass(), attributes);
            boolean defaultModelCheck = this.checkContentModel(jaxbContentType.getDefaultModelId(), contentType, response);
            if (defaultModelCheck) {
                contentType.setDefaultModel(String.valueOf(jaxbContentType.getDefaultModelId()));
            }
            boolean listModelCheck = this.checkContentModel(jaxbContentType.getListModelId(), contentType, response);
            if (listModelCheck) {
                contentType.setListModel(String.valueOf(jaxbContentType.getListModelId()));
            }
            ((IEntityTypesConfigurer) this.getContentManager()).addEntityPrototype(contentType);
            response.setResult(IResponseBuilder.SUCCESS, null);
        } catch (ApiException ae) {
            response.addErrors(ae.getErrors());
            response.setResult(IResponseBuilder.FAILURE, null);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "addContentType");
            throw new ApsSystemException("Error adding content type", t);
        }
        return response;
    }
    
    public StringApiResponse updateContentType(JAXBContentType jaxbContentType) throws Throwable {
        StringApiResponse response = new StringApiResponse();
        try {
            String typeCode = jaxbContentType.getTypeCode();
            Content masterContentType = (Content) this.getContentManager().getEntityPrototype(typeCode);
            if (null == masterContentType) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Content type with code '" + typeCode + "' doesn't exist");
            }
            Map<String, AttributeInterface> attributes = this.getContentManager().getEntityAttributePrototypes();
            Content contentType = (Content) jaxbContentType.buildEntityType(this.getContentManager().getEntityClass(), attributes);
            boolean defaultModelCheck = this.checkContentModel(jaxbContentType.getDefaultModelId(), contentType, response);
            if (defaultModelCheck) {
                contentType.setDefaultModel(String.valueOf(jaxbContentType.getDefaultModelId()));
            }
            boolean listModelCheck = this.checkContentModel(jaxbContentType.getListModelId(), contentType, response);
            if (listModelCheck) {
                contentType.setListModel(String.valueOf(jaxbContentType.getListModelId()));
            }
            ((IEntityTypesConfigurer) this.getContentManager()).updateEntityPrototype(contentType);
            response.setResult(IResponseBuilder.SUCCESS, null);
        } catch (ApiException ae) {
            response.addErrors(ae.getErrors());
            response.setResult(IResponseBuilder.FAILURE, null);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "updateContentType");
            throw new ApsSystemException("Error updating content type", t);
        }
        return response;
    }
    
    private boolean checkContentModel(Integer modelId, Content contentType, StringApiResponse response) {
        if (null == modelId) return true;
        ContentModel contentModel = this.getContentModelManager().getContentModel(modelId);
        if (null == contentModel) {
            ApiError error = new ApiError(IApiErrorCodes.API_VALIDATION_ERROR, "Content model with id '" + modelId + "' does not exist");
            response.addError(error);
            return false;
        }
        if (!contentType.getTypeCode().equals(contentModel.getContentType())) {
            ApiError error = new ApiError(IApiErrorCodes.API_VALIDATION_ERROR, "Content model with id '" 
                    + modelId + "' is for contents of type '" + contentModel.getContentType() + "'");
            response.addError(error);
            return false;
        }
        return true;
    }
    
    public void deleteContentType(Properties properties) throws ApiException, Throwable {
        try {
            String typeCode = properties.getProperty("code");
            Content masterContentType = (Content) this.getContentManager().getEntityPrototype(typeCode);
            if (null == masterContentType) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Content type with code '" + typeCode + "' doesn't exist");
            }
            EntitySearchFilter filter = new EntitySearchFilter(IEntityManager.ENTITY_TYPE_CODE_FILTER_KEY, false, typeCode, false);
            List<String> contentIds = this.getContentManager().searchId(new EntitySearchFilter[]{filter});
            if (null != contentIds && !contentIds.isEmpty()) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Content type '" + typeCode + "' are used into " + contentIds.size() + " contents");
            }
            ((IEntityTypesConfigurer) this.getContentManager()).removeEntityPrototype(typeCode);
        } catch (ApiException ae) {
            throw ae;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "deleteContentType");
            throw new ApsSystemException("Error deleting content type", t);
        }
    }
    
}
