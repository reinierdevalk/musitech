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
 * Created on 2004-8-24
 */
package de.uos.fmt.musitech.utility.xml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * @author Jens Wissmann
 */
public class XMLHelper {

    /**
     * create the xml-string representation of a document
     * 
     * @param document
     */
    public static String asXML(Document document) {
        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            tf.transform(new DOMSource(document), result);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * parse an xml-string
     * 
     * @param xml
     * @return document
     */
    public static Document parse(String xml) {
        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Result result = new DOMResult(document);
            tf.transform(new StreamSource(new StringReader(xml)), result);
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the next element sibling.
     * 
     * @return org.w3c.dom.Element
     * @param node
     *            org.w3c.dom.Node
     */
    public static Element getNextElementSibling(org.w3c.dom.Node node) {
        node = node.getNextSibling();
        while (node != null && node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
            node = node.getNextSibling();
        return (Element) node;
    }

    /**
     * Returns the first child of a node.
     * 
     * @return org.w3c.dom.Element
     * @param node
     *            org.w3c.dom.Node
     */
    public static Element getFirstElementChild(org.w3c.dom.Node node) {
        node = node.getFirstChild();
        while (node != null && node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
            node = node.getNextSibling();
        return (Element) node;
    }

    /**
     * Returns the first element or if there is no first element the child of a
     * node.
     * 
     * @return org.w3c.dom.Element
     * @param node
     *            org.w3c.dom.Node
     */
    public static Node getFirstElementOrTextChild(org.w3c.dom.Node node) {
        node = node.getFirstChild();
        while (node != null && node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE
               && node.getNodeType() != org.w3c.dom.Node.TEXT_NODE)
            node = node.getNextSibling();
        return node;
    }

    /**
     * Reads from xml file.
     * 
     * @date (21.10.00 14:52:57)
     * @return java.lang.Object
     * @param document
     *            javax.swing.text.Document
     */
    protected static Document transformXML(Document document, File stylesheet) {
        // transform the document
        Document transDocument = ObjectCopy.newDom();
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer;
            if (stylesheet == null || !stylesheet.exists())
                return null;
            StreamSource stylesource = new StreamSource(ObjectCopy.getWriteStylesheet());
            transformer = tFactory.newTransformer(stylesource);

            DOMSource source = new DOMSource(document);
            DOMResult result = new DOMResult(transDocument);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return transDocument;
    }

    /**
     * @param file
     * @return
     */
    public static Document parse(File file) {
        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Result result = new DOMResult(document);
            tf.transform(new StreamSource(new FileReader(file)), result);
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * gets the given element of a parent or creates/appends if no element with
     * the given name is found
     * 
     * @param parent
     * @param elementName
     * @return element <code>drawObjects</code>
     */
    public static Element getOrCreateElement(Element parent, String elementName) {
        Element element = (Element) parent.getElementsByTagName(elementName).item(0);
        if (element == null) {
            return addElement(parent, elementName);
        } else {
            return element;
        }

    }

    /**
     * gets the given element of a parent
     * 
     * @param parent
     * @return element <code>drawObjects</code>
     */
    public static Element getElement(Element parent, String elementName) {
        if (parent == null)
            return null;
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals(elementName))
                return (Element) child;
        }
        return null;
    }

    /**
     * adds an <code>element</code> to the parent
     * 
     * @param parent
     *            the <code>element</code> to with the new
     *            <code>element</code> is attached
     * @param string
     * @return the added element
     */
    public static Element addElement(Node parent, String tagName) {
        return (Element) parent.appendChild(parent.getOwnerDocument().createElement(tagName));
    }

    /**
     * add a text-element to the given parent-element
     * 
     * @param parent
     * @param text
     */
    public static void addText(Element parent, String text) {
        parent.appendChild(parent.getOwnerDocument().createTextNode(text));
    }

    /**
     * @param lyric
     * @param string
     * @return
     */
    public static Element[] getChildElements(Element parent, String childName) {
        NodeList children = parent.getChildNodes();
        ArrayList elements = new ArrayList();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(childName)) {
                elements.add(child);
            }
        }
        Element[] elemArr = new Element[elements.size()];
        for (int i = 0; i < elemArr.length; i++) {
            elemArr[i] = (Element) elements.get(i);
        }
        return elemArr;
    }

    /**
     * Reads the text of a TextElement, ignoring comments and sub-elements.
     * 
     * @param defVerse
     *            The element to read from.
     * @return The text within the element.
     */
    public static String getText(Element defVerse) {
        NodeList nodeList = defVerse.getChildNodes();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                sb.append(node.getNodeValue());
            }
        }
        return sb.toString();
    }

    /**
     * gets the child-elements of a given parent-element
     * 
     * @param parent
     * @return children
     */
    public static Element[] getChildElements(Element parent) {
        NodeList children = parent.getChildNodes();
        ArrayList elements = new ArrayList();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                elements.add(child);
            }
        }
        Element[] elemArr = new Element[elements.size()];
        for (int i = 0; i < elemArr.length; i++) {
            elemArr[i] = (Element) elements.get(i);
        }
        return elemArr;
    }

    /**
     * append
     * 
     * @param parent
     * @param childDocument
     */
    public static void addDocument(Element parent, Document childDocument) {
        Document parDoc = parent.getOwnerDocument();
        Node child = childDocument.getDocumentElement();
        child = parDoc.importNode(child, false);
        parent.appendChild(child);
        //TODO check if further handling is necessary, e.g. namespace check,
        // namespace prefixes etc.
    }

    /**
     * @param smr
     * @param smrFile
     */
    public static void writeXML(Document doc, File file) {
        String xml = XMLHelper.asXML(doc);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(xml);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}