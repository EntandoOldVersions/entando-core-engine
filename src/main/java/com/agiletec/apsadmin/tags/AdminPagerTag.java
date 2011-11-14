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
package com.agiletec.apsadmin.tags;

import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts2.util.SubsetIteratorFilter;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

import com.agiletec.aps.tags.util.IPagerVO;
import com.agiletec.apsadmin.tags.util.AdminPagerTagHelper;
import com.agiletec.apsadmin.tags.util.ComponentPagerVO;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * This tag is intend for the administration interface only.
 * Takes an iterator and outputs a subset of it.
 * @author E.Santoboni
 */
public class AdminPagerTag extends StrutsBodyTagSupport {
	
	public int doStartTag() throws JspException {
		Object source = this.findValue(_sourceAttr);
		ServletRequest request =  this.pageContext.getRequest();
		
		ValueStack stack = this.getStack();
		ComponentPagerVO compPagerVo = new ComponentPagerVO(stack);
		try {
			AdminPagerTagHelper helper = new AdminPagerTagHelper();
			IPagerVO pagerVo = helper.getPagerVO((Collection)source, 
					this.getPagerId(), this._countAttr, this.isAdvanced(), this.getOffset(), request);
			compPagerVo.initPager(pagerVo);
			stack.getContext().put(this.getObjectName(), compPagerVo);
			stack.setValue("#attr['" + this.getObjectName() + "']", compPagerVo, false);
		} catch (Throwable t) {
			throw new JspException("Error creating the pager", t);
		}
		
		_subsetIteratorFilter = new SubsetIteratorFilter();
		_subsetIteratorFilter.setCount(this._countAttr);
		_subsetIteratorFilter.setDecider(null);
		_subsetIteratorFilter.setSource(source);
		_subsetIteratorFilter.setStart(compPagerVo.getBegin());
		_subsetIteratorFilter.execute();
		this.getStack().push(_subsetIteratorFilter);
		if (getId() != null) {
			pageContext.setAttribute(getId(), _subsetIteratorFilter);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	public String getPagerId() {
		return _pagerId;
	}
	public void setPagerId(String pagerId) {
		this._pagerId = pagerId;
	}
	
	@Override
	public int doEndTag() throws JspException {
		this.getStack().pop();
		_subsetIteratorFilter = null;
		return EVAL_PAGE;
	}

	public void setCount(int count) {
		_countAttr = count;
	}
	public void setSource(String source) {
		_sourceAttr = source;
	}

	protected String getObjectName() {
		return _objectName;
	}
	public void setObjectName(String objectName) {
		this._objectName = objectName;
	}

	protected boolean isAdvanced() {
		return _advanced;
	}
	public void setAdvanced(boolean advanced) {
		this._advanced = advanced;
	}
	
	protected int getOffset() {
		return _offset;
	}
	public void setOffset(int offset) {
		this._offset = offset;
	}
	
	private String _pagerId;
	
	private int _countAttr;
	private String _sourceAttr;

	private SubsetIteratorFilter _subsetIteratorFilter = null;

	private String _objectName;

	private int _offset;
	private boolean _advanced;

}
