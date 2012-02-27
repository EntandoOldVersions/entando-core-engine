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

import java.util.Date;
import java.util.Calendar;

import org.jdom.Element;

import com.agiletec.aps.util.DateConverter;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author E.Santoboni
 */
public class DateAttributeValidationRules extends AbstractAttributeValidationRules {
    
    protected void fillJDOMConfigElement(Element configElement) {
        super.fillJDOMConfigElement(configElement);
        String toStringEqualValue = this.toStringValue(this.getValue());
        this.insertJDOMConfigElement("value", this.getValueAttribute(), toStringEqualValue, configElement);
        String toStringStartValue = this.toStringValue(this.getRangeStart());
        this.insertJDOMConfigElement("rangestart", this.getRangeStartAttribute(), toStringStartValue, configElement);
        String toStringEndValue = this.toStringValue(this.getRangeEnd());
        this.insertJDOMConfigElement("rangeend", this.getRangeEndAttribute(), toStringEndValue, configElement);
    }
    
    private String toStringValue(Object value) {
        if (null == value) return null;
        Date date = null;
        if (value instanceof XMLGregorianCalendar) {
            XMLGregorianCalendar grCal = (XMLGregorianCalendar) value;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, grCal.getDay());
            calendar.set(Calendar.MONTH, grCal.getMonth()-1);
            calendar.set(Calendar.YEAR, grCal.getYear());
            date = calendar.getTime();
        } else if (value instanceof Date) {
            date = (Date) value;
        }
        if (null != date) {
            return DateConverter.getFormattedDate(date, DATE_PATTERN);
        }
        return null;
    }
    
    
    protected void extractValidationRules(Element validationElement) {
        super.extractValidationRules(validationElement);
        Element valueElement = validationElement.getChild("value");
        if (null != valueElement) {
            this.setValue(DateConverter.parseDate(valueElement.getText(), DATE_PATTERN));
            this.setValueAttribute(valueElement.getAttributeValue("attribute"));
        }
        Element rangeStartElement = validationElement.getChild("rangestart");
        if (null != rangeStartElement) {
            this.setRangeStart(DateConverter.parseDate(rangeStartElement.getText(), DATE_PATTERN));
            this.setRangeStartAttribute(rangeStartElement.getAttributeValue("attribute"));
        }
        Element rangeEndElement = validationElement.getChild("rangeend");
        if (null != rangeEndElement) {
            this.setRangeEnd(DateConverter.parseDate(rangeEndElement.getText(), DATE_PATTERN));
            this.setRangeEndAttribute(rangeEndElement.getAttributeValue("attribute"));
        }
    }
    
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    
}
