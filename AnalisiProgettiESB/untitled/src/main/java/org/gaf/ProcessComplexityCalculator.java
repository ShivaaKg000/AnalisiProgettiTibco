package org.gaf;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ProcessComplexityCalculator {
    private static final int MAX_COMPLEXITY = 10000;

    public static int calculateComplexity(File file) throws Exception
    {
        Document doc = loadXMLDocument(file);
        int complexity = 0;

        // Conta le attività con pesi dinamici
        complexity += countWeightedActivities(doc);

        // Conta gli elementi logici (condizioni, cicli)
        complexity += countNodes(doc, "pd:condition") * 0.1;
        complexity += countNodes(doc, "pd:loop") * 0.1;

        // Conta gli elementi logici specifici (if, then, when, otherwise)
        complexity += (int) (countLogicalElements(doc) * 0.01); // Assegna un peso inferiore agli elementi logici

        // Conta la dimensione delle strutture (variabili)
        complexity += calculateStructureSize(doc) / 1000; // Riduce l'impatto della dimensione delle strutture

        // Considera la complessità dei dati (strutturati vs. non strutturati)
        complexity += calculateDataComplexity(doc);

        return complexity;
    }

    public static Document loadXMLDocument(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }

    public static int countWeightedActivities(Document doc) {
        int complexity = 0;
        NodeList activities = doc.getElementsByTagName("pd:activity");

        for (int i = 0; i < activities.getLength(); i++) {
            Element activity = (Element) activities.item(i);
            String type = activity.getElementsByTagName("pd:type").item(0).getTextContent();
            int paletteLength = activities.getLength();

            switch (type) {
                case "com.tibco.plugin.soap.SOAPInvokeActivity":
                case "com.tibco.plugin.rest.RESTInvokeActivity":
                //case "com.tibco.plugin.general.LogActivity":
                //case "com.tibco.plugin.general.TimerActivity":
                    complexity += 3 * paletteLength / 100; // Peso dinamico per attività SOAP e REST
                    break;
                case "com.tibco.plugin.mapper.MapperActivity":
                case "com.tibco.pe.core.AssignActivity":
                    complexity += 4 * paletteLength / 100; // Peso dinamico per attività Mapper, Log e Timer
                    break;
                case "com.tibco.plugin.jdbc.JDBCQueryActivity":
                case "com.tibco.plugin.jdbc.JDBCUpdateActivity":
                case "com.tibco.plugin.file.ReadFileActivity":
                case "com.tibco.plugin.file.WriteFileActivity":
                    complexity += 3 * paletteLength / 100; // Peso dinamico per attività JDBC
                    break;

                case "com.tibco.plugin.file.AppendToFileActivity":
                case "com.tibco.plugin.jdbc.JDBCConnectionActivity":
                    complexity += 2 * paletteLength / 100; // Peso dinamico per attività File
                    break;
                case "com.tibco.plugin.ftp.FTPGetActivity":
                case "com.tibco.plugin.ftp.FTPPutActivity":
                case "com.tibco.plugin.ftp.FTPDeleteActivity":
                    complexity += 3 * paletteLength / 100; // Peso dinamico per attività FTP
                    break;
                case "com.tibco.plugin.http.HTTPRequestActivity":
                case "com.tibco.plugin.http.HTTPReceiveActivity":
                case "com.tibco.plugin.http.HTTPReplyActivity":
                    complexity += 3 * paletteLength / 100; // Peso dinamico per attività HTTP
                    break;
                case "com.tibco.plugin.jms.JMSQueueSendActivity":
                case "com.tibco.plugin.jms.JMSQueueReceiveActivity":
                case "com.tibco.plugin.jms.JMSQueueRequestReplyActivity":
                    complexity += 3 * paletteLength / 100; // Peso dinamico per attività JMS
                    break;
                case "com.tibco.plugin.java.JavaMethodActivity":
                case "com.tibco.plugin.java.JavaProcessStarterActivity":
                    complexity += 2 * paletteLength / 100; // Peso dinamico per attività Java
                    break;
                case "com.tibco.plugin.xml.ParseXMLActivity":
                case "com.tibco.plugin.xml.RenderXMLActivity":
                    complexity += 4 * paletteLength / 100; // Peso dinamico per attività XML
                    break;
                case "com.tibco.plugin.loop.LoopActivity":
                case "com.tibco.plugin.loop.RepeatActivity":
                    complexity += 2 * paletteLength / 100; // Peso dinamico per attività Loop
                    break;
                case "com.tibco.plugin.transaction.TransactionBeginActivity":
                case "com.tibco.plugin.transaction.TransactionCommitActivity":
                case "com.tibco.plugin.transaction.TransactionRollbackActivity":
                    complexity += 3 * paletteLength / 100; // Peso dinamico per attività Transaction
                    break;
                case "com.tibco.plugin.error.ErrorHandlerActivity":
                case "com.tibco.plugin.error.ErrorCatchActivity":
                    complexity += 2 * paletteLength / 100; // Peso dinamico per attività di gestione degli errori
                    break;
                case "com.tibco.pe.core.CallProcessActivity":
                    complexity += 3 * paletteLength / 100; // Peso ridotto per attività Call Process
                    break;
                default:
                    complexity += 1 * paletteLength / 100; // Peso predefinito per altre attività
                    break;
            }
        }

        return complexity;
    }

    public static int calculateStructureSize(Document doc) {
        int size = 0;
        NodeList elements = doc.getElementsByTagName("*");

        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            size += element.getTextContent().length();
        }

        return size;
    }

    public static int countNodes(Document doc, String nodeName) {
        NodeList nodes = doc.getElementsByTagName(nodeName);
        return nodes.getLength();
    }

    public static int countLogicalElements(Document doc) {
        int count = 0;
        count += countNodes(doc, "xsl:if");
        count += countNodes(doc, "xsl:choose");
        count += countNodes(doc, "xsl:when");
        count += countNodes(doc, "xsl:otherwise");
        count += countNodes(doc, "xsl:for-each");

        return count;
    }

    public static int calculateDataComplexity(Document doc) {
        int complexity = 0;
        NodeList elements = doc.getElementsByTagName("*");

        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            String textContent = element.getTextContent();

            if (textContent.matches(".*\\{.*\\}.*")) { // Esempio di pattern per dati non strutturati
                complexity += 5; // Peso maggiore per dati non strutturati
            } else {
                complexity += 1; // Peso minore per dati strutturati
            }
        }

        return complexity;
    }

    public static int normalizeComplexity(int complexity) {
        return Math.min(MAX_COMPLEXITY, complexity);
    }
}