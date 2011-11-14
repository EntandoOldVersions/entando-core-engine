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
package com.agiletec.aps.system.common.renderer;

import com.agiletec.aps.system.common.entity.model.IApsEntity;

/**
 * Basic interface which declares the services used to render entities.
 * @author M.Diana - W.Ambu - E.Santoboni
 */
public interface IEntityRenderer {
	
	/**
	 * Render an entity with a velocity template.
	 * @param entity The entity to render.
	 * @param velocityTemplate The velocity template to use to render the entity.
	 * @param langCode The rendering lang.
	 * @param convertSpecialCharacters Specifies whether to convert special characters.
	 * @return The result of the rendering.
	 */
	public String render(IApsEntity entity, String velocityTemplate, String langCode, boolean convertSpecialCharacters);
	
}
