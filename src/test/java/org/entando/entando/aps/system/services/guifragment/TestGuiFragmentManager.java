package org.entando.entando.aps.system.services.guifragment;

import org.entando.entando.aps.system.services.storage.TestLocalStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;

public class TestGuiFragmentManager  extends BaseTestCase {
	
	private static final Logger _logger = LoggerFactory.getLogger(TestLocalStorageManager.class);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.init();
	}

	//TODO add tests
	
	private void init() throws Exception {
		try {
			this._guiFragmentManager = (IGuiFragmentManager) this.getApplicationContext().getBean(SystemConstants.GUI_FRAGMENT_MANAGER);
		} catch (Throwable t) {
			_logger.error("error on init", t);
		}
	}
	
	private IGuiFragmentManager _guiFragmentManager;
}
