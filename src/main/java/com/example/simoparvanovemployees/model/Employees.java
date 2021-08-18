package com.example.simoparvanovemployees.model;

import java.time.LocalDate;

public class Employees {
    private String empId;
    private String projectId;
    private LocalDate startDate;
    private LocalDate endDate;

    public Employees(String empId, String projectId, LocalDate startDate, LocalDate endDate) {
        this.empId = empId;
        this.projectId = projectId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
