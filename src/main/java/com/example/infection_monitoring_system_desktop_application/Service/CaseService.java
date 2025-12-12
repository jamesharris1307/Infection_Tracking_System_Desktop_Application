package com.example.infection_monitoring_system_desktop_application.Service;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import com.example.infection_monitoring_system_desktop_application.Model.Case;
import com.example.infection_monitoring_system_desktop_application.Model.CaseDAO;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Comparator;

public class CaseService {

    private final CaseDAO caseDAO = new CaseDAO();

    private final TreeSet<Case> dateSortedSet = new TreeSet<>(
            Comparator.comparing(Case::getDateReported).reversed()
    );

    public CompletableFuture<Void> addCaseAsync(Case c) {
        return CompletableFuture.runAsync(() -> {
            caseDAO.addCase(c);
            dateSortedSet.add(c);
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<List<Case>> getAllCasesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<Case> cases = caseDAO.getAllCases();
            dateSortedSet.clear();
            dateSortedSet.addAll(cases);
            return new ArrayList<>(dateSortedSet);
        }, ConcurrencyManager.getExecutor());
    }
}
