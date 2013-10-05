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
package com.agiletec.plugins.jacms.apsadmin.content.attribute.manager;

import com.agiletec.aps.system.common.entity.model.AttributeFieldError;
import java.util.List;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.util.HtmlHandler;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.agiletec.apsadmin.system.entity.attribute.manager.AttributeManagerInterface;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.util.HypertextAttributeUtil;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.util.ICmsAttributeErrorCodes;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.manager.util.ISymbolicLinkErrorMessenger;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Classe manager degli attributi tipo Hypertext.
 * @author E.Santoboni
 */
public class HypertextAttributeManager extends com.agiletec.apsadmin.system.entity.attribute.manager.TextAttributeManager {
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        super.checkSingleAttribute(action, attribute, tracer, entity);
        this.checkHypertext(action, attribute, tracer, entity);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected Object getValue(AttributeInterface attribute, Lang lang) {
        String text = (String) super.getValue(attribute, lang);
        if (text != null) {
            HtmlHandler htmlhandler = new HtmlHandler();
            String parsedText = htmlhandler.getParsedText(text).trim();
            if (parsedText.length() > 0) {
                return parsedText;
            }
        }
        return null;
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        super.checkMonoListElement(action, attribute, tracer, entity);
        this.checkHypertext(action, attribute, tracer, entity);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        super.checkMonoListCompositeElement(action, attribute, tracer, entity);
        this.checkHypertext(action, attribute, tracer, entity);
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkHypertext(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        int state = this.getState(attribute, tracer);
        if (state == VALUED_ATTRIBUTE_STATE) {
            List<Lang> langs = this.getLangManager().getLangs();
            for (int i = 0; i < langs.size(); i++) {
                Lang lang = langs.get(i);
                String value = (String) super.getValue(attribute, lang);
                List<SymbolicLink> symbLinks = HypertextAttributeUtil.getSymbolicLinksOnText(value);
                for (int j = 0; j < symbLinks.size(); j++) {
                    SymbolicLink symbLink = symbLinks.get(j);
                    int errorCode = this.getSymbolicLinkErrorMessenger().scan(symbLink, (Content) entity);
                    if (errorCode != ISymbolicLinkErrorMessenger.MESSAGE_CODE_NO_ERROR) {
                        String messageKey = null;
                        switch (errorCode) {
                            case ISymbolicLinkErrorMessenger.MESSAGE_CODE_INVALID_PAGE:
                                messageKey = "HypertextAttribute.fieldError.linkToPage";
                                break;
                            case ISymbolicLinkErrorMessenger.MESSAGE_CODE_VOID_PAGE:
                                messageKey = "HypertextAttribute.fieldError.linkToPage.voidPage";
                                break;
                            case ISymbolicLinkErrorMessenger.MESSAGE_CODE_INVALID_CONTENT:
                                messageKey = "HypertextAttribute.fieldError.linkToContent";
                                break;
                            case ISymbolicLinkErrorMessenger.MESSAGE_CODE_INVALID_PAGE_GROUPS:
                                messageKey = "HypertextAttribute.fieldError.linkToPage.wrongGroups";
                                break;
                            case ISymbolicLinkErrorMessenger.MESSAGE_CODE_INVALID_CONTENT_GROUPS:
                                messageKey = "HypertextAttribute.fieldError.linkToContent.wrongGroups";
                                break;
                        }
                        String[] args = {lang.getDescr()};
                        this.addFieldError(action, attribute, tracer, messageKey, args);
                    }
                }
            }
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void setExtraPropertyTo(AttributeManagerInterface manager) {
        super.setExtraPropertyTo(manager);
        ((HypertextAttributeManager) manager).setSymbolicLinkErrorMessenger(this.getSymbolicLinkErrorMessenger());
    }
    
    protected String getCustomAttributeErrorMessage(AttributeFieldError attributeFieldError, ActionSupport action) {
        String errorCode = attributeFieldError.getErrorCode();
        String messageKey = null;
        if (errorCode.equals(ICmsAttributeErrorCodes.INVALID_PAGE)) {
            messageKey = "HypertextAttribute.fieldError.linkToPage";
        } else if (errorCode.equals(ICmsAttributeErrorCodes.VOID_PAGE)) {
            messageKey = "HypertextAttribute.fieldError.linkToPage.voidPage";
        } else if (errorCode.equals(ICmsAttributeErrorCodes.INVALID_CONTENT)) {
            messageKey = "HypertextAttribute.fieldError.linkToContent";
        } else if (errorCode.equals(ICmsAttributeErrorCodes.INVALID_PAGE_GROUPS)) {
            messageKey = "HypertextAttribute.fieldError.linkToPage.wrongGroups";
        } else if (errorCode.equals(ICmsAttributeErrorCodes.INVALID_CONTENT_GROUPS)) {
            messageKey = "HypertextAttribute.fieldError.linkToContent.wrongGroups";
        }
        if (null != messageKey) {
            String[] args = {attributeFieldError.getTracer().getLang().getDescr()};
            return action.getText(messageKey, args);
        } else {
            return super.getCustomAttributeErrorMessage(attributeFieldError, action);
        }
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
