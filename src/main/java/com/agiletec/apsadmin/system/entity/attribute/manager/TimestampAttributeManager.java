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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.TimestampAttribute;
import com.agiletec.apsadmin.util.CheckFormatUtil;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;

/**
 * @author E.Santoboni
 */
public class TimestampAttributeManager extends DateAttributeManager {
	
	@Override
    protected void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
        String value = this.getValueFromForm(attribute, tracer, request);
        if (value != null) {
            if (value.trim().length() == 0) {
                value = null;
            }
            this.setValue(attribute, value);
        }
		String hourValue = this.getValueFromForm(attribute, tracer, "_hour", request);
		this.setValue(attribute, hourValue, true);
		String minuteValue = this.getValueFromForm(attribute, tracer, "_minute", request);
		this.setValue(attribute, minuteValue, false);
    }
	
	protected void setValue(AttributeInterface attribute, String value, boolean isHour) {
		TimestampAttribute timestampAttribute = (TimestampAttribute) attribute;
		//Date data = null;
		if (value != null) {
			value = value.trim();
		}
		Integer number = null;
		if (CheckFormatUtil.isValidNumber(value)) {
			try {
				number = Integer.parseInt(value);
			} catch (Throwable ex) {
				this.setError(timestampAttribute, value, isHour);
				throw new RuntimeException("Error while parsing the number - " + value + " -", ex);
			}
			//data = dataF.parse(value);
			//dateAttribute.setFailedDateString(null);
			int max = (isHour) ? 23 : 59;
			if (number > max) {
				this.setError(timestampAttribute, value, isHour);
			} else {
				this.resetError(timestampAttribute, isHour);
			}
		} else {
			this.setError(timestampAttribute, value, isHour);
		}
		if (null != number && null != timestampAttribute.getDate()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(timestampAttribute.getDate());
			if (isHour) {
				cal.set(Calendar.HOUR_OF_DAY, number);
			} else {
				cal.set(Calendar.MINUTE, number);
			}
			timestampAttribute.setDate(cal.getTime());
			this.resetError(timestampAttribute, isHour);
		}
	}
	
	private void setError(TimestampAttribute timestampAttribute, String value, boolean isHour) {
		if (isHour) {
			timestampAttribute.setFailedHourString(value);
		} else {
			timestampAttribute.setFailedMinuteString(value);
		}
	}
    
	private void resetError(TimestampAttribute timestampAttribute, boolean isHour) {
		if (isHour) {
			timestampAttribute.setFailedHourString(null);
		} else {
			timestampAttribute.setFailedMinuteString(null);
		}
	}
    
	protected String getValueFromForm(AttributeInterface attribute, AttributeTracer tracer, String suffix, HttpServletRequest request) {
        String formFieldName = tracer.getFormFieldName(attribute) + suffix;
        return request.getParameter(formFieldName);
    }
    
}