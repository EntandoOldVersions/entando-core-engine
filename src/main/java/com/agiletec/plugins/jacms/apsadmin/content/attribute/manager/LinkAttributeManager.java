/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
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
package com.agiletec.plugins.jacms.apsadmin.content.attribute.manager;

import com.agiletec.aps.system.common.entity.model.AttributeFieldError;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.agiletec.apsadmin.system.entity.attribute.manager.AttributeManagerInterface;
import com.agiletec.apsadmin.system.entity.attribute.manager.TextAttributeManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.LinkAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.util.ICmsAttributeErrorCodes;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.manager.util.ISymbolicLinkErrorMessenger;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Classe manager degli attributi tipo link.
 * @author E.Santoboni
 */
public class LinkAttributeManager extends TextAttributeManager {
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        super.checkSingleAttribute(action, attribute, tracer, entity);
        int state = this.getState(attribute, tracer);
        if (state == INCOMPLETE_ATTRIBUTE_STATE) {
            this.addFieldError(action, attribute, tracer, this.getInvalidAttributeMessage(), null);
        }
        this.checkLink(action, attribute, tracer, entity);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        super.checkMonoListCompositeElement(action, attribute, tracer, entity);
        this.checkLink(action, attribute, tracer, entity);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        super.checkMonoListElement(action, attribute, tracer, entity);
        this.checkLink(action, attribute, tracer, entity);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkLink(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        int state = this.getState(attribute, tracer);
        if (state == VALUED_ATTRIBUTE_STATE) {
            SymbolicLink symbLink = ((LinkAttribute) attribute).getSymbolicLink();
            int errorCode = this.getSymbolicLinkErrorMessenger().scan(symbLink, (Content) entity);
            if (errorCode != ISymbolicLinkErrorMessenger.MESSAGE_CODE_NO_ERROR) {
                String messageKey = null;
                switch (errorCode) {
                    case ISymbolicLinkErrorMessenger.MESSAGE_CODE_INVALID_PAGE:
                        messageKey = "LinkAttribute.fieldError.linkToPage";
                        break;
                    case ISymbolicLinkErrorMessenger.MESSAGE_CODE_VOID_PAGE:
                        messageKey = "LinkAttribute.fieldError.linkToPage.voidPage";
                        break;
                    case ISymbolicLinkErrorMessenger.MESSAGE_CODE_INVALID_CONTENT:
                        messageKey = "LinkAttribute.fieldError.linkToContent";
                        break;
                    case ISymbolicLinkErrorMessenger.MESSAGE_CODE_INVALID_PAGE_GROUPS:
                        messageKey = "LinkAttribute.fieldError.linkToPage.wrongGroups";
                        break;
                    case ISymbolicLinkErrorMessenger.MESSAGE_CODE_INVALID_CONTENT_GROUPS:
                        messageKey = "LinkAttribute.fieldError.linkToContent.wrongGroups";
                        break;
                }
                this.addFieldError(action, attribute, tracer, messageKey, null);
            }
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected int getState(AttributeInterface attribute, AttributeTracer tracer) {
        boolean isTextValued = super.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE;
        boolean isLinkValued = ((LinkAttribute) attribute).getSymbolicLink() != null;
        if (isLinkValued && isTextValued) {
            return VALUED_ATTRIBUTE_STATE;
        }
        if (!isLinkValued && !isTextValued) {
            return EMPTY_ATTRIBUTE_STATE;
        }
        return INCOMPLETE_ATTRIBUTE_STATE;
    }
    
    protected String getCustomAttributeErrorMessage(AttributeFieldError attributeFieldError, ActionSupport action) {
        String errorCode = attributeFieldError.getErrorCode();
        String messageKey = null;
        if (errorCode.equals(ICmsAttributeErrorCodes.INVALID_PAGE)) {
            messageKey = "LinkAttribute.fieldError.linkToPage";
        } else if (errorCode.equals(ICmsAttributeErrorCodes.VOID_PAGE)) {
            messageKey = "LinkAttribute.fieldError.linkToPage.voidPage";
        } else if (errorCode.equals(ICmsAttributeErrorCodes.INVALID_CONTENT)) {
            messageKey = "LinkAttribute.fieldError.linkToContent";
        } else if (errorCode.equals(ICmsAttributeErrorCodes.INVALID_PAGE_GROUPS)) {
            messageKey = "LinkAttribute.fieldError.linkToPage.wrongGroups";
        } else if (errorCode.equals(ICmsAttributeErrorCodes.INVALID_CONTENT_GROUPS)) {
            messageKey = "LinkAttribute.fieldError.linkToContent.wrongGroups";
        }
        if (null != messageKey) {
            return action.getText(messageKey);
        } else {
            return super.getCustomAttributeErrorMessage(attributeFieldError, action);
        }
    }
    
    protected String getInvalidAttributeMessage() {
        return "LinkAttribute.fieldError.invalidLink";
    }
    
    protected void setExtraPropertyTo(AttributeManagerInterface manager) {
        super.setExtraPropertyTo(manager);
        ((LinkAttributeManager) manager).setSymbolicLinkErrorMessenger(this.getSymbolicLinkErrorMessenger());
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected ISymbolicLinkErrorMessenger getSymbolicLinkErrorMessenger() {
        return _symbolicLinkErrorMessenger;
    }
	
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    public void setSymbolicLinkErrorMessenger(ISymbolicLinkErrorMessenger symbolicLinkErrorMessenger) {
        this._symbolicLinkErrorMessenger = symbolicLinkErrorMessenger;
    }
    
    private ISymbolicLinkErrorMessenger _symbolicLinkErrorMessenger;
    
}