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
package org.entando.entando.aps.system.services.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author E.Santoboni
 */
public abstract class AbstractApiResponse implements Serializable {
    
    public abstract void setResult(Object result, String html);
    
    public void addError(ApiError error) {
        if (null != error) {
            this._errors.add(error);
        }
    }

    public void addErrors(List<ApiError> errors) {
        if (null == errors) {
            return;
        }
        this._errors.addAll(errors);
    }
    
    @XmlElement(name = "error", required = true)
    @XmlElementWrapper(name = "errors")
    private List<ApiError> _errors = new ArrayList<ApiError>();
    
}