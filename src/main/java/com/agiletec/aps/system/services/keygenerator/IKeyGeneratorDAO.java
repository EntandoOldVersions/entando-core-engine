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
package com.agiletec.aps.system.services.keygenerator;

/**
 * Interfaccia base per i Data Access Object gestore del sistema di chiavi univoche.
 * @author S.Didaci - E.Santoboni
 */
public interface IKeyGeneratorDAO {

	/**
	 * Estrae la chiave presente nel db.
	 * Il metodo viene chiamato solo in fase di inizializzazione.
	 * @return La chiave estratta.
	 */
	public int getUniqueKey();

	/**
	 * Aggiorna la chiave univoca nel db.
	 * @param currentKey Il valore della chiave corrente.
	 */
	public void updateKey(int currentKey);

}
