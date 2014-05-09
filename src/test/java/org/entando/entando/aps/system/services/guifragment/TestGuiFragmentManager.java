package org.entando.entando.aps.system.services.guifragment;

import org.entando.entando.aps.system.services.storage.TestLocalStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;

public class TestGuiFragmentManager extends BaseTestCase {

	private static final Logger _logger = LoggerFactory.getLogger(TestLocalStorageManager.class);

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.init();
	}

	public void testCrud() throws Exception {
		String code = "mockCrud";
		try {
			assertNull(this._guiFragmentManager.getGuiFragment(code));
			//add
			GuiFragment fragment = this.createMockFragment(code, "lorem ipsum", null);
			this._guiFragmentManager.addGuiFragment(fragment);

			GuiFragment fragment2 = this._guiFragmentManager.getGuiFragment(code);
			assertNotNull(fragment2);
			assertEquals(fragment.getGui(), fragment2.getGui());
			//update
			fragment2.setGui("dolor sit");
			this._guiFragmentManager.updateGuiFragment(fragment2);
			GuiFragment fragment3 = this._guiFragmentManager.getGuiFragment(code);
			assertEquals(fragment2.getGui(), fragment3.getGui());
			//delete
			this._guiFragmentManager.deleteGuiFragment(code);
			assertNull(this._guiFragmentManager.getGuiFragment(code));
		} catch (Exception e) {
			throw e;
		} finally {
			this._guiFragmentManager.deleteGuiFragment(code);
		}
	}

	protected GuiFragment createMockFragment(String code, String gui, String widgetTypeCode) {
		GuiFragment mFragment = new GuiFragment();
		mFragment.setCode(code);
		mFragment.setGui(gui);
		mFragment.setWidgetTypeCode(widgetTypeCode);
		return mFragment;
	}

	private void init() throws Exception {
		try {
			this._guiFragmentManager = (IGuiFragmentManager) this.getApplicationContext().getBean(SystemConstants.GUI_FRAGMENT_MANAGER);
		} catch (Throwable t) {
			_logger.error("error on init", t);
		}
	}

	private IGuiFragmentManager _guiFragmentManager;
}
