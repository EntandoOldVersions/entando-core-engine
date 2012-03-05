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
package com.agiletec.aps.system;

import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Classe di utilità.
 * E' la classe detentrice del log di sistema.
 * @author E.Santoboni
 */
public class ApsSystemUtils {

	/**
	 * Inizializzazione della classe di utilità.
	 * @throws Exception
	 */
	public void init() throws Exception {
		Level logLevel = Level.INFO;
		try {
			String logName = (String) this._systemParams.get(INIT_PROP_LOG_NAME);
			_logger = Logger.getLogger(logName);
			String pattern = (String) this._systemParams.get(INIT_PROP_LOG_FILE_PREFIX);
			if (pattern != null && !pattern.equals("")) {
				_logger.setUseParentHandlers(false);
				pattern = pattern + ".%g"; // Esempio: "c:/dir/nome%g.log";

				int limit = Integer.parseInt((String) this._systemParams.get(INIT_PROP_LOG_FILE_SIZE));
				int count = Integer.parseInt((String) this._systemParams.get(INIT_PROP_LOG_FILES_COUNT));

				FileHandler handler = new FileHandler(pattern, limit, count);
				handler.setFormatter(new SimpleFormatter());
				handler.setEncoding("UTF-8");
				_logger.addHandler(handler);
			}
			String levelString = (String) this._systemParams.get(INIT_PROP_LOG_LEVEL);
			logLevel = Level.parse(levelString);
			_logger.setLevel(logLevel);
		} catch (Exception e) {
			try {
				_logger = Logger.getLogger(this.getClass().getName());
				_logger.log(Level.SEVERE, "Error detected while creating the logger: ", e);
			} catch (RuntimeException e1) {
			}
		}
	}

	/**
	 * Restituisce il logger di sistema.
	 * @return Il logger
	 */
	public static Logger getLogger() {
		return _logger;
	}

	/**
	 * Traccia una eccezione sul logger del contesto. Se il livello di soglia 
	 * del logger è superiore a FINER, viene emesso solo un breve messaggio di 
	 * livello SEVERE, altrimenti viene tracciato anche lo stack trace della
	 * eccezione (con il livello FINER).
	 * @param t L'eccezione da tracciare
	 * @param caller La classe chiamante, in cui si è verificato l'errore.
	 * @param methodName Il metodo in cui si è verificato l'errore.
	 * @param message Testo da includere nel tracciamento. 
	 */
	public static void logThrowable(Throwable t, Object caller,
			String methodName, String message){
		String className = null;
		if(caller != null) {
			className = caller.getClass().getName();
		}
		if(_logger.isLoggable(Level.FINER)){
			_logger.throwing(className, methodName, t);
		} 
		_logger.severe(message + " - " + t.toString() 
				+ " in " + className + "." + methodName);
	}

	/**
	 * Traccia una eccezione sul logger del contesto. Se il livello di soglia 
	 * del logger è superiore a FINER, viene emesso solo un breve messaggio di 
	 * livello SEVERE, altrimenti viene tracciato anche lo stack trace della
	 * eccezione (con il livello FINER).
	 * @param t L'eccezione da tracciare
	 * @param caller La classe chiamante, in cui si è verificato l'errore.
	 * @param methodName Il metodo in cui si è verificato l'errore.
	 */
	public static void logThrowable(Throwable t, Object caller, String methodName) {
		logThrowable(t, caller, methodName, "Exception");
	}

	/**
	 * Setta la mappa dei parametri di inizializzazione.
	 * @param systemParams I parametri di inizializzazione.
	 */
	public void setSystemParams(Map<String, Object> systemParams) {
		this._systemParams = systemParams;
	}

	/**
	 * Nome della property che definisce il nome 
	 * da assegnare al logger (tipo: String)
	 */
	public static final String INIT_PROP_LOG_NAME = "logName";

	/**
	 * Nome della property che definisce il path del file di log; il nome
	 * completo del file sarà ottenuto aggiungendo eventuale estensione
	 * secondo le regole di java.util.logging.Logger. (tipo: String)
	 */
	public static final String INIT_PROP_LOG_FILE_PREFIX = "logFilePrefix";

	/**
	 * Nome della property che definisce il livello di log; utilizzare
	 * uno dei nomi delle costanti di java.util.logging.Level. (tipo: String)
	 */
	public static final String INIT_PROP_LOG_LEVEL = "logLevel";

	/**
	 * Nome della property che definisce il size (in byte) del singolo file di log
	 */
	public static final String INIT_PROP_LOG_FILE_SIZE = "logFileSize";

	/**
	 * Nome della property che definisce numero di file per ciclo di log.
	 */
	public static final String INIT_PROP_LOG_FILES_COUNT = "logFilesCount";

	/**
	 * Logger di sistema.
	 */
	private static Logger _logger; 

	private Map<String, Object> _systemParams;

}