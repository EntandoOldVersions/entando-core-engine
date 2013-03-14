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
package com.agiletec.plugins.jacms.aps.system.services.resource.model;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.imageresizer.IImageResizer;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.util.IImageDimensionReader;

/**
 * Classe rappresentante una risorsa Image.
 * @author W.Ambu - E.Santoboni
 */
public class ImageResource extends AbstractMultiInstanceResource {

    /**
     * Restituisce il path dell'immagine (relativa al size immesso).
     * La stringa restituita è comprensiva del folder della risorsa e
     * del nome del file dell'istanza richiesta.
     * @param size Il size dell'istanza.
     * @return Il path dell'immagine.
     */
    public String getImagePath(String size) {
    	ResourceInstance instance = (ResourceInstance) this.getInstances().get(size);
    	return this.getUrlPath(instance);
    }
	
	@Override
	public File getFile() {
		return this.getFile(0, null);
	}
    
	@Override
	public File getFile(int size, String langCode) {
		ResourceInstance instance = (ResourceInstance) this.getInstances().get(String.valueOf(size));
		String filePath = this.getDiskFolder() + instance.getFileName();
		File file = new File(filePath);
		if (file.exists()) {
			return file;
		}
    	return null;
	}
	
    @Override
	public ResourceInterface getResourcePrototype() {
		ImageResource resource = (ImageResource) super.getResourcePrototype();
		resource.setImageDimensionReader(this.getImageDimensionReader());
		resource.setImageResizerClasses(this.getImageResizerClasses());
		resource.setImageMagickEnabled(this.isImageMagickEnabled());
		resource.setImageMagickWindows(this.isImageMagickWindows());
		resource.setImageMagickPath(this.getImageMagickPath());
		return resource;
	}

	/**
     * Aggiunge un'istanza alla risorsa, indicizzandola in base
	 * al size dell'istanza sulla mappa delle istanze.
     * @param instance L'istanza da aggiungere alla risorsa.
     */
    @Override
	public void addInstance(ResourceInstance instance) {
    	String key = String.valueOf(instance.getSize());
    	this.getInstances().put(key, instance);
    }

    @Override
	public String getInstanceFileName(String masterFileName, int size, String langCode) {
    	String baseName = masterFileName.substring(0, masterFileName.lastIndexOf("."));
    	String extension = masterFileName.substring(masterFileName.lastIndexOf('.')+1).trim();
    	StringBuilder fileName = new StringBuilder(baseName);
    	if (size >= 0) {
    		fileName.append("_d").append(size);
    	}
    	if (langCode != null) {
    		fileName.append("_").append(langCode);
    	}
    	fileName.append(".").append(extension);
    	return fileName.toString();
	}

    @Override
	public ResourceInstance getInstance(int size, String langCode) {
    	return (ResourceInstance) this.getInstances().get(String.valueOf(size));
	}
	
	@Override
	public void saveResourceInstances(ResourceDataBean bean) throws ApsSystemException {
		try {
			String masterImageFileName = this.getInstanceFileName(bean.getFileName(), 0, null);
			String baseDiskFolder = this.getInstanceHelper().getResourceDiskFolder(this);
			String masterFilePath = baseDiskFolder + masterImageFileName;
			this.getInstanceHelper().delete(masterFilePath);
			this.getInstanceHelper().save(masterFilePath, bean.getInputStream());
			ResourceInstance instance = new ResourceInstance();
			instance.setSize(0);
			instance.setFileName(masterImageFileName);
			String mimeType = bean.getMimeType();
			instance.setMimeType(mimeType);
			instance.setFileLength(bean.getFileSize() + " Kb");
			this.addInstance(instance);
			this.saveResizedInstances(bean, baseDiskFolder, masterFilePath);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveResourceInstances");
			throw new ApsSystemException("Error saving image resource instances", t);
		}
	}
	
    @Override
    public void reloadResourceInstances() throws ApsSystemException {
    	try {
    		ResourceInstance masterInstance = this.getInstance(0, null);
    		String filename = masterInstance.getFileName();
    		String baseDiskFolder = this.getInstanceHelper().getResourceDiskFolder(this);
			String masterFilePath = baseDiskFolder + filename;
			File masterFile = new File(masterFilePath);
			BaseResourceDataBean bean = new BaseResourceDataBean(masterFile);
			int index = filename.lastIndexOf("_d0.");
			String masterFileName = filename.substring(0, index) + filename.substring(index+3);
			bean.setFileName(masterFileName);
			bean.setMimeType(masterInstance.getMimeType());
			this.saveResizedInstances(bean, baseDiskFolder, masterFilePath);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reloadResourceInstances");
			throw new ApsSystemException("Error reloading image resource instances", t);
		}
    }
	
