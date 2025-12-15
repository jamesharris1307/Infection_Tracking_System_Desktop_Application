package com.example.infection_monitoring_system_desktop_application.Model;

import java.time.LocalDateTime;

public class MedicalHistory {
    private int medicalHistoryID;
    private int userID;
    private boolean longTermConditions;
    private boolean longTermMedications;
    private boolean vaccinationUpToDate;
    private boolean allergies;
    private LocalDateTime lastUpdated;

    public MedicalHistory(int userID, boolean longTermConditions, boolean longTermMedications, boolean vaccinationUpToDate, boolean allergies, LocalDateTime lastUpdated) {
        this.userID = userID;
        this.longTermConditions = longTermConditions;
        this.longTermMedications = longTermMedications;
        this.vaccinationUpToDate = vaccinationUpToDate;
        this.allergies = allergies;
        this.lastUpdated = lastUpdated;
    }

    public int getMedicalHistoryID() { return medicalHistoryID; }
    public void setMedicalHistoryID(int medicalHistoryID) { this.medicalHistoryID = medicalHistoryID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public boolean isLongTermConditions() { return longTermConditions; }
    public void setLongTermConditions(boolean longTermConditions) { this.longTermConditions = longTermConditions; }

    public boolean isLongTermMedications() { return longTermMedications; }
    public void setLongTermMedications(boolean longTermMedications) { this.longTermMedications = longTermMedications; }

    public boolean isVaccinationUpToDate() { return vaccinationUpToDate; }
    public void setVaccinationUpToDate(boolean vaccinationUpToDate) { this.vaccinationUpToDate = vaccinationUpToDate; }

    public boolean isAllergies() { return allergies; }
    public void setAllergies(boolean allergies) { this.allergies = allergies; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

}
