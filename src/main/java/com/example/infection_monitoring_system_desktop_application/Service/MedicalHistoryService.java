package com.example.infection_monitoring_system_desktop_application.Service;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import com.example.infection_monitoring_system_desktop_application.Model.MedicalHistory;
import com.example.infection_monitoring_system_desktop_application.Model.MedicalHistoryDAO;

import java.util.concurrent.CompletableFuture;

public class MedicalHistoryService {

    private final MedicalHistoryDAO dao = new MedicalHistoryDAO();

    public CompletableFuture<Void> addMedicalHistoryAsync(MedicalHistory medicalHistory) {
        return CompletableFuture.runAsync(() -> dao.addMedicalHistory(medicalHistory),
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Void> updateMedicalHistoryAsync(MedicalHistory medicalHistory) {
        return CompletableFuture.runAsync(() -> dao.updateMedicalHistory(medicalHistory),
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<MedicalHistory> getMedicalHistoryByUserIdAsync(int userId) {
        return CompletableFuture.supplyAsync(() -> dao.getMedicalHistoryByUserId(userId),
                ConcurrencyManager.getExecutor());
    }
}