	private void saveResizedInstances(ResourceDataBean bean, String baseDiskFolder, String masterFilePath) throws ApsSystemException {
		try {
			String mimeType = bean.getMimeType();
			Map<Integer, ImageResourceDimension> dimensions = this.getImageDimensionReader().getImageDimensions();
			Iterator<ImageResourceDimension> iterDimensions = dimensions.values().iterator();
			while (iterDimensions.hasNext()) {
				ImageResourceDimension dimension = iterDimensions.next();
				//Is the system use ImageMagick?
				if (!this.isImageMagickEnabled()) {
					ImageIcon imageIcon = new ImageIcon(masterFilePath);
					this.saveResizedImage(bean, imageIcon, dimension, mimeType, baseDiskFolder);
				} else {
					this.saveResizedImage(bean, dimension, mimeType, baseDiskFolder);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveResizedInstances");
			throw new ApsSystemException("Error saving resized image resource instances", t);
		}
	}

	/**
	 * Redim images using im4Java
	 * @param bean
	 * @param dimension
	 * @param mimeType
	 * @param baseDiskFolder
	 * @throws ApsSystemException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	private void saveResizedImage(ResourceDataBean bean, ImageResourceDimension dimension, String mimeType,
			String baseDiskFolder) throws ApsSystemException, IOException, InterruptedException, IM4JavaException {
		if (dimension.getIdDim() == 0) {
			//salta l'elemento con id zero che non va ridimensionato
			return;
		}
		String imageName = this.getInstanceFileName(bean.getFileName(), dimension.getIdDim(), null);
		String filePath = baseDiskFolder + imageName;
		try {
			this.getInstanceHelper().delete(filePath);
			ResourceInstance resizedInstance = new ResourceInstance();
			resizedInstance.setSize(dimension.getIdDim());
			resizedInstance.setFileName(imageName);
			resizedInstance.setMimeType(mimeType);
			File fileTemp = new File(filePath);
			long realLength = fileTemp.length() / 1000;
			resizedInstance.setFileLength(String.valueOf(realLength) + " Kb");
			this.addInstance(resizedInstance);
			// create command
			ConvertCmd convertCmd = new ConvertCmd();
			//Is it a windows system?
			if (this.isImageMagickWindows()) {
				//yes so configure the path where ImagicMagick is installed
				convertCmd.setSearchPath(this.getImageMagickPath());
			}
			// create the operation, add images and operators/options
			IMOperation imOper = new IMOperation();
			imOper.addImage();
			imOper.resize(dimension.getDimx(), dimension.getDimy());
			imOper.addImage();
			convertCmd.run(imOper, bean.getFile().getAbsolutePath(), fileTemp.getAbsolutePath());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveResizedImage");
			throw new ApsSystemException("Error creating resource file instance '" + filePath + "'", t);
		}
	}

	private void saveResizedImage(ResourceDataBean bean, ImageIcon imageIcon,
			ImageResourceDimension dimension, String mimeType, String baseDiskFolder) throws ApsSystemException {
		if (dimension.getIdDim() == 0) {
			//salta l'elemento con id zero che non va ridimensionato
			return;
		}
		String imageName = this.getInstanceFileName(bean.getFileName(), dimension.getIdDim(), null);
		String filePath = baseDiskFolder + imageName;
		try {
			this.getInstanceHelper().delete(filePath);
			IImageResizer resizer = this.getImageResizer(filePath);
			resizer.saveResizedImage(imageIcon, filePath, dimension);
			ResourceInstance resizedInstance = new ResourceInstance();
			resizedInstance.setSize(dimension.getIdDim());
			resizedInstance.setFileName(imageName);
			resizedInstance.setMimeType(mimeType);
			File fileTemp = new File(filePath);
			long realLength = fileTemp.length() / 1000;
			resizedInstance.setFileLength(String.valueOf(realLength) + " Kb");
			this.addInstance(resizedInstance);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveResizedImage");
			throw new ApsSystemException("Error creating resource file instance '" + filePath + "'", t);
		}
	}

	private IImageResizer getImageResizer(String filePath) {
		String extension = this.getInstanceHelper().getFileExtension(filePath).toLowerCase();
		String resizerClassName = this.getImageResizerClasses().get(extension);
		if (null == resizerClassName) {
			resizerClassName = this.getImageResizerClasses().get("DEFAULT_RESIZER");
		}
		try {
			Class resizerClass = Class.forName(resizerClassName);
			return (IImageResizer) resizerClass.newInstance();
		} catch (Throwable t) {
			throw new RuntimeException("Errore in creazione resizer da classe '"
					+ resizerClassName + "' per immagine tipo '" + extension + "'", t);
		}
	}
	
	@Override
	public boolean exists(String masterFormFileName) throws ApsSystemException {
		String fileName = this.getInstanceFileName(masterFormFileName, 0, null);
		String filePath = this.getDiskFolder() + fileName;
		return this.getInstanceHelper().exists(filePath);
	}
	
    protected IImageDimensionReader getImageDimensionReader() {
		return _imageDimensionReader;
	}
	public void setImageDimensionReader(IImageDimensionReader imageDimensionReader) {
		this._imageDimensionReader = imageDimensionReader;
	}

	protected Map<String, String> getImageResizerClasses() {
		return _imageResizerClasses;
	}
	public void setImageResizerClasses(Map<String, String> imageResizerClasses) {
		this._imageResizerClasses = imageResizerClasses;
	}

	public void setImageMagickEnabled(boolean imageMagickEnabled) {
		this._imageMagickEnabled = imageMagickEnabled;
	}

	protected boolean isImageMagickEnabled() {
		return _imageMagickEnabled;
	}

	public void setImageMagickWindows(boolean imageMagickWindows) {
		this._imageMagickWindows = imageMagickWindows;
	}

	protected boolean isImageMagickWindows() {
		return _imageMagickWindows;
	}

	public void setImageMagickPath(String imageMagickPath) {
		this._imageMagickPath = imageMagickPath;
	}

	protected String getImageMagickPath() {
		return _imageMagickPath;
	}

	private IImageDimensionReader _imageDimensionReader;

    private Map<String, String> _imageResizerClasses;

	private boolean _imageMagickEnabled;

	private boolean _imageMagickWindows;

	private String _imageMagickPath;

}