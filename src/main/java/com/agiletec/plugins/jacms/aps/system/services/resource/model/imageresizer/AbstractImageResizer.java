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
package com.agiletec.plugins.jacms.aps.system.services.resource.model.imageresizer;

/**
 * Classe astratta base a servizio delle classi delegate al redimensionameno e salvataggio di file tipo immagine.
 * @author E.Santoboni
 */
public abstract class AbstractImageResizer implements IImageResizer {
	
	/**
	 * Calcola il rapporto di scala sulla base della dimensione maggiore (tenuto conto
	 * del rapporto finale desiderato).
	 * Il fattore di scala restituito non sarà comunque superiore ad 1.
	 * @param width Dimensione attuale dell'immagine
	 * @param height Dimensione attuale dell'immagine
	 * @param finalWidth Dimensione finale dell'immagine
	 * @param finalHeight Dimensione finale dell'immagine
	 * @return Il fattore di scala da applicare all'immagine
	 */
	protected double computeScale(int width, int height, int finalWidth, int finalHeight) {
		double scale;
		if (((double) width / (double) height) >= ((double) finalWidth / (double) finalHeight)) {
			scale = (double) finalWidth / width;
		} else {
			scale = (double) finalHeight / height;
		}
		if (scale > 1) {
			scale = 1;
		}
		return scale;
	}
	
	protected String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.')+1).trim();
	}
	
}
