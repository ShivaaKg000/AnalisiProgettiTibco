package org.gaf;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import static org.gaf.Parser.getCsv;

public class Main {
    public static void main(String[] args) {
        File mainPath = new File("C:\\FMS_PiattaformaOperativa_New\\Fideuram_ExternalServices\\Trunk"); //C:\FMS_PiattaformaOperativa_New\FMS_DataServices

       // global.append(getCsv(mainPath, ".serviceagent", "SOAP","operations.row", "opImpl", "any","any")).append("\n");
        //global.append(getCsv(mainPath, ".process", "REST", "Binding","process", "any","any"));
        //global.append(getCsv(mainPath, ".process", "REST", "ProcessName.xsl:value-of","select", "any","any")).append("\n");
        //global.append(getCsv(mainPath, ".process", "JMS", "pd:starter.pd:type","","com.tibco.plugin.jms.JMSQueueEventSource", "any")).append("\n");
        //global.append(getCsv(mainPath, ".process", "BATCH", "pd:starter.pd:type","","com.tibco.plugin.timer.TimerEventSource", "any")).append("\n");
       //global.append(getCsv(mainPath, ".process", "SUBPROCESS", "config.processNameXPath","any","any", "any")).append("\n");

        System.out.println("PROCESS PATH,CLUSTER,CHARS,LINES,ACTIVITIES,TRANSITIONS,SUBPROCESS,DISCT_SUBPROCESS,MAPPERS,SOAP,JDBC,SP,HTTP_REC,HTTP_REQ,INV_REST,JMS,NULL_ASSIGN");
        Parser.getProcessMetrics(mainPath);
    }
}