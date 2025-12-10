package com.example.infection_monitoring_system_desktop_application.Model;

import java.sql.*;

public class MedicalHistoryDAO {

    public void addMedicalHistory(MedicalHistory medicalHistory) {

        String sql = "INSERT INTO MedicalHistory " +
                "(UserID, longTermConditions, longTermMedications, vaccinationUpToDate, allergies, lastUpdated) " +
                "VALUES (?, ?, ?, ?, ?, ?)";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, medicalHistory.getUserID());
            pstmt.setBoolean(2, medicalHistory.isLongTermConiditions());
            pstmt.setBoolean(3, medicalHistory.isLongTermMedications());
            pstmt.setBoolean(4, medicalHistory.isVaccinationUpToDate());
            pstmt.setBoolean(5, medicalHistory.isAllergies());
            pstmt.setObject(6, medicalHistory.getLastUpdated());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}