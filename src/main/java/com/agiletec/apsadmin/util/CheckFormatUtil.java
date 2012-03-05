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
package com.agiletec.apsadmin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author E.Santoboni
 */
public class CheckFormatUtil {
	
    /**
	 * Controlla la validità della stringa rappresentante un numero. 
     * Restituisce true nel caso che la stringa immessa 
     * corrisponda ad un numero, false in caso contrario.
     * @param numberString La stringa da controllare.
     * @return true nel caso che la stringa immessa 
     * corrisponda ad un numero, false in caso contrario.
     */
    public static boolean isValidNumber(String numberString) {
    	boolean validate = false;
		if (numberString != null && numberString.length()>0) {
			Pattern pattern = Pattern.compile("\\d+");
			Matcher matcher = pattern.matcher(numberString.trim());
			validate = matcher.matches();
		}
		return validate;
	}
    
    /**
	 * Controlla la validità della stringa rappresentante una data. 
	 * Restituisce true nel caso che la stringa immessa 
     * corrisponda ad una data nel formato dd/MM/yyyy, false in caso contrario.
	 * @param dateString La stringa rappresentante una data. 
	 * @return true nel caso che la stringa immessa 
     * corrisponda ad una data nel formato dd/MM/yyyy, false in caso contrario.
	 */
    public static boolean isValidDate(String dateString) {
		boolean validate = false;
		if (dateString != null && (dateString.length() > 0)) {
			dateString = dateString.trim();
			Pattern pattern = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[012])[/]\\d\\d\\d\\d");
			Matcher matcher = pattern.matcher(dateString);
			validate = matcher.matches();
			if (validate) {
				String temp1 = dateString.substring(0,2);//gg
				String temp2 = dateString.substring(3,5);
				if (temp1.equals("31") && (temp2.equals("04")||temp2.equals("06")||temp2.equals("09")||temp2.equals("11"))){
					validate = false;
				}
				int temp4 = new Integer(temp1).intValue();
				if (temp4>=30 && temp2.equals("02")) {
					validate = false;
				}
			}
		}
		return validate;
	}
	
}