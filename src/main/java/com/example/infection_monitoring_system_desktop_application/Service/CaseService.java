package com.example.infection_monitoring_system_desktop_application.Service;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import com.example.infection_monitoring_system_desktop_application.Index.CaseIndexManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Model.CaseDAO;
import com.example.infection_monitoring_system_desktop_application.Model.Case;
import java.util.concurrent.CompletableFuture;
import java.util.List;

public class CaseService {

    private final CaseDAO caseDAO;
    private final CaseIndexManager indexManager = new CaseIndexManager();

    public CaseService(CaseDAO caseDAO) {
        this.caseDAO = caseDAO;
    }

    public CompletableFuture<Void> addCaseAsync(Case c) {

        if (c == null) {
            throw ExceptionFactory.validationError("Cannot add a null case object.");
        }
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