package com.example.infection_monitoring_system_desktop_application.Util.Exceptions;

public class SceneLoadException extends RuntimeException {

    public SceneLoadException(String message) {
        super(message);
    }

    public SceneLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
