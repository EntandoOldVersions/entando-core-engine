/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.services.api.provider.json;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.provider.JSONUtils;
import org.apache.cxf.jaxrs.provider.PrefixRespectingMappedNamespaceConvention;
import org.apache.cxf.staxutils.DelegatingXMLStreamWriter;
import org.apache.cxf.staxutils.StaxUtils;
import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.TypeConverter;
import org.entando.entando.aps.system.services.api.model.CDataAdapter;

/**
 * @author E.Santoboni
 */
@Produces("application/json")
@Consumes("application/json")
@Provider
public class JSONProvider extends org.apache.cxf.jaxrs.provider.JSONProvider {

    private static final String MAPPED_CONVENTION = "mapped";
    private static final String BADGER_FISH_CONVENTION = "badgerfish";

    @Override
    protected XMLStreamWriter createWriter(Object actualObject,
            Class<?> actualClass, Type genericType, String enc,
            OutputStream os, boolean isCollection) throws Exception {
        if (BADGER_FISH_CONVENTION.equals(convention)) {
            return JSONUtils.createBadgerFishWriter(os);
        }
        QName qname = getQName(actualClass, genericType, actualObject, true);
        Configuration config = JSONUtils.createConfiguration(namespaceMap,
                writeXsiType && !ignoreNamespaces, attributesToElements,
                typeConverter);

        XMLStreamWriter writer = createStreamWriter(os, qname,
                writeXsiType && !ignoreNamespaces, config, serializeAsArray,
                arrayKeys, isCollection || dropRootElement);
        writer = createIgnoreMixedContentWriterIfNeeded(writer, ignoreMixedContent);
        writer = createIgnoreNsWriterIfNeeded(writer, ignoreNamespaces);
        return createTransformWriterIfNeeded(writer, os);
    }

    public static XMLStreamWriter createIgnoreMixedContentWriterIfNeeded(XMLStreamWriter writer, boolean ignoreMixedContent) {
        return ignoreMixedContent ? new IgnoreMixedContentWriter(writer) : writer;
    }

    public static XMLStreamWriter createIgnoreNsWriterIfNeeded(XMLStreamWriter writer, boolean ignoreNamespaces) {
        return ignoreNamespaces ? new CDataIgnoreNamespacesWriter(writer) : writer;
    }

    private static class IgnoreMixedContentWriter extends DelegatingXMLStreamWriter {

        String lastText;
        boolean isMixed;
        List<Boolean> mixed = new LinkedList<Boolean>();

        public IgnoreMixedContentWriter(XMLStreamWriter writer) {
            super(writer);
        }

        public void writeCharacters(String text) throws XMLStreamException {
            if (CDataAdapter.isCdata(new String(text))) {
                text = CDataAdapter.parse(new String(text));
            }
            if (StringUtils.isEmpty(text.trim())) {
                lastText = text;
            } else if (lastText != null) {
                lastText += text;
            } else if (!isMixed) {
                super.writeCharacters(text);
            } else {
                lastText = text;
            }
        }

        public void writeStartElement(String prefix, String local, String uri) throws XMLStreamException {
            if (lastText != null) {
                isMixed = true;
            }
            mixed.add(0, isMixed);
            lastText = null;
            isMixed = false;
            super.writeStartElement(prefix, local, uri);
        }

        public void writeStartElement(String uri, String local) throws XMLStreamException {
            if (lastText != null) {
                isMixed = true;
            }
            mixed.add(0, isMixed);
            lastText = null;
            isMixed = false;
            super.writeStartElement(uri, local);
        }

        public void writeStartElement(String local) throws XMLStreamException {
            if (lastText != null) {
                isMixed = true;
            }
            mixed.add(0, isMixed);
            lastText = null;
            isMixed = false;
            super.writeStartElement(local);
        }

        public void writeEndElement() throws XMLStreamException {
            if (lastText != null && (!isMixed || !StringUtils.isEmpty(lastText.trim()))) {
                super.writeCharacters(lastText.trim());
            }
            super.writeEndElement();
            isMixed = mixed.get(0);
            mixed.remove(0);
        }
    }
    
    private static final Charset UTF8 = Charset.forName("utf-8");

    public static XMLStreamWriter createStreamWriter(OutputStream os,
            QName qname, boolean writeXsiType, Configuration config,
            boolean serializeAsArray, List<String> arrayKeys,
            boolean dropRootElement) throws Exception {
        MappedNamespaceConvention convention = new PrefixRespectingMappedNamespaceConvention(config);
        AbstractXMLStreamWriter xsw = new CDataMappedXMLStreamWriter(convention, new OutputStreamWriter(os, UTF8));
        if (serializeAsArray) {
            if (arrayKeys != null) {
                for (String key : arrayKeys) {
                    xsw.serializeAsArray(key);
                }
            } else {
                String key = getKey(convention, qname);
                xsw.serializeAsArray(key);
            }
        }
        XMLStreamWriter writer = (!writeXsiType || dropRootElement) ? new IgnoreContentJettisonWriter(
                xsw, writeXsiType, dropRootElement ? qname : null) : xsw;
        return writer;
    }

    private static String getKey(MappedNamespaceConvention convention, QName qname) throws Exception {
        return convention.createKey(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart());
    }

    private static class IgnoreContentJettisonWriter extends DelegatingXMLStreamWriter {

        private boolean writeXsiType;
        private QName ignoredQName;
        private boolean rootDropped;
        private int index;

        public IgnoreContentJettisonWriter(XMLStreamWriter writer, boolean writeXsiType, QName qname) {
            super(writer);
            this.writeXsiType = writeXsiType;
            ignoredQName = qname;
        }
        
