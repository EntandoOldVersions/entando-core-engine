/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.init;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import javax.sql.DataSource;
import org.entando.entando.aps.system.init.model.SystemInstallationReport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author eu
 */
public abstract class AbstractInitializerManager implements BeanFactoryAware {
	
	protected SystemInstallationReport extractReport() throws ApsSystemException {
		SystemInstallationReport report = null;
		try {
			InstallationReportDAO dao = new InstallationReportDAO();
			DataSource dataSource = (DataSource) this.getBeanFactory().getBean("portDataSource");
			dao.setDataSource(dataSource);
			report = dao.loadReport(this.getConfigVersion());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractReport");
			throw new ApsSystemException("Error extracting report", t);
		}
		return report;
	}
	
	protected String getConfigVersion() {
		return _configVersion;
	}
	public void setConfigVersion(String configVersion) {
		this._configVersion = configVersion;
	}
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	private String _configVersion;
	private BeanFactory _beanFactory;
	
}
