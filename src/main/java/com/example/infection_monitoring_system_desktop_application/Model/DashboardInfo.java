package com.example.infection_monitoring_system_desktop_application.Model;

import java.time.LocalDate;

public class DashboardInfo {
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private LocalDate lastHistoryUpdate;
    private String existingConditions;
    private String vaccinationStatus;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public LocalDate getLastHistoryUpdate() { return lastHistoryUpdate; }
    public void setLastHistoryUpdate(LocalDate lastHistoryUpdate) { this.lastHistoryUpdate = lastHistoryUpdate; }

    public String getExistingConditions() { return existingConditions; }
    public void setExistingConditions(String existingConditions) { this.existingConditions = existingConditions; }

    public String getVaccinationStatus() { return vaccinationStatus; }
    public void setVaccinationStatus(String vaccinationStatus) { this.vaccinationStatus = vaccinationStatus; }
}
