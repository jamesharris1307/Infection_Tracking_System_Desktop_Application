package com.example.infection_monitoring_system_desktop_application.Unit.Manager;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyManagerTest {

    @Test
    void constructor_ShouldBePrivate() {
        Constructor<ConcurrencyManager> constructor = assertDoesNotThrow(() ->
                ConcurrencyManager.class.getDeclaredConstructor()
        );

        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
                "The ConcurrencyManager constructor must be private to enforce static utility usage.");
    }

    @Test
    void getExecutor_ShouldReturnAnActiveExecutorService() throws InterruptedException {
        ExecutorService executor = ConcurrencyManager.getExecutor();
        AtomicBoolean taskExecuted = new AtomicBoolean(false);

        assertNotNull(executor, "The executor service must be initialized.");
        assertFalse(executor.isShutdown(), "The executor service should be active upon request.");

        executor.submit(() -> {
            taskExecuted.set(true);
        });

        Thread.sleep(100);

        assertTrue(taskExecuted.get(), "The submitted task must be executed by the pool.");
    }
}