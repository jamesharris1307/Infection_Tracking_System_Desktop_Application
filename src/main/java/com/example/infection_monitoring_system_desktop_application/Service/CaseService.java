package com.example.infection_monitoring_system_desktop_application.Service;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import com.example.infection_monitoring_system_desktop_application.Index.CaseIndexManager;
import com.example.infection_monitoring_system_desktop_application.Model.CaseDAO;
import com.example.infection_monitoring_system_desktop_application.Model.Case;
import java.util.concurrent.CompletableFuture;
import java.util.List;

public class CaseService {

    private final CaseDAO caseDAO = new CaseDAO();
    private final CaseIndexManager indexManager = new CaseIndexManager();

    public CompletableFuture<Void> addCaseAsync(Case c) {
        return CompletableFuture.runAsync(() -> {
            caseDAO.addCase(c);
            indexManager.addCase(c);
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<List<Case>> getAllCasesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<Case> cases = caseDAO.getAllCases();
            indexManager.rebuildIndex(cases);
            return indexManager.getCasesSortedByDate();
        }, ConcurrencyManager.getExecutor());
    }
}