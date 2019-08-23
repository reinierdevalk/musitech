/**********************************************

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see
<http://www.gnu.org/licenses/>.
In addition to the rights granted to the GNU General Public License,
you opt to use this program as specified in the following:

MUSITECH LINKING EXCEPTION

Linking this library statically or dynamically with other modules is making
a combined work based on this library. Thus, the terms and conditions of the
GNU General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you permission
to link this library with independent modules to produce an executable, regardless
of the license terms of these independent modules, and to copy and distribute the
resulting executable under terms of your choice, provided that you also meet,
for each linked independent module, the terms and conditions of the license of
that module. An independent module is a module which is not derived from or based
on this library.

For the MUSITECH library, this exceptional permission described in the paragraph
above is subject to the following three conditions:
- If you modify this library, you must extend the GNU General Public License and
       this exception including these conditions to your version of the MUSITECH library.
- If you distribute a combined work with this library, you have to mention the
       MUSITECH project and link to its web site www.musitech.org in a location
       easily accessible to the users of the combined work (typically in the "About"
       section of the "Help" menu) and in any advertising material for the combined
       software.
- If you distribute a combined work with the MUSITECH library, you allow the MUSITECH
               project to use mention your combined work for promoting the MUSITECH project.
       For the purpose of this licence, 'distribution' includes the provision of software
       services (e.g. over the World Wide Web).

**********************************************/
package de.uos.fmt.musitech.data.metadata;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.media.image.ImageURL;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * A MetaDataValue contains metadata with the value as an Object and its type. Types
 * use MIME, but also types for smaller objects (e.g. number, character). Values
 * are registered for a specified language. If no language is specified, the
 * value is taken as the standard value.
 * 
 * TODO wo ist die liste der unterstuetzten typen ?
 * 
 * @author Tillman Weyde, Kerstin Neubarth
 * @version 0.114
 * 
 * @hibernate.class table = "MetaDataValue"
 */
public class MetaDataValue implements java.io.Serializable, IMPEGSerializable {

    private static final long serialVersionUID = -4263516283686840549L;
    
    //	private java.lang.Object metaValue;
    private java.lang.String metaType;
    private Long uId;

    /**
     * HashMap mapping a language to the meta data value. The Map's key
     * (language) is of type String, the Map's value (meta data value) of type
     * Object.
     */
    private Map<String,Object> languageToValues = new HashMap<String,Object>();

    private String activeLanguage = null;

    /**
     * Empty constructor. Creates a MetaDataValue with null type and value.
     */
    public MetaDataValue() {
    }

    /**
     * Constructs a new MetaDataValue with given (MIME) type and Object value.
     * 
     * @param mimeType
     *            String indicating the (MIME) type of the value
     * @param value
     *            Object being the meta data value
     */
    public MetaDataValue(String mimeType, Object value) {
        //	   this.metaValue = value;
        this.metaType = mimeType;
        setMetaValue(value);
    }

    /**
     * Returns the type of the meta data value.
     * 
     * @return java.lang.String giving the data type of the meta data value
     * 
     * @hibernate.property
     */
    public java.lang.String getMetaType() {
        return metaType;
    }

    /**
     * Get the value as an object. Creation date: (10.1.2002 17:01:27)
     * 
     * @return java.lang.Object
     * 
     * @hibernate.property type="serializable"
     */
    public java.lang.Object getMetaValue() {
        //		return metaValue;
        return getMetaValue(activeLanguage);
    }

    /**
     * Returns the MetaDataValue registered for the specified language. The argument
     * might be null.
     * 
     * TODO if no key language: return null or return value for null-key
     * (standard)?
     * 
     * @param language
     *            String giving the language specification, or null
     * @return Object the MetaDataValue for the given language specification
     */
    public Object getMetaValue(String language) {
        //	    return languageToValues.get(language);
        if (languageToValues.get(language) != null) {
            return languageToValues.get(language);
        }
        return languageToValues.get(null);
    }

    /**
     * Set the type. Creation date: (10.1.2002 17:44:19)
     * 
     * @param newMimeType
     *            java.lang.String
     */
    public void setMetaType(java.lang.String newMimeType) {
        metaType = newMimeType;
    }

