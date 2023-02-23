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
/*
 * File RenderingHints.java
 * Created on 30.08.2004
 */

package de.uos.fmt.musitech.data.rendering;

import java.awt.Color;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import de.uos.fmt.musitech.data.score.CustomSVGGraphic;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * <code>RenderingHints</code> contain information about how to show certain
 * features of an object. These information are recorded in a HashMap with the
 * features as keys and the features' value as the value of the corresponding
 * map entry. The keys are of type String. <br>
 * <br>
 * Well known features and values are: <br>
 * "barline" -> "full", "half", "none" (see e.g. NoteList, to be evluated by
 * NotationDisplay) <br>
 * "time signature" -> "standard", "none" (see e.g. NoteList, to be evluated by
 * NotationDisplay) <br>
 * "color" -> rrggbb or rrggbbaa ( a string with the red, green, blue, and alpha
 * values in hex notation). <br>
  * "stem direction" -> "up" or "down" ( indicating that stems should point up or
 * down ).<br>
 * "staff lines" -> "1" or "5" ( indicating the number of staff lines, one is
 * for rhythm ).<br>
 * "hidden" -> "true" or "false" ( indicating whether the object should be
 * displayed visually ).
 * 
 * @author tweyde, Kerstin Neubarth
 */
public class RenderingHints implements Serializable, IMPEGSerializable {

    private static final long serialVersionUID = 22120638274095132l;
    /**
     * HashMap containing the mapping of object features to values. The keys are
     * the features which are to be shown in an certain way, which is specified
     * by the value.
     */
    private Map featureValueMap;
    private Class parentClass;
    static HashMap validRHs;

    static {
        loadKnownHints();
    }

    /**
     * Constructor. Creates the (empty) HashMap of this
     * <code>RenderingHints</code>.
     */
    public RenderingHints() {
        featureValueMap = new HashMap();
    }

    /**
     * Returns the Object which is the value for the specified <code>key</code>
     * in the HashMap of this <code>RenderingHints</code>. If the HashMap
     * does not contain the specified String as a key, null is returned. Null
     * might also be returned, if the value for the <code>key</code> is null.
     * 
     * @param key
     *            String indicating the key of the RenderingHint's HashMap whose
     *            value is requested
     * @return Object being the value to which the specified String leads in the
     *         RenderingHint's HashMap, might be null
     */
    public Object getValue(String key) {
        return featureValueMap.get(key);
    }

    /**
     * Returns true, if the HashMap of this <code>RenderingHints</code>
     * contains the specified String as a key. If the map does not know the
     * <code>key</code>, false is returned.
     * 
     * @param key
     *            String to be checked if being a key in the HashMap of this
     *            <code>RenderingHints</code>
     * @return boolean true if the HashMap of this <code>RenderingHints</code>
     *         contains the specified String as a key, false otherwise
     */
    public boolean containsKey(String key) {
        return featureValueMap.containsKey(key);
    }

    public void checkHint(String key, String value) {
        if (parentClass != null && validRHs != null) {
            HashMap map = (HashMap) validRHs.get(parentClass.getName());
            if (map == null || !map.containsKey(key)) {
                System.out.println("Warning: " + key + " is an unknown rendering Hint for " + parentClass);
                return;
            }
            if (!map.get(key).equals(value)) {
                System.out.println("Warning: " + key + " should have a value of " + map.get(key) + " but has a "
                                   + value);
                return;
            }
        }
    }

    /**
     * Puts the entry given by the specified String (as key) and Object (as
     * value) in to the HashMap of this <code>RenderingHints</code>. If the
     * map has contained an entry for the same key before, the previous value is
     * returned.
     * 
     * @param key
     *            String being the key of the entry to be added to the HashMap
     *            of this <code>RenderingHints</code>
     * @param value
     *            Object being the value of the entry to be added to the HashMap
     *            of this <code>RenderingHints</code>
     * @return Object null if the specified String has not been contained as a
     *         key before, the previous value of the corresponding entry
     *         otherwise
     */
    public Object registerHint(String key, Object value) {
        checkHint(key, value.getClass().getName());
        return featureValueMap.put(key, value);
    }

    /**
     * This method adds all rendering hints from rh into this object. If a
     * rendering from rh already exists in this, then the "old" rendering hint
     * os overridden.
     * 
     * @param rh
     */
    public void add(RenderingHints rh) {
        for (Iterator iter = rh.getKeySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            registerHint(element, rh.getValue(element));
        }
    }

