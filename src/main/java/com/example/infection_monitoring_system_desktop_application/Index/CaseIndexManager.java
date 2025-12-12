package com.example.infection_monitoring_system_desktop_application.Index;

import com.example.infection_monitoring_system_desktop_application.Model.Case;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class CaseIndexManager {

    private final TreeSet<Case> dateSortedSet =
            new TreeSet<>(Comparator.comparing(Case::getDateReported).reversed());

    public void rebuildIndex(List<Case> cases) {
        dateSortedSet.clear();
        dateSortedSet.addAll(cases);
    }

    public void addCase(Case c) {
        dateSortedSet.add(c);
    }

    public List<Case> getCasesSortedByDate() {
        return new ArrayList<>(dateSortedSet);
    }
}