    /**
     * Sets the specified Object as the value for the standard language or no
     * language specification.
     * 
     * @param newValue
     *            Object to be the (standard) value of this MetaDataValue
     */
    public void setMetaValue(java.lang.Object newValue) {
        //		metaValue = newValue;
        setMetaValue(null, newValue);
    }

    /**
     * Sets the language dependent value of the MetaDataValue to the specified
     * Object. The String argument might be null.
     * 
     * @param language
     *            String giving the language specification, or null
     * @param newValue
     *            Object to be the value for the specified language
     */
    public void setMetaValue(String language, Object newValue) {
        languageToValues.put(language, newValue);
    }

    /**
     * Sets the currently active language. Default is null (the standard).
     * 
     * @param language
     *            String giving the language
     */
    public void setActiveLanguage(String language) { //TODO umbennen, trennen
        // zwischen Standard und
        // Einstellung
        this.activeLanguage = language;
    }

    /**
     * Registers the given Object as language dependent value for the specified
     * language.
     * 
     * @param value
     *            Object giving a language dependent meta data value
     * @param language
     *            String indicating the language
     */
    public void addValueForLanguage(Object value, String language) {
        languageToValues.put(language, value);
    }

    /**
     * Returns a String representation of this MetaDataValue.
     * 
     * @return String representing the data of this MetaDataValue
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        //		StringBuffer buffer = new StringBuffer("MetaDataValue: ");
        //		String valueToString = "";
        //		if (getMetaValue()==null)
        //			valueToString = "null";
        //		else
        //			valueToString = getMetaValue().toString();
        //		if (valueToString.equals(""))
        //			valueToString = "''";
        //		buffer.append(" type "+metaType).append(" value "+valueToString);
        //		return buffer.toString();
        return toString(null);
    }

    /**
     * Returns a String representation of the language dependent meta data. The
     * argument might be null, in this case the standard value is returned.
     * 
     * @param language
     *            String giving the language specification, or null
     * @return String representing the data of this MetaDataValue for the given
     *         language
     */
    public String toString(String language) {
        StringBuffer buffer = new StringBuffer("MetaDataValue: ");
        String valueToString = "";
        if (getMetaValue(language) == null)
            valueToString = "null";
        else
            valueToString = getMetaValue(language).toString();
        if (valueToString.equals(""))
            valueToString = "''";
        buffer.append(" type " + metaType).append("  value " + valueToString);
        return buffer.toString();
    }

    /**
     * Returns a list of the languages for which values are distinguished.
     * 
     * @return Collection containing the known language specifications for this
     *         MetaDataValue
     */
    public Collection<String> returnLanguageOptions() {
        return languageToValues.keySet();
    }

    /**
     * TODO check this
     * @param data The data to convert. 
     * @param mimeType ignored
     * @return The Image object.
     * @see Toolkit#createImage(byte[])
     */
    public static Image getImageFromData(byte data[], String mimeType) {
        return Toolkit.getDefaultToolkit().createImage(data);
        /*
         * int index = type.lastIndexOf('/'); if(index>-1) type =
         * type.substring(index+1); try { File tmpFile =
         * File.createTempFile("Image",type); FileOutputStream fos = new
         * FileOutputStream(tmpFile); fos.write(data); fos.close(); return
         * Toolkit.getDefaultToolkit().getImage(tmpFile.getAbsolutePath()); }
         * catch (Exception e) { return null; } // read image object from data
         */
    }

