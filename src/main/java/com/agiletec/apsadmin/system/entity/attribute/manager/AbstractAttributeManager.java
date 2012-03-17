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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.model.AttributeFieldError;
import com.agiletec.aps.system.common.entity.model.FieldError;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.util.OgnlValidationRule;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.i18n.II18nManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This abstract class is the base for the managers of all attributes.
 * For the 'complex' attributes this class must be directly extended, otherwise
 * -for 'simple' attributes- this class is extended by the managers of the 
 * 'mono-language' and 'multi-language' Attributes.
 * @author E.Santoboni
 */
public abstract class AbstractAttributeManager implements AttributeManagerInterface {
    
    @Deprecated
    public void checkEntityAttribute(ActionSupport action, Map<String, AttributeManagerInterface> attributeManagers, AttributeInterface attribute, IApsEntity entity) {
        this.setAttributeManagers(attributeManagers);
        this.checkAttribute(action, attribute, new AttributeTracer(), entity);
    }

    /**
     * Basic method for attribute checking.
     * This method knows in advance all the possible combinations of attributes in the system;
     * if new combinations are needed is it necessary to modify this method and
     * define the coherency checks and the control logic.
     * This method must be modified, together with the 'tracer' class, if new 'complex'
     * attributes are added so to properly manages the messages related to the new combinations available.
     * With 'complex' attributes this method must be always extended.
     *  
     * @param action The action to fill with the appropriate errors, if any
     * @param attribute The current attribute, both 'simple' or 'complex', to check.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one. 
     * @param entity The entity to check.
     * @deprecated 
     */
    protected void checkAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        if (tracer.isMonoListElement()) {
            if (tracer.isCompositeElement()) {
                this.checkMonoListCompositeElement(action, attribute, tracer, entity);
            } else {
                this.checkMonoListElement(action, attribute, tracer, entity);
            }
        } else if (tracer.isListElement()) {
            this.checkListElement(action, attribute, tracer, entity);
        } else {
            this.checkSingleAttribute(action, attribute, tracer, entity);
        }
        this.checkExpression(action, attribute, tracer, entity);
    }

    @Deprecated
    protected void checkExpression(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        OgnlValidationRule ognlValidationRule = attribute.getValidationRules().getOgnlValidationRule();
        if (null == ognlValidationRule) {
            return;
        }
        String expression = ognlValidationRule.getExpression();
        if (null == expression || expression.trim().length() == 0) {
            return;
        }
        if (ognlValidationRule.isEvalExpressionOnValuedAttribute() && this.getState(attribute, tracer) == EMPTY_ATTRIBUTE_STATE) {
            return;
        }
        try {
            Object expr = Ognl.parseExpression(expression);
            OgnlContext ctx = this.createContextForExpressionValidation(attribute, tracer, entity);
            Boolean value = (Boolean) Ognl.getValue(expr, ctx, attribute, Boolean.class);
            if (!value) {
                String messageAttributePositionPrefix = this.createErrorMessageAttributePositionPrefix(action, attribute, tracer);
                String formFieldName = tracer.getFormFieldName(attribute);
                String ognlMessage = this.getOgnlExpressionMessage(ognlValidationRule, action);
                action.addFieldError(formFieldName, messageAttributePositionPrefix + " " + ognlMessage);
            }
        } catch (OgnlException e) {
            ApsSystemUtils.logThrowable(e, this, "checkExpression", "Error on evaluation of expression : " + expression);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "checkExpression");
            throw new RuntimeException("Generic Error on evaluation Ognl Expression", t);
        }
    }

    @Deprecated
    protected OgnlContext createContextForExpressionValidation(AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        OgnlContext context = new OgnlContext();
        if (null != this.getLangManager()) {
            Map<String, Lang> langs = new HashMap<String, Lang>();
            List<Lang> langList = this.getLangManager().getLangs();
            for (int i = 0; i < langList.size(); i++) {
                Lang lang = langList.get(i);
                langs.put(lang.getCode(), lang);
            }
            context.put("langs", langs);
        }
        context.put("attribute", attribute);
        context.put("entity", attribute.getParentEntity());
        if (tracer.isCompositeElement()) {
            context.put("parent", tracer.getParentAttribute());
        } else {
            if (tracer.isListElement() || tracer.isMonoListElement()) {
                context.put("parent", entity.getAttribute(attribute.getName()));
                context.put("index", tracer.getListIndex());
            }
            if (tracer.isListElement()) {
                context.put("listLang", tracer.getListLang());
            }
        }
        return context;
    }

    @Deprecated
    protected String getOgnlExpressionMessage(OgnlValidationRule ognlValidationRule, ActionSupport action) throws ApsSystemException {
        String ognlMessage = ognlValidationRule.getErrorMessage();
        if (null != ognlMessage && ognlMessage.trim().length() > 0) {
            return ognlMessage;
        }
        try {
            String labelKey = ognlValidationRule.getErrorMessageKey();
            Lang currentLang = this.getLangManager().getDefaultLang();
            if (action instanceof BaseAction) {
                currentLang = ((BaseAction) action).getCurrentLang();
            }
            String label = this.getI18nManager().getLabel(labelKey, currentLang.getCode());
            ognlMessage = (label != null) ? label : labelKey;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getOgnlExpressionMessage");
            throw new ApsSystemException("Error on extracting Ognl Expression Message", t);
        }
        return ognlMessage;
    }

    /**
     * Check the simple attribute when it is inserted as an element of a 'composite' attribute
     * in a monolist.
     * Extend this method in the helper class of the attribute which, due to its nature, 
     * requires further checks other the "general" (standard) ones,
     * when inserted in a composite attribute of a monolist. 
     * 
     * @param action The action to fill with the proper error messages, if any.
     * @param attribute The current Attribute to check, both 'simple' or 'complex'.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @param entity The entity to check.
     * @deprecated 
     */
    protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        if (!this.isValidMonoListCompositeElement(attribute, tracer)) {
            this.addFieldError(action, attribute, tracer, this.getMonoListCompositeElementNotValidMessage(), null);
        } else if (attribute.isRequired() && this.getState(attribute, tracer) != VALUED_ATTRIBUTE_STATE) {
            this.addFieldError(action, attribute, tracer, this.getRequiredAttributeMessage(), null);
        }
    }

    /**
     * This method defines a standard of validity for an attribute inserted as an
     * element of a composite attribute in a monolist.
     * This method must be extended in all the attribute managers which must
     * enforce the standard validity checks with additional controls when inserting
     * a new element of a composite attribute in a monolist.
     * 
     * @param attribute The current attribute to check, both 'simple' or 'complex'.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @return true If the element is valid, false otherwise.
     * @deprecated 
     */
    protected boolean isValidMonoListCompositeElement(AttributeInterface attribute, AttributeTracer tracer) {
        return this.getState(attribute, tracer) != INCOMPLETE_ATTRIBUTE_STATE;
    }

    /**
     * This return the key of the message to return if the attribute of a composite
     * element in a monolist is not valid.
     * If the nature of the method requires particular message is necessary to override this method.
     * 
     * @return The message to return.
     * @deprecated 
     */
    protected String getMonoListCompositeElementNotValidMessage() {
        return this.getInvalidAttributeMessage();
    }

    /**
     * Check the 'simple' attribute when inserted as element in a monolist.
     * Extend this method in the helper class of the attribute which, due to its nature, 
     * requires further checks other the "general" (standard) ones,
     * when inserted as element of a monolist.
     * 
     * @param action The action to fill with the proper error messages, if any.
     * @param attribute The current attribute to check, both 'simple' or 'complex'.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @param entity The entity to check.
     * @deprecated 
     */
    protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        if (!this.isValidMonoListElement(attribute, tracer)) {
            this.addFieldError(action, attribute, tracer, this.getMonoListElementNotValidMessage(), null);
        }
    }

    /**
     * This method defines a standard of validity for an attribute inserted as an
     * element in a monolist.
     * This method must be extended in all the attribute managers which must
     * enforce the standard validity checks with additional controls when inserting
     * a new element in a monolist.
     * 
     * @param attribute The current attribute to check, both 'simple' or 'complex'.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @return true If the element is valid, false otherwise.
     * @deprecated 
     */
    protected boolean isValidMonoListElement(AttributeInterface attribute, AttributeTracer tracer) {
        return this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE;
    }

    /**
     * This return the key of the message to return if the attribute element in a 
     * monolist is not valid. If the nature of the method requires particular message
     * is necessary to override this method. 
     * 
     * @return The message to return.
     * @deprecated 
     */
    protected String getMonoListElementNotValidMessage() {
        return this.getInvalidAttributeMessage();
    }

    /**
     * Check the validity of the simple attribute when it is inserted in a multi-language list.
     * Extend this method in the helper class of the attribute which, due to its nature, 
     * requires further checks other the "general" (standard) ones, when inserted as 
     * element of a multi-language list.
     * 
     * @param action The action to fill with the proper error messages, if any.
     * @param attribute The current attribute to check, both 'simple' or 'complex'.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @param entity The entity to check.
     * @deprecated 
     */
    protected void checkListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        if (!this.isValidListElement(attribute, tracer)) {
            this.addFieldError(action, attribute, tracer, this.getListElementNotValidMessage(), null);
        }
    }

    /**
     * This method defines a standard of validity for an attribute inserted as an
     * element in a multi-language list.
     * This method must be extended in all the attribute managers which must
     * enforce the standard validity checks with additional controls when inserting
     * a new element in a multi-language list.
     * 
     * @param attribute The current attribute to check, both 'simple' or 'complex'.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @return true If the element is valid, false otherwise.
     * @deprecated 
     */
    protected boolean isValidListElement(AttributeInterface attribute, AttributeTracer tracer) {
        return this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE;
    }

    /**
     * This return the key of the message to return if the attribute element in a 
     * multi-language list is not valid.
     * If the nature of the method requires particular message is necessary to override this method. 
     * 
     * @return The message to return.
     * @deprecated 
     */
    protected String getListElementNotValidMessage() {
        return this.getInvalidAttributeMessage();
    }

    /**
     * Return the key of the message to retrieve when an attribute is not valid.
     * If a customized message is needed eg. due to the nature of the attribute, extend this method.
     * @return The key of the message to return. 
     */
    protected String getInvalidAttributeMessage() {
        return "EntityAttribute.fieldError.invalidAttribute";
    }

    /**
     * This method implements the validity criteria of an attribute, both simple or
     * complex; the only check performed here is whether the attribute is mandatory or not. 
     * If the attribute needs further checks other than those by default, extend this method.
     * 
     * @param action The action to fill with the proper error messages, if any.
     * @param attribute The current attribute to check, both 'simple' or 'complex'.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @param entity The entity to check.
     * @deprecated 
     */
    protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
        if (attribute.isRequired() && this.getState(attribute, tracer) == EMPTY_ATTRIBUTE_STATE) {
            this.addFieldError(action, attribute, tracer, this.getRequiredAttributeMessage(), null);
        }
    }

    /**
     * Return the key of the message used when a mandatory attribute is not populated.
     * If a customized message is needed eg. due to the nature of the attribute, extend this method.
     * 
     * @return The key of the message to return.
     */
    protected String getRequiredAttributeMessage() {
        return "EntityAttribute.fieldError.required";
    }

    /**
     * Add an error message related to a field in a form.
     * 
     * @param action The action where the error message is added
     * @param fieldName The name of the field.
     * @param messageKey The key of the error message.
     * @param args The arguments of the error message.
     * @deprecated 
     */
    protected void addFieldError(ActionSupport action, String fieldName, String messageKey, String[] args) {
        action.addFieldError(fieldName, action.getText(messageKey, args));
    }

    /**
     * Add an error message related to a field in a form.
     * 
     * @param action The action where the error message is added.
     * @param attribute The current attribute (simple or complex) to which the error message is appended.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @param messageKey The key of the error message.
     * @param args The arguments of the error message.
     * @deprecated 
     */
    protected void addFieldError(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, String messageKey, String[] args) {
        String messageAttributePositionPrefix = this.createErrorMessageAttributePositionPrefix(action, attribute, tracer);
        String messageError = null;
        if (args != null) {
            messageError = action.getText(messageKey, args);
        } else {
            messageError = action.getText(messageKey);
        }
        String formFieldName = tracer.getFormFieldName(attribute);
        action.addFieldError(formFieldName, messageAttributePositionPrefix + " " + messageError);
    }

    private String createErrorMessageAttributePositionPrefix(ActionSupport action, AttributeInterface attribute, com.agiletec.aps.system.common.entity.model.AttributeTracer tracer) {
        if (tracer.isMonoListElement()) {
            if (tracer.isCompositeElement()) {
                String[] args = {tracer.getParentAttribute().getName(), String.valueOf(tracer.getListIndex() + 1), attribute.getName()};
                return action.getText("EntityAttribute.compositeListAttributeElement.errorMessage.prefix", args);
            } else {
                String[] args = {attribute.getName(), String.valueOf(tracer.getListIndex() + 1)};
                return action.getText("EntityAttribute.monolistAttributeElement.errorMessage.prefix", args);
            }
        } else if (tracer.isCompositeElement()) {
            String[] args = {tracer.getParentAttribute().getName(), attribute.getName()};
            return action.getText("EntityAttribute.compositeAttributeElement.errorMessage.prefix", args);
        } else if (tracer.isListElement()) {
            String[] args = {attribute.getName(), tracer.getListLang().getDescr(), String.valueOf(tracer.getListIndex() + 1)};
            return action.getText("EntityAttribute.listAttributeElement.errorMessage.prefix", args);
        } else {
            String[] args = {attribute.getName()};
            return action.getText("EntityAttribute.singleAttribute.errorMessage.prefix", args);
        }
    }

    /**
     * Return the status of the current attribute.
     * The status can be: EMPTY, INCOMPLETE, POPULATED.
     * INCOMPLETE applies only to those simple attributes composed by two or more elements
     * (eg. the 'image' attribute is composed by a resource and a description).
     * This method applies to both simple and complex attributes.
     * This check does neither imply that the attribute is valid nor complete: eg. for
     * the 'number' attribute this method does not check for the given string to be a number,
     * but it rather checks for the presence of the string; in the List attribute at least one
     * element -whether correct or not- must be present. 
     * 
     * @param attribute The current attribute (simple or complex) to check.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @return The code representing the current status
     * @deprecated 
     */
    protected abstract int getState(AttributeInterface attribute, AttributeTracer tracer);
    
    public void updateEntityAttribute(AttributeInterface attribute, Map<String, AttributeManagerInterface> attributeManagers, HttpServletRequest request) {
        this.setAttributeManagers(attributeManagers);
        this.updateAttribute(attribute, new AttributeTracer(), request);
    }

    /**
     * Updates the attribute with the criteria specified in the content editing form.
     * 
     * @param attribute The current attribute (simple or complex) to check.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @param request The request.
     */
    protected abstract void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request);
    
    /**
     * Return the value of the current attribute passed from the form.
     * 
     * @param attribute The current attribute (simple or complex) to check.
     * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
     * @param request The request.
     * @return The value passed in the form
     */
    protected String getValueFromForm(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
        String formFieldName = tracer.getFormFieldName(attribute);
        return request.getParameter(formFieldName);
    }

    protected AttributeManagerInterface getManager(String typeCode) {
        AbstractAttributeManager manager = (AbstractAttributeManager) this.getAttributeManagers().get(typeCode);
        AbstractAttributeManager clone = null;
        try {
            Class attributeClass = Class.forName(manager.getClass().getName());
            clone = (AbstractAttributeManager) attributeClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not clone the attribute manager '" + this.getClass().getName() + "'");
        }
        clone.setAttributeManagers(this.getAttributeManagers());
        manager.setExtraPropertyTo(clone);
        return clone;
    }

    /**
     * Set the extra properties in the given manager.
     * This method is used when creating a manager to handle the attribute element of a complex
     * attribute and must be implemented when setting extra attributes.
     * @param manager The manager to create.
     */
    protected void setExtraPropertyTo(AttributeManagerInterface manager) {
        //nothing to do
    }

    public void validate(ActionSupport action, com.agiletec.aps.system.common.entity.model.AttributeTracer tracer, AttributeInterface attribute) {
        try {
            List<AttributeFieldError> errors = attribute.validate(tracer);
            if (null != errors && errors.size() > 0) {
                for (int i = 0; i < errors.size(); i++) {
                    AttributeFieldError attributeFieldError = errors.get(i);
                    this.addFieldError(action, attribute, attributeFieldError);
                }
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "validate");
            throw new RuntimeException("Error in validate", t);
        }
    }
    
    protected void addFieldError(ActionSupport action, AttributeInterface attribute, AttributeFieldError attributeFieldError) {
        com.agiletec.aps.system.common.entity.model.AttributeTracer tracer = attributeFieldError.getTracer();
        String messageAttributePositionPrefix = this.createErrorMessageAttributePositionPrefix(action, attribute, tracer);
        String errorMessage = this.getErrorMessage(attributeFieldError, action, attribute);
        String formFieldName = tracer.getFormFieldName(attribute);
        action.addFieldError(formFieldName, messageAttributePositionPrefix + " " + errorMessage);
    }
    
    protected String getErrorMessage(AttributeFieldError attributeFieldError, ActionSupport action, AttributeInterface attribute) {
        try {
            String errorCode = attributeFieldError.getErrorCode();
            if (errorCode.equals(FieldError.MANDATORY)) {
                return action.getText(this.getRequiredAttributeMessage());
            } else if (errorCode.equals(AttributeFieldError.OGNL_VALIDATION)) {
                String message = attributeFieldError.getMessage();
                if (null != message && message.trim().length() > 0) {
                    return message;
                }
                String label = null;
                String labelKey = attributeFieldError.getMessageKey();
                if (null != labelKey && labelKey.trim().length() > 0) {
                    Lang currentLang = this.getLangManager().getDefaultLang();
                    if (action instanceof BaseAction) {
                        currentLang = ((BaseAction) action).getCurrentLang();
                    }
                    label = this.getI18nManager().getLabel(labelKey, currentLang.getCode());
                }
                if (label != null) {
                    return label;
                } else return this.getCustomAttributeErrorMessage(attributeFieldError, action, attribute);
            } else {
                return this.getCustomAttributeErrorMessage(attributeFieldError, action, attribute);
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getErrorMessage");
            throw new RuntimeException("Error creating Error Message", t);
        }
    }

    /**
     * Return a custom error message.
     * This method shouwld to be extended for custom attribute manager
     * @param errorCode The error code 
     * @return The message for the specific error code.
     */
    protected String getCustomAttributeErrorMessage(AttributeFieldError attributeFieldError, ActionSupport action, AttributeInterface attribute) {
        return action.getText(this.getInvalidAttributeMessage());
    }
    
    private Map<String, AttributeManagerInterface> getAttributeManagers() {
        return this._attributeManagers;
    }
    private void setAttributeManagers(Map<String, AttributeManagerInterface> attributeManagers) {
        this._attributeManagers = attributeManagers;
    }

    protected II18nManager getI18nManager() {
        return _i18nManager;
    }
    public void setI18nManager(II18nManager i18nManager) {
        this._i18nManager = i18nManager;
    }

    /**
     * Return the manager of the system languages.
     * @return The manager of the system languages.
     */
    protected ILangManager getLangManager() {
        return _langManager;
    }

    /**
     * Set the manager of the system languages.
     * @param langManager The manager that handles the language.
     */
    public void setLangManager(ILangManager langManager) {
        this._langManager = langManager;
    }
    private Map<String, AttributeManagerInterface> _attributeManagers;
    private II18nManager _i18nManager;
    private ILangManager _langManager;
    
    /**
     * Constant code describing the status of the empty attribute.
     * @deprecated 
     */
    protected final int EMPTY_ATTRIBUTE_STATE = 0;
    
    /**
     * Constant code describing the status of the incomplete attribute (not properly populated). Please note
     * that this status cannot be never accepted by the system.
     * @deprecated 
     */
    protected final int INCOMPLETE_ATTRIBUTE_STATE = 1;
    
    /**
     * Constant code describing the status of the valued attribute.
     * @deprecated 
     */
    protected final int VALUED_ATTRIBUTE_STATE = 2;
    
}