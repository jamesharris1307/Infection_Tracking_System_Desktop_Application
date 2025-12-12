package com.example.infection_monitoring_system_desktop_application.Service;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import com.example.infection_monitoring_system_desktop_application.Model.Case;
import com.example.infection_monitoring_system_desktop_application.Model.CaseDAO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CaseService {

    private final CaseDAO caseDAO = new CaseDAO();

    public CompletableFuture<Void> addCaseAsync(Case c) {
        return CompletableFuture.runAsync(() -> caseDAO.addCase(c),
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<List<Case>> getAllCasesAsync() {
        return CompletableFuture.supplyAsync(caseDAO::getAllCases,
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Void> deleteCasesByUserAsync(int userID) {
        return CompletableFuture.runAsync(() -> caseDAO.deleteCasesByUser(userID),
                ConcurrencyManager.getExecutor());
    }
}
