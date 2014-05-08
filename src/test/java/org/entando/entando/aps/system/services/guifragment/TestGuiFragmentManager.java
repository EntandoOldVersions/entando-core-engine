package org.entando.entando.aps.system.services.guifragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
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

	protected GuiFragment createMockFragment(String code, String gui, String defaultGui, String widgetTypeCode) {
		GuiFragment mFragment = new GuiFragment();
		mFragment.setCode(code);
		mFragment.setGui(gui);
		mFragment.setDefaultGui(defaultGui);
		mFragment.setWidgetTypeCode(widgetTypeCode);
		return mFragment;
	}


	public String getMockTemplate(String a, String b, String c, String d) throws Throwable {
		String template = IOUtils.toString(this.getClass().getResourceAsStream("mockTemplate"));
		if (null != a) {
			template = template.replaceAll("PLACECHOLDER_A", a);
		}
		if (null != b) {
			template = template.replaceAll("PLACECHOLDER_B", b);
		}
		if (null != c) {
			template = template.replaceAll("PLACECHOLDER_C", c);
		}
		if (null != d) {
			template = template.replaceAll("PLACECHOLDER_D", d);
		}
		return template;
	}

	@SuppressWarnings("unchecked")
	public void testSearch() throws Throwable {
		List<GuiFragment> fragments = new ArrayList<GuiFragment>(); 
		for (int i = 0; i < 5; i++) {
			fragments.add(this.createMockFragment("mock"+i, this.getMockTemplate("code_"+i, "mock"+i, "fixed_gui", null), null, null));
		}
		Iterator<GuiFragment> it = fragments.iterator();
		while (it.hasNext()) {
			this._guiFragmentManager.addGuiFragment(it.next());
		}
		try {

			List<GuiFragment> list = ((GuiFragmentUtilizer)this._guiFragmentManager).getGuiFragmentUtilizers("code_1");
			assertEquals(1, list.size());

			list = ((GuiFragmentUtilizer)this._guiFragmentManager).getGuiFragmentUtilizers("fixed_gui");
			assertEquals(5, list.size());
		
			list = ((GuiFragmentUtilizer)this._guiFragmentManager).getGuiFragmentUtilizers("code_1");
			assertEquals(1, list.size());
			assertEquals("mock1", list.get(0).getCode());
			
			list = ((GuiFragmentUtilizer)this._guiFragmentManager).getGuiFragmentUtilizers("code_2");
			assertEquals(1, list.size());
			assertEquals("mock2", list.get(0).getCode());

			list = ((GuiFragmentUtilizer)this._guiFragmentManager).getGuiFragmentUtilizers("code_3");
			assertEquals(1, list.size());
			assertEquals("mock3", list.get(0).getCode());

			list = ((GuiFragmentUtilizer)this._guiFragmentManager).getGuiFragmentUtilizers("mock3");
			assertEquals(1, list.size());
			assertEquals("mock3", list.get(0).getCode());

		} catch (Exception e) {
			throw e;
		} finally {
			Iterator<GuiFragment> it1 = fragments.iterator();
			while (it1.hasNext()) {
				GuiFragment fragment = it1.next();
				String code = fragment.getCode();
				this._guiFragmentManager.deleteGuiFragment(code);
			}
		}
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
