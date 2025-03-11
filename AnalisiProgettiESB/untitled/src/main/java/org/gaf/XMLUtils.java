package org.gaf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XMLUtils {

    public static String getTagValue(Document doc, String tagName) {
        NodeList childNodes = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equals(tagName)) {
                return "/" + childNode.getTextContent();
            }
        }
        return "";
    }

    public static Document parseXML(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> findAttributeValues(Document doc, Node node, String[] nodePath, int depth, String attributeName, String nodeValue, String attributeValue) {
        List<String> values = new ArrayList<>();

        if (depth < nodePath.length && node.getNodeName().equals(nodePath[depth])) {
            if (depth == nodePath.length - 1) {
                if (node instanceof Element element) {
                    if (attributeName.equals("any")) {
                        values.add(element.getTextContent());
                    } else if (nodeValue.equals("any")) {
                        checkAttributes(doc, attributeName, attributeValue, element, values);
                    } else {
                        checkNode(doc, nodeValue, element, values);
                    }
                }
            } else {
                NodeList nodeList = node.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node childNode = nodeList.item(i);
                    values.addAll(findAttributeValues(doc, childNode, nodePath, depth + 1, attributeName, nodeValue, attributeValue));
                }
            }
        } else {
            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node childNode = nodeList.item(i);
                values.addAll(findAttributeValues(doc, childNode, nodePath, depth, attributeName, nodeValue, attributeValue));
            }
        }
        return values;
    }

    private static void checkAttributes(Document doc, String attributeName, String attributeValue, Element element, List<String> values) {
        String value = element.getAttribute(attributeName);
        if (attributeValue.equals("any")) {
            if (!value.isEmpty()) {
                values.add(value);
            }
        } else {
            if (attributeValue.equals(value)) {
                values.add(getTagValue(doc,"pd:name"));
            }
        }
    }

    private static void checkNode(Document doc, String nodeValue, Element element, List<String> values) {
        String value = element.getTextContent();
        if (nodeValue.equals(value)) {
            values.add(getTagValue(doc,"pd:name"));
        }
    }

    public static int countNodes(Document doc, String nodeName) {

        Node root = doc.getDocumentElement();
        return countNodesRecursive(root, nodeName);
    }

    public static int countValueNodes(Document doc, String value) {

        Node root = doc.getDocumentElement();
        return countAttrNodesRecursive(root, value);
    }

    private static int countNodesRecursive(Node node, String nodeName) {
        int count = 0;

        if (node.getNodeName().equals(nodeName)) {
            count++;
        }

        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            count += countNodesRecursive(children.item(i), nodeName);
        }

        return count;
    }

    private static int countAttrNodesRecursive(Node node, String attrName){
        int count = 0;

        if (node.getNodeType() == Node.ELEMENT_NODE && node.getTextContent().equals(attrName)) {
            count++;
        }

        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            count += countAttrNodesRecursive(children.item(i), attrName);
        }

        return count;
    }

    public static int distinctCalledProcesses(String attrName, Document doc) {


        Set<String> activityDistinct = new HashSet<>();
        NodeList activityList = doc.getElementsByTagName("pd:activity");

        for (int i = 0; i < activityList.getLength(); i++) {
            Element activityElement = (Element) activityList.item(i);
            String type = activityElement.getElementsByTagName("pd:type").item(0).getTextContent();
            if (attrName.equals(type)) {
                String processNameInActivity = activityElement.getElementsByTagName("processName").item(0).getTextContent();
                activityDistinct.add(processNameInActivity);
            }

        }
        return activityDistinct.size();
    }
}