    public static String getStringFromData(byte data[]) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            InputStreamReader isr = new InputStreamReader(bais, "UTF-8");
            char chars[] = new char[data.length];
            int strLen = isr.read(chars);
            return new String(chars, 0, strLen);
        } catch (Exception e) {
            return null;
        }
        // read image object from data
    }

    /** read data from an image object */
    public static byte[] getJpegDataFromImage(Image img, ImageObserver iob) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
        // convert the Image into BufferedImage
        int w = img.getWidth(iob);
        int h = img.getHeight(iob);
        BufferedImage bImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D bImageContext = bImage.createGraphics();
        bImageContext.drawImage(img, 0, 0, null);
        try {
            encoder.encode(bImage);
            baos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getDataFromFile(File f) {
        if (!f.exists())
            return null;
        byte data[] = new byte[(int) f.length()];
        try {
            FileInputStream fis = new FileInputStream(f);
            fis.read(data);
            fis.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // read image object from data
    }

    public static byte[] getDataFromURL(URL url) {
        byte data[] = new byte[1000];
        try {
            InputStream is = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int available = is.available();
            while (available > 0) {
                is.read(data, 0, Math.min(data.length, available));
                baos.write(data, 0, Math.min(data.length, available));
                available = is.available();
            }
            is.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // read image object from data
    }

    public static byte[] getDataFromString(String str) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter stw = new OutputStreamWriter(baos);
        try {
            stw.write(str);
            stw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return baos.toByteArray();
    }

    /**
     * @return Returns the id.
     * 
     * @hibernate.id generator-class="native"
     */
    private Long getUid() {
        return uId;
    }

    /**
     * @param id
     *            The id to set.
     */
    private void setUid(Long id) {
        this.uId = id;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
     */
    public boolean toMPEG(MusiteXMLSerializer instance, Node parent, Object object, String fieldname) {
        Element metaValue = XMLHelper.addElement(parent, "metaDataValue");
        if (instance.knowsObject(metaValue, object))
            return true;

        // create child-elements

        metaValue.setAttribute("type", this.getMetaType());

        /*
         * default
         */

        Element defaultMV = XMLHelper.addElement(metaValue, "default");
        if (activeLanguage != null) {
            defaultMV.setAttribute("language", activeLanguage);
        }
        Object defaultMVObj = getMetaValue();
        if (defaultMVObj instanceof URL) {
            defaultMV.setAttribute("isURL", "true");
        }
        XMLHelper.addText(defaultMV, defaultMVObj.toString());

        /*
         * translations
         */
        for (String lang: languageToValues.keySet()) {
            if (lang != activeLanguage) {
                Object transMVObj = languageToValues.get(lang);
                Element transMV = XMLHelper.addElement(metaValue, "translation");
                if (transMVObj instanceof URL) {
                    transMV.setAttribute("isURL", "true");
                }
                transMV.setAttribute("language", lang);
                XMLHelper.addText(transMV, transMVObj.toString());
            }
        }

        return true;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Element)
     */
    public Object fromMPEG(MusiteXMLSerializer instance, Element node) {
        // reference-handling
        Object reference = instance.getReferenced(node, this);
        if (reference != null)
            return reference;
        /*
         * type
         */
        String dataType = node.getAttribute("type");
        this.setMetaType(dataType);
        /*
         * default
         */
        Element defaultMV = XMLHelper.getElement(node, "default");
        setActiveLanguage(defaultMV.getAttribute("language"));
        if ("true".equals(defaultMV.getAttribute("isURL"))) {
            try {
                URL url = new URL(XMLHelper.getText(defaultMV));
                if ("Image".equalsIgnoreCase(dataType)) {
                    this.setMetaValue(new ImageURL(url));
                } else if ("Sound".equalsIgnoreCase(dataType)) {
                    this.setMetaValue(new AudioFileObject(url));
                } else {
                    this.setMetaValue(url);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (DOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.setMetaValue(XMLHelper.getText(defaultMV));
        }
        /*
         * translation
         */
        NodeList translationList = node.getElementsByTagName("translation");
        for (int i = 0; i < translationList.getLength(); i++) {
            Element translation = (Element) translationList.item(i);
            String lang = translation.getAttribute("language");
            if ("true".equals(translation.getAttribute("isURL"))) {
                try {
                    this.setMetaValue(lang, new URL(translation.getAttribute("value")));
                    // TODO Image und Sound wie oben behandeln oder insgesamt
                    // bessere Loesung fuer den Typ der MetaValues schaffen
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                this.setMetaValue(lang, translation.getAttribute("value"));
            }
        }
        return this;
    }
}