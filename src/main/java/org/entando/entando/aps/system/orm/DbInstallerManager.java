/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

import org.entando.entando.aps.system.orm.util.DataInstallerDAO;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.FileTextReader;
import java.io.InputStream;
import java.lang.reflect.Method;

import java.util.*;
import javax.sql.DataSource;

import org.entando.entando.aps.system.orm.model.ComponentReport;
import org.entando.entando.aps.system.orm.model.DataReport;
import org.entando.entando.aps.system.orm.model.InstallationReport;
import org.entando.entando.aps.system.orm.model.SchemaReport;
import org.entando.entando.aps.system.orm.util.TableFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.Resource;

/**
 * @author E.Santoboni
 */
public class DbInstallerManager implements BeanFactoryAware, IDbInstallerManager {
	
	public void init() throws Exception {
		if (!this.isCheckOnStartup()) {
			ApsSystemUtils.getLogger().config(this.getClass().getName() + ": short init executed");
			return;
		}
		//ESTRAE REGISTRO... 
		//SE IL REGISTRO é IN STATO INIT, VERIFICA LA PRESENZA DI RISORSE SQL - da quello capisce il da farsi
		/*
		 * POSSIBILI STATI DI USCITA - 
		 **************************************
		 ** "INIT" - PRIMA INSTALLAZIONE
		 * Fa un ciclo dei componenti ed installa "db core", "dati core di default", 
		 * e per ciascun componente "db" e "dati di default"
		 * (registro da creare ex novo)
		 **************************************
		 ** "OK" - INSTALLAZIONI PRECEDENTI CON SUCCESSO
		 * Fa un ciclo dei componenti ed installa, per ciascun componente non presente nel registro, 
		 * "db" e "dati di default"
		 * (registro da aggiornare)
		 **************************************
		 ** "PORTING" - Inizializzazione vecchia installazione ( <= 3.0.0 )
		 * fa un ciclo dei componenti e marchia tutto come installato
		 * (SI PRESUPPONE CHE CHI FA IL PORTING ABBIA TUTTO A POSTO)
		 **************************************
		 ** "RESTORE" - Creazione nuovo db da istanza
		 * Fa un ciclo dei componenti ed installa "db core" e per ciascun componente "db".
		 * I dati vengono recuperati dalle risorse SQL di backup.
		 * (registro da creare ex novo)
		 **************************************
		 ** "INCOMPLETE" - situazione precedente inconsistente
		 * Fa un ciclo dei componenti e verifica l'installazione dei singoli componenti, 
		 * sulla base della definizione nel registro e del singolo componente (ignora eventuali risorse sql da verificare)
		 * (registro da aggiornare)
		 **************************************
		*/
		//ESTRAZIONE LISTA COMPONENTI
		//CREAZIONE DB CORE (sulla base dello stato nel registro)
		//CREAZIONE DB COMPONENTI (sulla base dello stato nel registro)
		// -> In caso di RESTORE, eseguue tutte le query nell'ordine definito dai componenti
		// -> in altri casi, esegue il restore (sulla base dello stato nel registro)
		
		// ----------------------------------------------------
		
		InstallationReport report = this.extractReport();
		if (null == report) {
			
			//non c'è db locale installato, cerca nei backup locali
			//TODO DA FARE
			
			//non c'è... fa l'inizzializazione es novo
			report = InstallationReport.getInstance();
		}
		try {
			this.initMasterDatabases(report);
			List<EntandoComponentConfiguration> components = this.extractComponents();
			for (int i = 0; i < components.size(); i++) {
				EntandoComponentConfiguration entandoComponentConfiguration = components.get(i);
				this.initComponentDatabases(entandoComponentConfiguration, report);
			}
			this.initMasterDefaultResource(report);
			for (int i = 0; i < components.size(); i++) {
				EntandoComponentConfiguration entandoComponentConfiguration = components.get(i);
				this.initComponentDefaultResources(entandoComponentConfiguration, report);
			}
			if (report.getStatus().equals(InstallationReport.Status.PORTING)) {
				//FAI RESTORE
			}
		} catch (Throwable t) {
			report.setStatus(InstallationReport.Status.INCOMPLETE);
			throw new Exception("Error while initializating Db Installer", t);
		} finally {
			//Solo se CI SONO STATE MODIFICHE?
			this.updateReport(report);
		}
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initializated");
	}
	
	//-------------------- REPORT -------- START
	
