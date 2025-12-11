package com.example.infection_monitoring_system_desktop_application.Manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ConcurrencyManager {

    private static final ExecutorService EXECUTOR =
            Executors.newFixedThreadPool(5);

    private ConcurrencyManager() {}

    public static ExecutorService getExecutor() {
        return EXECUTOR;
    }
}
