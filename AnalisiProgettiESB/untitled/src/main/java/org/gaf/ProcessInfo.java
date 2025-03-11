package org.gaf;

import lombok.Data;

import java.util.List;

@Data
public class ProcessInfo {

    private String processName;
    private List<String> calledProcesses;
    private Integer lines;
    private Integer characters;
    private Integer activitiesNumber;
    private Integer transitionNumber;
}