        public void writeAttribute(String prefix, String uri, String local, String value) throws XMLStreamException {
            if (!writeXsiType && "type".equals(local) && "xsi".equals(prefix)) {
                return;
            }
            super.writeAttribute(prefix, uri, local, value);
        }
        
        public void writeStartElement(String prefix, String local, String uri) throws XMLStreamException {
            index++;
            if (ignoredQName != null && ignoredQName.getLocalPart().equals(local) && ignoredQName.getNamespaceURI().equals(uri)) {
                rootDropped = true;
                return;
            }
            super.writeStartElement(prefix, local, uri);
        }
        
        public void writeEndElement() throws XMLStreamException {
            index--;
            if (rootDropped && index == 0) {
                return;
            }
            super.writeEndElement();
        }
        
        public void writeCharacters(String text) throws XMLStreamException {
            if (CDataAdapter.isCdata(new String(text))) {
                String parsedCDataText = CDataAdapter.parse(new String(text));
                super.writeCharacters(parsedCDataText);
            } else {
                super.writeCharacters(text);
            }
        }
        
    }

    private QName getQName(Class<?> cls, Type type, Object object, boolean allocatePrefix) throws Exception {
        QName qname = getJaxbQName(cls, type, object, false);
        if (qname != null) {
            String prefix = getPrefix(qname.getNamespaceURI(), allocatePrefix);
            return new QName(qname.getNamespaceURI(), qname.getLocalPart(), prefix);
        }
        return null;
    }

    private String getPrefix(String namespace, boolean allocatePrefix) {
        String prefix = namespaceMap.get(namespace);
        if (prefix == null) {
            if (allocatePrefix && namespace.length() > 0) {
                prefix = "ns" + (namespaceMap.size() + 1);
                namespaceMap.put(namespace, prefix);
            } else {
                prefix = "";
            }
        }
        return prefix;
    }
    
    protected XMLStreamWriter createTransformWriterIfNeeded(XMLStreamWriter writer, OutputStream os) {
        /*
        return TransformUtils.createTransformWriterIfNeeded(writer, os, 
        outElementsMap,
        outDropElements,
        outAppendMap,
        attributesToElements,
        null);
         */
        return createTransformWriterIfNeeded(writer, os,
                outElementsMap, outDropElements, outAppendMap, this.isAttributesToElements(), null);
    }

    public static XMLStreamWriter createNewWriterIfNeeded(XMLStreamWriter writer, OutputStream os) {
        return writer == null ? StaxUtils.createXMLStreamWriter(os) : writer;
    }

    public static XMLStreamWriter createTransformWriterIfNeeded(
            XMLStreamWriter writer, OutputStream os,
            Map<String, String> outElementsMap, List<String> outDropElements,
            Map<String, String> outAppendMap, boolean attributesToElements, String defaultNamespace) {
        if (outElementsMap != null || outDropElements != null
                || outAppendMap != null || attributesToElements) {
            writer = createNewWriterIfNeeded(writer, os);
            writer = new CDataOutTransformWriter(writer, outElementsMap,
                    outAppendMap, outDropElements, attributesToElements, defaultNamespace);
        }
        return writer;
    }
    
    public void setWriteXsiType(boolean writeXsiType) {
        super.setWriteXsiType(writeXsiType);
        this.writeXsiType = writeXsiType;
    }
    
    public void setIgnoreNamespaces(boolean ignoreNamespaces) {
        super.setIgnoreNamespaces(ignoreNamespaces);
        this.ignoreNamespaces = ignoreNamespaces;
    }
    
    public void setSerializeAsArray(boolean serializeAsArray) {
        super.setSerializeAsArray(serializeAsArray);
        this.serializeAsArray = serializeAsArray;
    }
    
    public void setArrayKeys(List<String> arrayKeys) {
        super.setArrayKeys(arrayKeys);
        this.arrayKeys = arrayKeys;
    }
    
    public void setDropRootElement(boolean dropRootElement) {
        this.dropRootElement = dropRootElement;
    }
    
    public void setTypeConverter(TypeConverter typeConverter) {
        super.setTypeConverter(typeConverter);
        this.typeConverter = typeConverter;
    }
    
    public void setIgnoreMixedContent(boolean ignoreMixedContent) {
        super.setIgnoreMixedContent(ignoreMixedContent);
        this.ignoreMixedContent = ignoreMixedContent;
    }

    protected boolean isAttributesToElements() {
        return _attributesToElements;
    }
    
    public void setAttributesToElements(boolean attributesToElements) {
        super.setAttributesToElements(attributesToElements);
        this._attributesToElements = attributesToElements;
    }

    public String getConvention() {
        return convention;
    }
    
    public void setConvention(String convention) {
        super.setConvention(convention);
        this.convention = convention;
    }

    public ConcurrentHashMap<String, String> getNamespaceMap() {
        return namespaceMap;
    }

    public void setNamespaceMap(Map<String, String> namespaceMap) {
        super.setNamespaceMap(namespaceMap);
        this.namespaceMap.putAll(namespaceMap);
    }
    
    private boolean writeXsiType = true;
    private boolean ignoreNamespaces;
    private boolean attributesToElements;
    private boolean serializeAsArray;
    private List<String> arrayKeys;
    private boolean dropRootElement;
    private TypeConverter typeConverter;
    private boolean ignoreMixedContent;
    private ConcurrentHashMap<String, String> namespaceMap = new ConcurrentHashMap<String, String>();
    private boolean _attributesToElements;
    private String convention = MAPPED_CONVENTION;
    
}