    /**
     * Removes the entry in the HashMap of this <code>RenderingHints</code>
     * whose key is the specified String. If the map does not contain this key,
     * null is returned, otherwise the value of the removed entry is returned.
     * 
     * @param key
     *            String whose entry is to be removed from the HashMap of this
     *            <code>RenderingHints</code>
     * @return Object the value of the removed entry or null if the map does not
     *         contain an entry for the specified key
     */
    public Object removeHint(String key) {
        return featureValueMap.remove(key);
    }

    /**
     * Returns the key set of this <code>RenderingHints</code>' HashMap.
     * 
     * @return Set containing the keys of the HashMap of this
     *         <code>RenderingHints</code>
     */
    public Set getKeySet() {
        return featureValueMap.keySet();
    }

    /**
     * Returns the entry set of this <code>RenderingHints</code>' HashMap.
     * 
     * @return Set containing the entries of the HashMap of this
     *         <code>RenderingHints</code>
     */
    public Set getEntrySet() {
        return featureValueMap.entrySet();
    }

    private static void loadKnownHints() {
        DefaultHandler handler = new Parser();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            URL url = RenderingHints.class.getResource("RenderingHints.xml");
            parser.parse(url.toExternalForm(), handler);
        } catch (Exception e) {
        	if(DebugState.DEBUG_RENDERING)
        		e.printStackTrace();
        }
        validRHs = ((Parser) handler).allowedRHs;
    }

    public Class getParentClass() {
        return parentClass;
    }

    public void setParentClass(Class parentClass) {
        this.parentClass = parentClass;
        if (validRHs != null && validRHs.containsKey(parentClass.getName())) {
            for (Iterator iter = featureValueMap.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                checkHint(key, featureValueMap.get(key).getClass().getName());
            }
        }
    }
    
    /**
     * Get the map used internally for rendering hints, should be used 
     * for persistence and editing framework only. 
     * @return A Map containing the.  
     */
    public Map getContentsMap(){
        return featureValueMap;
    }

    static class Parser extends DefaultHandler {

        HashMap allowedRHs = new HashMap();

        private String classname;

        @Override
		public void startElement(String namespaceURI, String sname, String qname, Attributes attrs) {
            String name = sname;
            if ("".equals(name))
                name = qname;

            if (name.equals("RenderingHints")) {
                classname = null;
                for (int i = 0; i < attrs.getLength(); i++) {
                    if (attrs.getQName(i).equals("java-class")) {
                        classname = attrs.getValue(i);
                        break;
                    }
                }
                if (classname == null) {
                    System.err.println("a java-class attribute is missing in RenderingHints.xml");
                    return;
                }
            }
        }

        private StringBuffer buf = null;

        @Override
		public void characters(char[] chars, int offset, int length) {
            String s = new String(chars, offset, length);
            if (buf == null)
                buf = new StringBuffer(s);
            else
                buf.append(s);
        }

        private String key, value;

        @Override
		public void endElement(String namespaceURI, String sname, String qname) {
            String name = sname;
            if ("".equals(name))
                name = qname;

            if (name.equals("key")) {
                key = buf.toString().trim();
            } else if (name.equals("value")) {
                value = buf.toString().trim();
            } else if (name.equals("RenderingHint")) {
                HashMap rhs;
                if (allowedRHs.containsKey(classname)) {
                    rhs = (HashMap) allowedRHs.get(classname);
                } else {
                    rhs = new HashMap();
                    allowedRHs.put(classname, rhs);
                }
                rhs.put(key, value);
            }
            buf = null;
        }
    }

    /**
     * Calculates a String as needed for color values in RenderingHints from a
     * java.awt.Color object.
     * 
     * @see RenderingHints
     * @param col
     *            The color to represent.
     * @return The string representation.
     */
    public String colorToString(Color col) {
        int argb = col.getRGB();
        int alpha = (argb & 0xff000000) >> 24;
        argb = (argb << 8) | (alpha & 0x000000ff);
        StringBuffer sb = new StringBuffer(Integer.toHexString(argb));
        for (int i = sb.length(); i < 8; i++) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    /**
     * Calculates a Color-object as needed for color values in RenderingHints
     * from a String.
     * 
     * @see RenderingHints
     * @param rgbStr
     *            The string representation.
     * @return The color to represent.
     */
    public Color stringToColor(String rgbStr) {
        try {
            int r = Integer.parseInt(rgbStr.substring(0, 2), 16);
            int g = Integer.parseInt(rgbStr.substring(2, 4), 16);
            int b = Integer.parseInt(rgbStr.substring(4, 6), 16);
            int alpha = 0xFF;
            if (rgbStr.length() >= 8) {
                alpha = Integer.parseInt(rgbStr.substring(6, 8), 16);
            }
            return new Color(r, g, b, alpha);
        } catch (RuntimeException e) {
            if (DebugState.DEBUG)
                e.printStackTrace();
            return null;
        }
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
     */
    @Override
	public boolean toMPEG(MusiteXMLSerializer instance, Node parent, Object object, String fieldname) {
        // create 'piece' element

        //if (instance.knowsObject(rhints, object))
        //    return true;

        for (Iterator iter = featureValueMap.keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            Object value = featureValueMap.get(key);
            if (key.equals("tabulatur note")) {
                if (parent.getNodeName().equals("head")) {
                    instance.writeXML(parent, value, null, null);
                } else if (parent.getNodeName().equals("chord")) {
                    // ignore, because tabnotes are in head too
                } else {
                    assert false;
                }
            } else if (key.equals("custom head") || key.equals("custom clef") || key.equals("preview")) {
                // SVG
                addSVG(parent, value);
            } else if (key.equals("tremolo")) {
                try {
                    int tremBars = Integer.parseInt(value.toString());
                    if (tremBars > 0)
                        XMLHelper.getOrCreateElement((Element) parent, "stem").setAttribute("tremoloBars",
                                                                                            value.toString());
                } catch (Exception e) {
                    if (DebugState.DEBUG)
                        System.out.println("WARINNG: " + getClass().getName()
                                           + ".toMPEG() value for key 'tremolo' is invalid.");
                }
            } else if ("draw stem".equals(key)) {
                assert ("chord".equals(parent.getNodeName()));
                boolean invis = !((Boolean) value).booleanValue();
                XMLHelper.getOrCreateElement((Element) parent, "stem").setAttribute("invisible", "" + invis);
            } else if ("stem direction".equals(key)) {
                assert ("chord".equals(parent.getNodeName()));
                XMLHelper.getOrCreateElement((Element) parent, "stem").setAttribute("dir", value.toString());
            } else if ("page bottom space".equals(key)) {
                // TODO ??? assert ("score".equals(parent.getNodeName()));
                XMLHelper.getOrCreateElement(XMLHelper.getOrCreateElement((Element) parent, "layout"), "pages")
                        .setAttribute("bottom", value.toString());
            } else if ("time signature preview".equals(key)) {
                Node rhints = XMLHelper.getOrCreateElement((Element) parent, "renderingHints");
                Element rhint = XMLHelper.addElement(rhints, "renderingHint");
                rhint.setAttribute("key", key.toString());
                int[] values = (int[]) value;
                rhint.setAttribute("value", values[0] + "/" + values[1]);
            } else if (key.equals("beam.linear duration progression")) {
                Node rhints = XMLHelper.getOrCreateElement((Element) parent, "renderingHints");
                Rational[] values = (Rational[]) value;
                Element rhint = XMLHelper.addElement(rhints, "renderingHint");
                rhint.setAttribute("key", key.toString());
                rhint.setAttribute("value", values[0].getNumer() + "/" + values[0].getDenom() + "; "
                                            + values[1].getNumer() + "/" + values[1].getDenom());
            } else if (key.equals("@@@collins super hack@@@")) {
                // TODO ignore???
            } else if (key.equals("custom")) {
                Node rhints = XMLHelper.getOrCreateElement((Element) parent, "renderingHints");
                Element rhint = XMLHelper.addElement(rhints, "renderingHint");
                // default case
                rhint.setAttribute("key", key.toString());
                float[] values = (float[]) value;
                rhint.setAttribute("value", values[0] + "/" + values[1]);
            } else {
                Node rhints = XMLHelper.getOrCreateElement((Element) parent, "renderingHints");
                Element rhint = XMLHelper.addElement(rhints, "renderingHint");
                // default case
                rhint.setAttribute("key", key.toString());
                rhint.setAttribute("value", value.toString());
            }
        }

        return true;
    }

    /**
     * @param parent
     * @param value
     */
    private void addSVG(Node parent, Object value) {
        CustomSVGGraphic svgHead = (CustomSVGGraphic) value;
        Element drawObj = XMLHelper
                .addElement(XMLHelper.getOrCreateElement((Element) parent, "drawObjects"), "drawObj");
        Element svg = XMLHelper.addElement(drawObj, "svg");
        Element basic = XMLHelper.addElement(drawObj, "basic");//TODO
        svg.setAttribute("url", svgHead.getUri());
        svg.setAttribute("width", "" + svgHead.getWidth());
        svg.setAttribute("height", "" + svgHead.getHeight());
        svg.setAttribute("x", "" + svgHead.getCutX());
        svg.setAttribute("y", "" + svgHead.getCutY());
        // TODO convert vertAlign to Capella Format
        basic.setAttribute("vertAlign", "" + svgHead.getVerticalAlignment());
        // TODO integrate vertShift into Schema
        basic.setAttribute("vertShift", "" + svgHead.getVerticalShift());
        if (DebugState.DEBUG_MPEG && svgHead.getLeadingText() != null && !"".equals(svgHead.getLeadingText()))
            System.out.println("[WARNING] unserialized SVG-field: " + svgHead.getLeadingText());
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Element)
     */
    @Override
	public Object fromMPEG(MusiteXMLSerializer instance, Element node) {
        // reference-handling
        Object reference = instance.getReferenced(node, this);
        if (reference != null)
            return reference;

        NodeList rhintList = node.getElementsByTagName("renderingHint");
        for (int i = 0; i < rhintList.getLength(); i++) {
            Element rhint = (Element) rhintList.item(i);
            String key = rhint.getAttribute("key");
            String val = rhint.getAttribute("value");
            if ("note spacing".equals(key) || "duration extension pulldown".equals(key)) {
                // FLOAT
                featureValueMap.put(key, new Float(val));
            } else if ("draw tabulatur".equals(key) || "draw duration extension".equals(key) || "visible".equals(key)) {
                // BOOLEAN
                featureValueMap.put(key, new Boolean(val));
            } else if ("number of lines".equals(key)) {
                // INTEGER
                featureValueMap.put(key, new Integer(val));
            } else if ("time signature preview".equals(key)) {
                // INTEGER[] representing Rational
                int sepIdx = val.indexOf('/');
                assert (sepIdx > 0);
                featureValueMap.put(key, new int[] {Integer.parseInt(val.substring(0, sepIdx)),
                                                     Integer.parseInt(val.substring(sepIdx + 1))});
            } else if ("custom".equals(key)) {
                // FLOAT[] representing Rational
                int sepIdx = val.indexOf('/');
                assert (sepIdx > 0);
                featureValueMap.put(key, new float[] {Float.parseFloat(val.substring(0, sepIdx)),
                                                       Float.parseFloat(val.substring(sepIdx + 1))});
            } else if ("spaced notes".equals(key)) {
                // RATIONAL
                int sepIdx = val.indexOf('/');
                assert (sepIdx > 0);
                Rational rVal = new Rational(Integer.parseInt(val.substring(0, sepIdx)), Integer.parseInt(val
                        .substring(sepIdx + 1)));
                featureValueMap.put(key, rVal);
            } else if ("custom head".equals(key)) {
                assert false;
            } else if ("custom clef".equals(key)) {
                assert false;
            } else if ("@@@collins super hack@@@".equals(key)) {
                // TODO ignore???
            } else if ("beam.linear duration progression".equals(key)) {
                int commaIdx = val.indexOf(';');
                String r1 = val.substring(0, commaIdx).trim();
                String r2 = val.substring(commaIdx + 1).trim();
                int slashIdx1 = r1.indexOf('/');
                int slashIdx2 = r2.indexOf('/');
                Rational rat1 = new Rational(Integer.parseInt(r1.substring(0, slashIdx1)), Integer.parseInt(r1
                        .substring(slashIdx1 + 1)));
                Rational rat2 = new Rational(Integer.parseInt(r2.substring(0, slashIdx2)), Integer.parseInt(r2
                        .substring(slashIdx2 + 1)));
                featureValueMap.put(key, new Rational[] {rat1, rat2});
            } else {
                featureValueMap.put(key, val);
            }
        }

        return this;
    }
}