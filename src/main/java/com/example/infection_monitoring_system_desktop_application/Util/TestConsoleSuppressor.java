//package com.example.infection_monitoring_system_desktop_application.Util;
//
//import java.io.ByteArrayOutputStream;
//import java.io.PrintStream;
//
//public class TestConsoleSuppressor {
//
//    private final PrintStream originalErr = System.err;
//    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
//
//    public void suppressErrors() {
//        System.setErr(new PrintStream(errContent));
//    }
//
//    public void restoreErrors() {
//        System.setErr(originalErr);
//    }
//
//    public String getSuppressedErrors() {
//        return errContent.toString();
//    }
//}