package org.gaf;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import static org.gaf.Parser.getCsv;

public class Main {
    public static void main(String[] args) throws Exception {
        File mainPath = new File("C:\\progettiESB\\esb\\NAG"); //C:\progettiESB\\ESB20

        System.out.println
                ("PROCESS,CLUSTER_PATH,CHARS,LINES,ACTIVITIES,TRANSITIONS,SUBPROCESS,DISCT_SUBPROCESS,MAPPERS,SOAP,JDBC,SP,HTTP_REC,HTTP_REQ,INV_REST,JMS,NULL,ASSIGN,ESTIMATED_HOURS");
        Parser.getProcessMetrics(mainPath);
    }
}