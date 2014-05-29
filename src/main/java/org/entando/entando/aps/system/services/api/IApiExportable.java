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
package org.entando.entando.aps.system.services.api;

import javax.ws.rs.core.MediaType;

/**
 * Interface implemented by all those services or beans 
 * which handle objects suitable to be exported through API calls.
 * @author E.Santoboni
 */
public interface IApiExportable {
	
	/**
	 * Return the uniform resource locator of the given object.
	 * @param object The object
	 * @param applicationBaseUrl The application base url.
	 * @param langCode The current lang code
	 * @param mediaType The produces media type
	 * @return The uniform resource locator of the given object.
	 */
	public String getApiResourceUrl(Object object, String applicationBaseUrl, String langCode, MediaType mediaType);
	
}