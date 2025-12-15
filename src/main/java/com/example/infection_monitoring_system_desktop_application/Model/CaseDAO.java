package com.example.infection_monitoring_system_desktop_application.Model;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.DAOException;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import java.util.*;
import java.sql.*;

public class CaseDAO {

    private static final String INSERT_CASE_SQL = """
        INSERT INTO Cases (UserID, DateReported, SymptomsBegan, Symptoms, Severity, ConfirmedExposure) 
        VALUES (?, ?, ?, ?, ?, ?)
    """;
    private static final String SELECT_ALL_CASES_SQL = """
        SELECT c.*, u.FirstName, u.LastName, u.Email, u.Role, u.DateOfBirth
        FROM Cases c
        JOIN Users u ON c.UserID = u.UserID
    """;

    private static final Set<String> ALLOWED_SYMPTOMS = Set.of(
            "Fever", "Cough", "Headache", "Fatigue", "ShortnessOfBreath", "LossOfSmell"
    );
    private static final Set<String> ALLOWED_SEVERITY = Set.of(
            "Mild", "Moderate", "Severe"
    );

    private final DataSource dataSource;

    public CaseDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addCase(Case newCase) {
        if (!ALLOWED_SEVERITY.contains(newCase.getSeverity())) {
            throw new IllegalArgumentException("Invalid severity: " + newCase.getSeverity());
        }
        for (String symptom : newCase.getSymptoms()) {
            if (!ALLOWED_SYMPTOMS.contains(symptom)) {
                throw new IllegalArgumentException("Invalid symptom: " + symptom);
            }
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_CASE_SQL)) {

            pstmt.setInt(1, newCase.getUserID());
            pstmt.setTimestamp(2, Timestamp.valueOf(newCase.getDateReported()));
            pstmt.setTimestamp(3, newCase.getSymptomsBegan() != null ? Timestamp.valueOf(newCase.getSymptomsBegan()) : null);
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

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_CASES_SQL);
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
}