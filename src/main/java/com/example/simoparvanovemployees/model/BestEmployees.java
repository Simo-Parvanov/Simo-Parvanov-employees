package com.example.simoparvanovemployees.model;

import java.time.LocalDate;

public class BestEmployees {
    private String empIdOne;
    private String empIdTwo;
    private String projectId;
    private long allDays ;

    public BestEmployees() {
    }

    public BestEmployees(String empIdOne, String empIdTwo, String projectId, long allDays) {
        this.empIdOne = empIdOne;
        this.empIdTwo = empIdTwo;
        this.projectId = projectId;
        this.allDays = allDays;
    }

    public String getEmpIdOne() {
        return empIdOne;
    }

    public void setEmpIdOne(String empIdOne) {
        this.empIdOne = empIdOne;
    }

    public String getEmpIdTwo() {
        return empIdTwo;
    }

    public void setEmpIdTwo(String empIdTwo) {
        this.empIdTwo = empIdTwo;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public long getAllDays() {
        return allDays;
    }

    public void setAllDays(long allDays) {
        this.allDays = allDays;
    }
}
