package org.gaf;

import org.w3c.dom.Document;

import java.io.*;
import java.util.*;

import static org.gaf.ProcessComplexityCalculator.calculateComplexity;
import static org.gaf.ProcessComplexityCalculatorVA.calculateComplexity2;
import static org.gaf.TextUtils.*;
import static org.gaf.XMLUtils.*;

public class Parser {

    public static String getCsv(File mainPath, String ext, String opType, String parent, String attr, String nodeValue, String attrValue) {
        StringBuilder sb = new StringBuilder();
        Map<String, ProcessInfo> processInfoMap = scanDirectory(mainPath, ext, parent, attr, nodeValue, attrValue);
        Map<String, Map<String, Integer>> transformedMap = transformMap(processInfoMap);

        transformedMap.forEach((fileName, values) -> values.forEach((s, count) -> {
            s = s.replaceAll("'", "");
            if (attrValue.equals("any")) {
                if (s.contains("/Processes")) {
                    sb.append(s).append(",").append(opType).append(",").append(fileName).append(",").append(count).append("\n");
                } else {
                    System.out.println("Skipping " + s);
                }
            } else {
                sb.append(s).append(",").append(opType).append(",").append(fileName).append(",").append(count).append("\n");
            }
        }));
        return sb.toString();
    }

    public static Map<String, ProcessInfo> scanDirectory(File directory, String extension, String nodePath, String attributeName, String nodeValue, String attrValue) {
        Map<String, ProcessInfo> results = new HashMap<>();

        FilenameFilter filter = (dir, name) -> name.endsWith(extension);
        File[] files = directory.listFiles(filter);

        if (files != null) {
            for (File file : files) {
                Document doc = parseXML(file);
                if (doc != null) {
                    List<String> values = findAttributeValues(doc, doc.getDocumentElement(), nodePath.split("\\."), 0, attributeName, nodeValue, attrValue);
                    if (!values.isEmpty()) {
                        ProcessInfo processInfo = new ProcessInfo();
                        processInfo.setCalledProcesses(values);
                        String pdName = getTagValue(doc, "pd:name");
                        processInfo.setProcessName(pdName);
                        results.put(pdName, processInfo);
                    }
                }
            }
        }

        // Ottieni la lista delle sottocartelle
        File[] subDirs = directory.listFiles(File::isDirectory);

        if (subDirs != null) {
            for (File subDir : subDirs) {
                results.putAll(scanDirectory(subDir, extension, nodePath, attributeName, nodeValue, attrValue)); // Ricorsione per le sottocartelle
            }
        }

        return results;
    }


    public static Map<String, Map<String, Integer>> transformMap(Map<String, ProcessInfo> matchingAttributes) {
        Map<String, Map<String, Integer>> result = new HashMap<>();

        for (Map.Entry<String, ProcessInfo> entry : matchingAttributes.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue().getCalledProcesses();

            Map<String, Integer> occurrencesMap = new HashMap<>();
            for (String value : values) {
                occurrencesMap.put(value, occurrencesMap.getOrDefault(value, 0) + 1);
            }

            result.put(key, occurrencesMap);
        }

        return result;
    }

    public static void getProcessMetrics(File directory) throws Exception {

        FilenameFilter filter = (dir, name) -> name.endsWith(".process");
        File[] files = directory.listFiles(filter);

        if (files != null) {
            for (File file : files) {
                Document doc = parseXML(file);
                if (doc != null) {
                    StringBuilder global = new StringBuilder();
                    String cluster = file.getAbsolutePath().split("\\\\")[3]+ "\\" +file.getAbsolutePath().split("\\\\")[4];
                    String processName = getTagValue(doc, "pd:name");
                    // Sostituisci gli slash da / a \
                    processName = processName.replace('/', '\\');
                    // Elimina il primo carattere
                    if (processName.length() > 0) {
                        processName = processName.substring(1);
                    }
                    Integer lines = countLines(file);
                    Integer chars = countCharsExcludingSpaces(file.getAbsolutePath());

                    int starter = countNodes(doc,"pd:startName");
                    int end = countNodes(doc,"pd:endName");
                    Integer activities = countNodes(doc,"pd:activity") + starter + end;



                    Integer calledProcesses = countValueNodes(doc,"com.tibco.pe.core.CallProcessActivity");
                    Integer DistinctCalledProcesses = distinctCalledProcesses("com.tibco.pe.core.CallProcessActivity", doc);
                    Integer mappers = countValueNodes(doc,"com.tibco.plugin.mapper.MapperActivity");
                    Integer soap = countValueNodes(doc, "com.tibco.plugin.soap.SOAPSendReceiveActivity");
                    Integer sqlDirect = countValueNodes(doc, "com.tibco.plugin.jdbc.JDBCGeneralActivity");
                    Integer sqlUpdate = countValueNodes(doc, "com.tibco.plugin.jdbc.JDBCUpdateActivity");
                    Integer jdbcQuery = countValueNodes(doc, "com.tibco.plugin.jdbc.JDBCQueryActivity");
                    Integer sp = countValueNodes(doc, "com.tibco.plugin.jdbc.JDBCCallActivity");
                    Integer httpReceiver = countValueNodes(doc, "com.tibco.plugin.http.HTTPEventSource");
                    Integer httpRequest = countValueNodes(doc, "com.tibco.plugin.http.client.HttpRequestActivity");
                    Integer invoke = countValueNodes(doc, "com.tibco.plugin.json.activities.RestActivity");
                    Integer jmsReceiver = countValueNodes(doc, "com.tibco.plugin.jms.JMSQueueEventSource");
                    Integer jmsSender = countValueNodes(doc, "com.tibco.plugin.jms.JMSQueueSendActivity");
                    Integer nullActivity = countValueNodes(doc, "com.tibco.plugin.timer.NullActivity");
                    Integer assignActivity = countValueNodes(doc, "com.tibco.pe.core.AssignActivity");

                    Integer transitions = countNodes(doc,"pd:transition");
                    global.append(processName).append(",")
                            .append(cluster).append(",")
                            .append(chars).append(",")
                            .append(lines).append(",")
                            .append(activities).append(",")
                            .append(transitions).append(",")
                            .append(calledProcesses).append(",")
                            .append(DistinctCalledProcesses).append(",")
                            .append(mappers).append(",")
                            .append(soap).append(",")
                            .append(sqlDirect + sqlUpdate + jdbcQuery).append(",")
                            .append(sp).append(",")
                            .append(httpReceiver).append(",")
                            .append(httpRequest).append(",")
                            .append(invoke).append(",")
                            .append(jmsReceiver + jmsSender).append(",")
                            .append(nullActivity).append(",")
                            .append(assignActivity).append(",")
                            .append(calculateComplexity2(file));
                    System.out.println(global);
                }
            }
        }
        File[] subDirs = directory.listFiles(File::isDirectory);

        if (subDirs != null) {
            for (File subDir : subDirs) {
                getProcessMetrics(subDir);
            }
        }
    }
}

