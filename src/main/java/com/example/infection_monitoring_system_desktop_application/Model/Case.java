package com.example.infection_monitoring_system_desktop_application.Model;

import java.time.LocalDateTime;
import java.util.Set;

public class Case {
    private int caseID;
    private int userID;
    private LocalDateTime dateReported;
    private LocalDateTime symptomsBegan;
    private Set<String> symptoms;
    private String severity;
    private boolean confirmedExposure;

    public Case(int userID, LocalDateTime dateReported, LocalDateTime symptomsBegan,
                Set<String> symptoms, String severity, boolean confirmedExposure) {
        this.userID = userID;
        this.dateReported = dateReported;
        this.symptomsBegan = symptomsBegan;
        this.symptoms = symptoms;
        this.severity = severity;
        this.confirmedExposure = confirmedExposure;
    }

    public int getCaseID() { return caseID; }
    public void setCaseID(int caseID) { this.caseID = caseID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public LocalDateTime getDateReported() { return dateReported; }
    public void setDateReported(LocalDateTime dateReported) { this.dateReported = dateReported; }

    public LocalDateTime getSymptomsBegan() { return symptomsBegan; }
    public void setSymptomsBegan(LocalDateTime symptomsBegan) { this.symptomsBegan = symptomsBegan; }

    public Set<String> getSymptoms() { return symptoms; }
    public void setSymptoms(Set<String> symptoms) { this.symptoms = symptoms; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public boolean isConfirmedExposure() { return confirmedExposure; }
    public void setConfirmedExposure(boolean confirmedExposure) { this.confirmedExposure = confirmedExposure; }
}
