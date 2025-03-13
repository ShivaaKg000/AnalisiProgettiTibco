package org.gaf;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ProcessComplexityCalculatorVA {
    private static final int MAX_COMPLEXITY = 10000; // Complessità massima
    private static final double CONDITION_WEIGHT = 0.25; // Peso per le condizioni
    private static final double LOOP_WEIGHT = 0.25; // Peso per i cicli
    private static final double LOGICAL_ELEMENT_WEIGHT = 0.5; // Peso per gli elementi logici
    private static final int STRUCTURE_SIZE_DIVISOR = 1000; // Divisore per la dimensione delle strutture
    private static final int UNSTRUCTURED_DATA_WEIGHT = 2; // Peso per i dati non strutturati
    private static final int STRUCTURED_DATA_WEIGHT = 1; // Peso per i dati strutturati

    // Metodo principale per calcolare la complessità
    public static int calculateComplexity2(File file) throws Exception {
        Document doc = loadXMLDocument(file); // Carica il documento XML
        int complexity = 0;

        complexity += countWeightedActivities(doc); // Conta le attività con pesi dinamici
        // System.out.println("Conta le attività con pesi dinamici: "+complexity);
        complexity += countNodes(doc, "pd:activity") * CONDITION_WEIGHT; // Conta le condizioni
        //System.out.println("Conta le condizioni: "+complexity);
        complexity += countNodes(doc, "pd:transition") * LOOP_WEIGHT; // Conta i cicli
        //System.out.println("Conta i cicli: "+complexity);
        complexity += countLogicalElements(doc) * LOGICAL_ELEMENT_WEIGHT; // Conta gli elementi logici
        //System.out.println("Conta gli elementi logici: " + complexity);
        //complexity += calculateStructureSize(doc) / STRUCTURE_SIZE_DIVISOR; // Calcola la dimensione delle strutture
        //System.out.println("Calcola la dimensione delle strutture: "+complexity);
        complexity += calculateDataComplexity(doc); // Calcola la complessità dei dati
        //System.out.println("Calcola la complessità dei dati: "+complexity);

        double result = ((double)normalizeComplexity(complexity) / 85) + 3;

        return ((int) result); // Normalizza la complessità
    }

    // Metodo per caricare il documento XML
    public static Document loadXMLDocument(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }

    // Metodo per contare le attività con pesi dinamici
    public static int countWeightedActivities(Document doc) {
        int complexity = 0;
        NodeList activities = doc.getElementsByTagName("pd:activity");

        for (int i = 0; i < activities.getLength(); i++) {
            Element activity = (Element) activities.item(i);
            String type = activity.getElementsByTagName("pd:type").item(0).getTextContent();
            int paletteLength = getPaletteLength(activity); // Ottiene la lunghezza del palette
            complexity += getActivityWeight(type, paletteLength); // Calcola il peso dell'attività
        }

        return complexity;
    }

    // Metodo per ottenere la lunghezza del palette
    private static int getPaletteLength(Element activity) {
        NodeList childNodes = activity.getChildNodes();
        int length = 0;

        for (int i = 0; i < childNodes.getLength(); i++) {
            length += childNodes.item(i).getTextContent().length();
        }

        return length;
    }

    // Metodo per calcolare il peso dell'attività
    private static double getActivityWeight(String type, int paletteLength) {
        double baseWeight;

        switch (type) {
            case "com.tibco.plugin.mapper.MapperActivity":
            case "com.tibco.plugin.xml.ParseXMLActivity":
            case "com.tibco.plugin.xml.RenderXMLActivity":
            case "com.tibco.pe.core.AssignActivity":
                baseWeight = 1.25; // Peso base per queste attività
                break;
            case "com.tibco.plugin.jdbc.JDBCQueryActivity":
            case "com.tibco.plugin.jdbc.JDBCUpdateActivity":
            case "com.tibco.plugin.ftp.FTPGetActivity":
            case "com.tibco.plugin.ftp.FTPPutActivity":
            case "com.tibco.plugin.ftp.FTPDeleteActivity":
            case "com.tibco.plugin.http.HTTPRequestActivity":
            case "com.tibco.plugin.http.HTTPReceiveActivity":
            case "com.tibco.plugin.http.HTTPReplyActivity":
            case "com.tibco.plugin.jms.JMSQueueSendActivity":
            case "com.tibco.plugin.jms.JMSQueueReceiveActivity":
            case "com.tibco.plugin.jms.JMSQueueRequestReplyActivity":
            case "com.tibco.plugin.transaction.TransactionBeginActivity":
            case "com.tibco.plugin.transaction.TransactionCommitActivity":
            case "com.tibco.plugin.transaction.TransactionRollbackActivity":
            case "com.tibco.pe.core.CallProcessActivity":
            case "com.tibco.plugin.file.ReadFileActivity":
            case "com.tibco.plugin.file.WriteFileActivity":
                baseWeight = 1; // Peso base per queste attività
                break;
            case "com.tibco.plugin.soap.SOAPInvokeActivity":
            case "com.tibco.plugin.soap.SOAPSendReceiveActivity":
            case "com.tibco.plugin.rest.RESTInvokeActivity":
            case "com.tibco.plugin.jdbc.JDBCConnectionActivity":
            case "com.tibco.plugin.file.AppendToFileActivity":
            case "com.tibco.plugin.java.JavaMethodActivity":
            case "com.tibco.plugin.java.JavaProcessStarterActivity":
            case "com.tibco.plugin.loop.LoopActivity":
            case "com.tibco.plugin.loop.RepeatActivity":
            case "com.tibco.plugin.error.ErrorHandlerActivity":
            case "com.tibco.plugin.error.ErrorCatchActivity":
                baseWeight = 0.5; // Peso base per queste attività
                break;
            default:
                baseWeight = 0.2; // Peso base per altre attività
                break;
        }

        return baseWeight * paletteLength / 100; // Calcola il peso finale basato sulla lunghezza del palette
    }

    // Metodo per calcolare la dimensione delle strutture
    public static int calculateStructureSize(Document doc) {
        int size = 0;
        NodeList elements = doc.getElementsByTagName("*");

        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            size += element.getTextContent().length();
        }

        return size;
    }

    // Metodo per contare i nodi
    public static int countNodes(Document doc, String nodeName) {
        NodeList nodes = doc.getElementsByTagName(nodeName);
        return nodes.getLength();
    }

    // Metodo per contare gli elementi logici
    public static int countLogicalElements(Document doc) {
        int count = 0;
        count += countNodes(doc, "xsl:if");
        count += countNodes(doc, "xsl:choose");
        count += countNodes(doc, "xsl:when");
        count += countNodes(doc, "xsl:otherwise");
        count += countNodes(doc, "xsl:for-each");

        return count;
    }

    // Metodo per calcolare la complessità dei dati
    public static int calculateDataComplexity(Document doc) {
        int complexity = 0;
        NodeList elements = doc.getElementsByTagName("*");

        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            String textContent = element.getTextContent();

            if (textContent.matches(".*\\{.*\\}.*")) {
                complexity += UNSTRUCTURED_DATA_WEIGHT; // Peso maggiore per dati non strutturati
            } else {
                complexity += STRUCTURED_DATA_WEIGHT; // Peso minore per dati strutturati
            }
        }

        return complexity;
    }

    // Metodo per normalizzare la complessità
    public static int normalizeComplexity(int complexity) {
        return Math.min(MAX_COMPLEXITY, complexity);
    }
}