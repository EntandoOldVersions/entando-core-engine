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
package org.entando.entando.apsadmin.portal.model;

import com.agiletec.aps.system.services.pagemodel.PageModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;

/**
 * @author E.Santoboni
 */
public class PageModelFinderAction extends AbstractPageModelAction {
	
	public List<PageModel> getPageModels() {
		List<PageModel> models = new ArrayList<PageModel>();
		models.addAll(this.getPageModelManager().getPageModels());
		BeanComparator c = new BeanComparator("description");
		Collections.sort(models, c);
		return models;
	}
	
}