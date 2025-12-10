package com.example.infection_monitoring_system_desktop_application.Model;

import java.sql.*;
import java.util.Set;

public class CaseDAO {

    private static final Set<String> ALLOWED_SYMPTOMS = Set.of(
            "Fever", "Cough", "Headache", "Fatigue", "ShortnessOfBreath", "LossOfSmell"
    );

    private static final Set<String> ALLOWED_SEVERITY = Set.of(
            "Mild", "Moderate", "Severe"
    );

    public void addCase(Case newCase) {
        if (!ALLOWED_SEVERITY.contains(newCase.getSeverity())) {
            throw new IllegalArgumentException("Invalid severity: " + newCase.getSeverity());
        }

        for (String symptom : newCase.getSymptoms()) {
            if (!ALLOWED_SYMPTOMS.contains(symptom)) {
                throw new IllegalArgumentException("Invalid symptom: " + symptom);
            }
        }

        String sql = "INSERT INTO Cases " +
                "(UserID, DateReported, SymptomsBegan, Symptoms, Severity, ConfirmedExposure) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newCase.getUserID());
            pstmt.setTimestamp(2, Timestamp.valueOf(newCase.getDateReported()));
            pstmt.setObject(3, newCase.getSymptomsBegan() != null ? newCase.getSymptomsBegan() : null);
            pstmt.setString(4, String.join(",", newCase.getSymptoms()));
            pstmt.setString(5, newCase.getSeverity());
            pstmt.setBoolean(6, newCase.isConfirmedExposure());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCasesByUser(int userID) {
        String sql = "DELETE FROM cases WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



//    public Case getCaseByCaseID(int CaseID){
//        String sql = "SELECT * FROM Cases WHERE CaseID = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, CaseID);
//            ResultSet rs = pstmt.executeQuery();
//        }
//
//    }
//
//    public Case getCaseByUserID(int UserID){
//        String sql = "SELECT * FROM Cases WHERE UserID = ?";
//    }

//    public void deleteCase(int CaseID) {
//        String sql = "DELETE FROM Cases WHERE CaseID = ?";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, CaseID);
//            pstmt.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
