package org.gaf;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import static org.gaf.Parser.getCsv;

public class Main {
    public static void main(String[] args) throws Exception {
        File mainPath = new File("C:\\FMS_PiattaformaOperativa_New\\FMS_DataServices\\Trunk"); //C:\progettiESB\esbC:\FMS_PiattaformaOperativa_New\FMS_DataServices

        System.out.println
                ("PROCESS,CLUSTER PATH,CHARS,LINES,ACTIVITIES,TRANSITIONS,SUBPROCESS,DISCT_SUBPROCESS,MAPPERS,SOAP,JDBC,SP,HTTP_REC,HTTP_REQ,INV_REST,JMS,NULL_ASSIGN,COMPLEXITY");
        Parser.getProcessMetrics(mainPath);
    }
}