package com.example.infection_monitoring_system_desktop_application.Service;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import com.example.infection_monitoring_system_desktop_application.DataAccessObject.MedicalHistoryDAO;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Model.MedicalHistory;

import java.util.concurrent.CompletableFuture;

public class MedicalHistoryService {

    private final MedicalHistoryDAO dao;

    public MedicalHistoryService(MedicalHistoryDAO dao) {
        this.dao = dao;
        if (this.dao == null) {
            throw new IllegalArgumentException("MedicalHistoryDAO must be provided to MedicalHistoryService.");
        }
    }

    public CompletableFuture<Void> saveOrUpdateHistoryAsync(MedicalHistory medicalHistory) {
        if (medicalHistory == null) {
            throw ExceptionFactory.validationError("Cannot save a null medical history object.");
        }

        return CompletableFuture.runAsync(() -> {
            MedicalHistory existingHistory = dao.getMedicalHistoryByUserId(medicalHistory.getUserID());

            if (existingHistory == null) {
                dao.addMedicalHistory(medicalHistory);
            } else {
                dao.updateMedicalHistory(medicalHistory);
            }
        }, ConcurrencyManager.getExecutor());
    }
}