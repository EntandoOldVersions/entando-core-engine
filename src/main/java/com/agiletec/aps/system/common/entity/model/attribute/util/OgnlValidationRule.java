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
package com.agiletec.aps.system.common.entity.model.attribute.util;

import org.jdom.CDATA;
import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class OgnlValidationRule {
	
	public OgnlValidationRule() {}
	
	public OgnlValidationRule(Element element) {
		if (null == element) {
			throw new RuntimeException("null jdom element");
		}
		String eval = element.getAttributeValue("evalOnValuedAttribute");
		this.setEvalExpressionOnValuedAttribute(null != eval && eval.equalsIgnoreCase("true"));
		Element ognlExpressionElement = element.getChild("ognlexpression");
		this.setExpression(ognlExpressionElement.getText());
		Element errorMessageElement = element.getChild("errormessage");
		if (null != errorMessageElement) {
			this.setErrorMessage(errorMessageElement.getText());
			this.setErrorMessageKey(errorMessageElement.getAttributeValue("key"));
		}
		Element helpMessageElement = element.getChild("helpmessage");
		if (null != helpMessageElement) {
			this.setHelpMessage(helpMessageElement.getText());
			this.setHelpMessageKey(helpMessageElement.getAttributeValue("key"));
		}
	}
	
	@Override
	protected OgnlValidationRule clone() {
		OgnlValidationRule clone = new OgnlValidationRule();
		clone.setErrorMessage(this.getErrorMessage());
		clone.setErrorMessageKey(this.getErrorMessageKey());
		clone.setEvalExpressionOnValuedAttribute(this.isEvalExpressionOnValuedAttribute());
		clone.setExpression(this.getExpression());
		clone.setHelpMessage(this.getHelpMessage());
		clone.setHelpMessageKey(this.getHelpMessageKey());
		return clone;
	}
	
	public Element getConfigElement() {
		if (null == this.getExpression() || this.getExpression().trim().length() == 0) return null;
		Element exprElement = new Element("expression");
		exprElement.setAttribute("evalOnValuedAttribute", String.valueOf(this.isEvalExpressionOnValuedAttribute()));
		Element ognlExprElement = new Element("ognlexpression");
		CDATA cdata = new CDATA(this.getExpression());
		ognlExprElement.addContent(cdata);
		exprElement.addContent(ognlExprElement);
		Element errorMessageElement = new Element("errormessage");
		if (null != this.getErrorMessageKey() && this.getErrorMessageKey().trim().length() > 0) {
			errorMessageElement.setAttribute("key", this.getErrorMessageKey());
		}
		if (null != this.getErrorMessage() && this.getErrorMessage().trim().length() > 0) {
			CDATA label = new CDATA(this.getErrorMessage());
			errorMessageElement.addContent(label);
		}
		exprElement.addContent(errorMessageElement);
		Element helpMessageElement = new Element("helpmessage");
		if (null != this.getHelpMessageKey() && this.getHelpMessageKey().trim().length() > 0) {
			helpMessageElement.setAttribute("key", this.getHelpMessageKey());
		}
		if (null != this.getHelpMessage() && this.getHelpMessage().trim().length() > 0) {
			CDATA label = new CDATA(this.getHelpMessage());
			helpMessageElement.addContent(label);
		}
		exprElement.addContent(helpMessageElement);
		return exprElement;
	}
	
	public String getExpression() {
		return _expression;
	}
	public void setExpression(String expression) {
		this._expression = expression;
	}
	public boolean isEvalExpressionOnValuedAttribute() {
		return _evalExpressionOnValuedAttribute;
	}
	public void setEvalExpressionOnValuedAttribute(boolean evalExpressionOnValuedAttribute) {
		this._evalExpressionOnValuedAttribute = evalExpressionOnValuedAttribute;
	}
	public String getErrorMessage() {
		return _errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this._errorMessage = errorMessage;
	}
	public String getErrorMessageKey() {
		return _errorMessageKey;
	}
	public void setErrorMessageKey(String errorMessageKey) {
		this._errorMessageKey = errorMessageKey;
	}
	public String getHelpMessage() {
		return _helpMessage;
	}
	public void setHelpMessage(String helpMessage) {
		this._helpMessage = helpMessage;
	}
	public String getHelpMessageKey() {
		return _helpMessageKey;
	}
	public void setHelpMessageKey(String helpMessageKey) {
		this._helpMessageKey = helpMessageKey;
	}
	
	private String _expression;
	private boolean _evalExpressionOnValuedAttribute;
	private String _errorMessage;
	private String _errorMessageKey;
	private String _helpMessage;
	private String _helpMessageKey;
	
}