package com.example.infection_monitoring_system_desktop_application.Model;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.DAOException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
            throw new DAOException("Failed to add case for userID: " + newCase.getUserID(), e);
        }
    }

    public List<Case> getAllCases() {
        List<Case> list = new ArrayList<>();
        String sql = """
        SELECT c.*, u.FirstName, u.LastName, u.Email, u.Role, u.DateOfBirth
        FROM Cases c
        JOIN Users u ON c.UserID = u.UserID
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int caseID = rs.getInt("CaseID");
                int userID = rs.getInt("UserID");

                LocalDateTime dateReported = rs.getTimestamp("DateReported").toLocalDateTime();

                Timestamp ts = rs.getTimestamp("SymptomsBegan");
                LocalDateTime symptomsBegan = ts != null ? ts.toLocalDateTime() : null;

                Set<String> symptoms = new HashSet<>(Arrays.asList(rs.getString("Symptoms").split(",")));

                String severity = rs.getString("Severity");
                boolean confirmedExposure = rs.getBoolean("ConfirmedExposure");

                Case c = new Case(userID, dateReported, symptomsBegan, symptoms, severity, confirmedExposure);
                c.setCaseID(caseID);

                User user = new GeneralPublicUser();
                user.setUserId(userID);
                user.setFirstName(rs.getString("FirstName"));
                user.setLastName(rs.getString("LastName"));

                c.setUser(user);

                list.add(c);
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to fetch all cases", e);
        }
        return list;
    }

    public void deleteCasesByUser(int userID) {
        String sql = "DELETE FROM cases WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to delete cases for userID: " + userID, e);
        }
    }
}