	private InstallationReport extractReport() throws ApsSystemException {
		InstallationReport report = null;
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
	
	private void updateReport(InstallationReport report) throws ApsSystemException {
		if (null == report || report.getReports().isEmpty()) {
			return;
		}
		try {
			InstallationReportDAO dao = new InstallationReportDAO();
			DataSource dataSource = (DataSource) this.getBeanFactory().getBean("portDataSource");
			dao.setDataSource(dataSource);
			dao.saveConfigItem(report.toXml(), this.getConfigVersion());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateReport");
			throw new ApsSystemException("Error updating report", t);
		}
	}
	
	//-------------------- REPORT -------- END
	
	private void initMasterDatabases(InstallationReport report) throws ApsSystemException {
		ComponentReport componentReport = report.getComponentReport("entandoCore", true);
		SchemaReport schemaReport = componentReport.getSchemaReport();
		/*
		if (componentReport.getStatus().equals(InstallationReport.Status.OK) 
				|| schemaReport.getStatus().equals(InstallationReport.Status.OK)) {
			ApsSystemUtils.getLogger().info("Core Schema Component - Already installed/verified!");
			System.out.println("Core Schema Component - Already installed/verified!");
			return;
		}
		*/
		//TODO COMMENTATO PER DARE LA POSSIBILITA' DI INIZIALIZZAZIONE DI NUOVI DATASOURCE
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			Map<String, InstallationReport.Status> databasesStatus = schemaReport.getDatabaseStatus();
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				if (report.getStatus().equals(InstallationReport.Status.PORTING)) {
					System.out.println("'" +dataSourceName+ "' Core Component TABLES - Already present! db " + dataSourceName);
					databasesStatus.put(dataSourceName, InstallationReport.Status.PORTING);
					continue;
				}
				InstallationReport.Status status = databasesStatus.get(dataSourceName);
				//System.out.println("********* initMasterDatabases - DATASOURCE " + dataSourceNames[i] + " - " + status);
				if (status != null && (status.equals(InstallationReport.Status.OK) || status.equals(InstallationReport.Status.PORTING))) {
					System.out.println("'" +dataSourceName+ "' Core Component TABLES - Already installed/verified! db " + dataSourceName);
				} else if (status == null || !status.equals(InstallationReport.Status.OK)) {
					//System.out.println("********* initMasterDatabases - DATASOURCE " + dataSourceNames[i]);
					DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
					//System.out.println("********* initMasterDatabases - DATASOURCE " + dataSourceNames[i] + " - " + dataSource);
					databasesStatus.put(dataSourceName, InstallationReport.Status.INCOMPLETE);
					this.initMasterDatabase(dataSourceName, dataSource, schemaReport);
					databasesStatus.put(dataSourceName, InstallationReport.Status.OK);
				}
			}
			//schemaReport.setStatus(InstallationReport.Status.OK);
			ApsSystemUtils.getLogger().info("Core Schema Component installation DONE!");
		} catch (Throwable t) {
			//schemaReport.setStatus(InstallationReport.Status.INCOMPLETE);
			ApsSystemUtils.logThrowable(t, this, "initMasterDatabases");
			throw new ApsSystemException("Error initializating master databases", t);
		}
	}
	
	private void initMasterDatabase(String databaseName, DataSource dataSource, SchemaReport schemaReport) throws ApsSystemException {
		try {
			DatabaseType type = this.getType(dataSource);
			if (type.equals(DatabaseType.DERBY)) {
				this.initDerbySchema(dataSource);
			}
			List<String> tableClassNames = this.getTableMapping().get(databaseName);
			if (null == tableClassNames || tableClassNames.isEmpty()) {
				ApsSystemUtils.getLogger().info("No Master Tables defined for db - " + databaseName);
				schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.NOT_AVAILABLE);
			} else {
				this.createTables(databaseName, tableClassNames, dataSource, schemaReport);
			}
		} catch (Throwable t) {
			schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.INCOMPLETE);
			ApsSystemUtils.logThrowable(t, this, "initMasterDatabase", "Error inizializating db " + databaseName);
			throw new ApsSystemException("Error creating master tables to db " + databaseName, t);
		}
	}
	
	private void initComponentDatabases(EntandoComponentConfiguration componentConfiguration, InstallationReport report) throws ApsSystemException {
		String logPrefix = "Component '" + componentConfiguration.getCode() + "' SCHEMA";
		ComponentReport componentReport = report.getComponentReport(componentConfiguration.getCode(), true);
		if (componentReport.getStatus().equals(InstallationReport.Status.PORTING) || 
				componentReport.getStatus().equals(InstallationReport.Status.OK)) {
			ApsSystemUtils.getLogger().info(logPrefix + " - Already installed/verified!");
			System.out.println(logPrefix + " - Already installed/verified!");
			return;
		}
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			Map<String, List<String>> tableMapping = componentConfiguration.getTableMapping();
			SchemaReport schemaReport = componentReport.getSchemaReport();
			String logTablePrefix = logPrefix + " TABLES";
			//if (null != tableMapping && !tableMapping.isEmpty()) {
			System.out.println(logTablePrefix + " - Installation STARTED!!!");
			for (int j = 0; j < dataSourceNames.length; j++) {
				String dataSourceName = dataSourceNames[j];
				String logDbTablePrefix = logTablePrefix + " / Datasource " + dataSourceName;
				List<String> tableClassNames = (null != tableMapping) ? tableMapping.get(dataSourceName) : null;
				if (null == tableClassNames || tableClassNames.isEmpty()) {
					System.out.println(logDbTablePrefix + " - NOT AVAILABLE!");
					schemaReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.NOT_AVAILABLE);
					continue;
				}
				if (report.getStatus().equals(InstallationReport.Status.PORTING)) {
					schemaReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.PORTING);
					String message = logTablePrefix + "- Already present! " + InstallationReport.Status.PORTING + " - db " + dataSourceName;
					ApsSystemUtils.getLogger().info(message);
					System.out.println(message);
					continue;
				}
				InstallationReport.Status schemaStatus = schemaReport.getDatabaseStatus().get(dataSourceName);
				System.out.println(logDbTablePrefix + " - INIT!!!");
				if (null != schemaStatus && (schemaStatus.equals(InstallationReport.Status.NOT_AVAILABLE) || 
						schemaStatus.equals(InstallationReport.Status.OK))) {
					System.out.println(logDbTablePrefix + "- Already installed/verified! " + InstallationReport.Status.PORTING + " - db " + dataSourceName);
					continue;
				}
				if (null == schemaReport.getDatabaseTables().get(dataSourceName)) {
					schemaReport.getDatabaseTables().put(dataSourceName, new ArrayList<String>());
				}
				System.out.println(logDbTablePrefix + " - INSTALLATION STARTED!");
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				this.createTables(dataSourceName, tableClassNames, dataSource, schemaReport);
				System.out.println(logDbTablePrefix + " - INSTALLATION DONE! - installated tables " + schemaReport.getDatabaseTables().get(dataSourceName));
				schemaReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.OK);
			}
			System.out.println(logTablePrefix + " - Installation DONE!!!");
			ApsSystemUtils.getLogger().info(logTablePrefix + " - Installation DONE!");
		} catch (Throwable t) {
			//componentReport.setStatus(InstallationReport.Status.INCOMPLETE);
			ApsSystemUtils.logThrowable(t, this, "initComponent", 
					"Error initializating component " + componentConfiguration.getCode());
			throw new ApsSystemException("Error initializating component " + componentConfiguration.getCode(), t);
		}
	}
	
	private void createTables(String databaseName, List<String> tableClassNames, 
			DataSource dataSource, SchemaReport schemaReport) throws ApsSystemException {
		try {
			DatabaseType type = this.getType(dataSource);
			TableFactory tableFactory = new TableFactory(databaseName, dataSource, type);
			tableFactory.createTables(tableClassNames, schemaReport);
		} catch (Throwable t) {
			//this.updateReport(report);
			ApsSystemUtils.logThrowable(t, this, "createTables", "Error creating tables into db " + databaseName);
			throw new ApsSystemException("Error creating tables to db " + databaseName, t);
		}
	}
	
	private List<EntandoComponentConfiguration> extractComponents() throws ApsSystemException {
		List<EntandoComponentConfiguration> componentBeans = new ArrayList<EntandoComponentConfiguration>();
		try {
			String[] componentBeansNames = this.extractBeanNames(EntandoComponentConfiguration.class);
			if (null == componentBeansNames || componentBeansNames.length == 0) return componentBeans;
			for (int i = 0; i < componentBeansNames.length; i++) {
				//System.out.println("* " + componentBeansNames[i] + " *");
				EntandoComponentConfiguration componentConfiguration = (EntandoComponentConfiguration) this.getBeanFactory().getBean(componentBeansNames[i]);
				if (null != componentConfiguration) componentBeans.add(componentConfiguration);
			}
			Collections.sort(componentBeans);
		} catch (Throwable t) {
			//t.printStackTrace();
			ApsSystemUtils.logThrowable(t, this, "extractComponents");
			throw new ApsSystemException("Error extracting components", t);
		}
		return componentBeans;
	}
	
	protected DatabaseType getType(DataSource dataSource) {
		DatabaseType type = null;
		String typeString = null;
		try {
			//System.out.println(((BasicDataSource) dataSource).getDriverClassName());
			String driverClassName = this.invokeGetMethod("getDriverClassName", dataSource);
			//System.out.println(driverClassName);
			Iterator<Object> typesIter = this.getDatabaseTypeDrivers().keySet().iterator();
			while (typesIter.hasNext()) {
				String typeCode = (String) typesIter.next();
				List<String> driverClassNames = (List<String>) this.getDatabaseTypeDrivers().get(typeCode);
				if (null != driverClassNames && driverClassNames.contains(driverClassName)) {
					typeString = typeCode;
					//System.out.println(typeCode);
					break;
				}
			}
			if (null == typeString) return DatabaseType.DERBY;
			type = Enum.valueOf(DatabaseType.class, typeString.toUpperCase());
			//System.out.println(type);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().severe("Invalid type for db - '" + typeString + "' - " + t.getMessage());
			type = DatabaseType.DERBY;
		}
		return type;
	}
	
	private void initDerbySchema(DataSource dataSource) throws Throwable {
		String username = this.invokeGetMethod("getUsername", dataSource);
		try {
			String[] queryCreateSchema = new String[] {"CREATE SCHEMA " + username.toUpperCase()};
			DataInstallerDAO.executeQueries(dataSource, queryCreateSchema, false);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().info("Error creating derby schema - " + t.getMessage());
			throw new ApsSystemException("Error creating derby schema", t);
		}
		try {
			String[] initSchemaQuery = new String[] {"SET SCHEMA \"" + username.toUpperCase() + "\""};
			DataInstallerDAO.executeQueries(dataSource, initSchemaQuery, true);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initDerbySchema", "Error initializating Derby Schema");
			throw new ApsSystemException("Error initializating Derby Schema", t);
		}
	}
	
	private String invokeGetMethod(String methodName, DataSource dataSource) throws Throwable {
		Method method = dataSource.getClass().getDeclaredMethod(methodName);
		return (String) method.invoke(dataSource);
	}
	
	//---------------- DATA ------------------- START
	
	
	private void initMasterDefaultResource(InstallationReport report) throws ApsSystemException {
		ComponentReport coreComponentReport = report.getComponentReport("entandoCore", false);
		if (coreComponentReport.getStatus().equals(InstallationReport.Status.OK) || 
				coreComponentReport.getStatus().equals(InstallationReport.Status.RESTORE)) {
			String message = "Core Component RESOURCES - Already installed/verified/present! " + coreComponentReport.getStatus();
			ApsSystemUtils.getLogger().info(message);
			System.out.println(message);
			return;
		}
		DataReport dataReport = coreComponentReport.getDataReport();
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				if (report.getStatus().equals(InstallationReport.Status.PORTING)) {
					dataReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.PORTING);
					String message = "Core Component RESOURCES - Already present! " + InstallationReport.Status.PORTING + " - db " + dataSourceName;
					ApsSystemUtils.getLogger().info(message);
					System.out.println(message);
					continue;
				}
				//System.out.println("********* initMasterDatabases - DATASOURCE " + dataSourceNames[i]);
				
				//System.out.println("********* initMasterDatabases - DATASOURCE " + dataSourceNames[i] + " - " + dataSource);
				Resource resource = this.getDefaultSqlResources().get(dataSourceName);
				String script = this.readFile(resource);
				if (null != script && script.trim().length() != 0) {
					dataReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.INCOMPLETE);
					DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
					DataInstallerDAO.valueDatabase(script, dataSourceName, dataSource, null);
					dataReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.OK);
				} else {
					dataReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.NOT_AVAILABLE);
				}
			}
			ApsSystemUtils.getLogger().info("Core Component DATA installation DONE!");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initMasterDefaultResource");
			throw new ApsSystemException("Error initializating master DefaultResource", t);
		}
	}
	
	private void initComponentDefaultResources(EntandoComponentConfiguration componentConfiguration, 
			InstallationReport report) throws ApsSystemException {
		String logPrefix = "Component '" + componentConfiguration.getCode() + "' DATA";
		ComponentReport componentReport = report.getComponentReport(componentConfiguration.getCode(), false);
		if (componentReport.getStatus().equals(InstallationReport.Status.OK) || 
				componentReport.getStatus().equals(InstallationReport.Status.RESTORE)) {
			ApsSystemUtils.getLogger().info(logPrefix + " - Already installed/verified/present!");
			System.out.println(logPrefix + " - Already installed/verified/present!");
			return;
		}
		DataReport dataReport = componentReport.getDataReport();
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			Map<String, Resource> defaultSqlResources = componentConfiguration.getDefaultSqlResources();
			String logDataPrefix = "Component " + componentReport.getComponent() + " DATA";
			//if (null != defaultSqlResources && !defaultSqlResources.isEmpty()) {
			System.out.println(logDataPrefix + " - INIT!!!");
			for (int j = 0; j < dataSourceNames.length; j++) {
				String dataSourceName = dataSourceNames[j];
				String logDbDataPrefix = logDataPrefix + " / Datasource " + dataSourceName;
				if (report.getStatus().equals(InstallationReport.Status.PORTING)) {
					dataReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.PORTING);
					continue;
				}
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				InstallationReport.Status dataStatus = dataReport.getDatabaseStatus().get(dataSourceName);
				if (null != dataStatus && (dataStatus.equals(InstallationReport.Status.NOT_AVAILABLE) || 
						dataStatus.equals(InstallationReport.Status.RESTORE) || 
						dataStatus.equals(InstallationReport.Status.OK))) {
					System.out.println(logDbDataPrefix + " - Already installed/verified!");
					continue;
				}
				Resource resource = (null != defaultSqlResources) ? defaultSqlResources.get(dataSourceName) : null;
				String script = this.readFile(resource);
				if (null != script && script.trim().length() > 0) {
					System.out.println(logDbDataPrefix + " - Installation STARTED!!!");
					dataReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.INCOMPLETE);
					DataInstallerDAO.valueDatabase(script, dataSourceName, dataSource, dataReport);
					System.out.println(logDbDataPrefix + " - Installation DONE!!!");
					dataReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.OK);
				} else {
					System.out.println(logDbDataPrefix + " - NOT AVAILABLE!");
					dataReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.NOT_AVAILABLE);
				}
			}
			System.out.println(logDataPrefix + " - INSTALLATION DONE!!!");
			ApsSystemUtils.getLogger().info(logDataPrefix + "' Component installation DONE!");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initComponent", 
					"Error initializating component " + componentConfiguration.getCode());
			throw new ApsSystemException("Error initializating component " + componentConfiguration.getCode(), t);
		}
	}
	
	private String readFile(Resource resource) throws Throwable {
		if (resource == null) return null;
		InputStream is = null;
		String text = null;
		try {
			is = resource.getInputStream();
			if (null == is) {
				return null;
			}
			text = FileTextReader.getText(is);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "readFile", "Error reading resource");
			throw new ApsSystemException("Error reading resource", t);
		} finally {
			if (null != is) is.close();
		}
		return text;
	}
	
	//---------------- DATA ------------------- END
	
	@Override
	public void createBackup() throws ApsSystemException {
		//TODO
		System.out.println("BACKUP DONE");
	}
	
	private String[] extractBeanNames(Class beanClass) {
		ListableBeanFactory factory = (ListableBeanFactory) this.getBeanFactory();
		return factory.getBeanNamesForType(beanClass);
	}
	
	protected boolean isCheckOnStartup() {
		return _checkOnStartup;
	}
	public void setCheckOnStartup(boolean checkOnStartup) {
		this._checkOnStartup = checkOnStartup;
	}
	
	protected String getConfigVersion() {
		return _configVersion;
	}
	public void setConfigVersion(String configVersion) {
		this._configVersion = configVersion;
	}
	
	protected Properties getDatabaseTypeDrivers() {
		return _databaseTypeDrivers;
	}
	public void setDatabaseTypeDrivers(Properties databaseTypeDrivers) {
		this._databaseTypeDrivers = databaseTypeDrivers;
	}
	
	protected Map<String, List<String>> getTableMapping() {
		return _tableMapping;
	}
	public void setTableMapping(Map<String, List<String>> tableMapping) {
		this._tableMapping = tableMapping;
	}
	
	protected Map<String, Resource> getDefaultSqlResources() {
		return _defaultSqlResources;
	}
	public void setDefaultSqlResources(Map<String, Resource> defaultSqlResources) {
		this._defaultSqlResources = defaultSqlResources;
	}
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	private boolean _checkOnStartup;
	private String _configVersion;
	private Properties _databaseTypeDrivers;
	private Map<String, List<String>> _tableMapping;
	private Map<String, Resource> _defaultSqlResources;
	
	private BeanFactory _beanFactory;
	
}