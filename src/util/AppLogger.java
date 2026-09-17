package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * Minimal logging utility. Appends timestamped entries to logs/app.log
 * so every issue/return/fine transaction is auditable (non-functional
 * requirement: logging/monitoring).
 */
public class AppLogger {
    private static final String LOG_FILE = "logs/app.log";

    private AppLogger() { }

    public static void log(String level, String message) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            pw.printf("[%s] [%s] %s%n", LocalDateTime.now(), level, message);
        } catch (IOException e) {
            // Fall back to console if log file can't be written — never let logging crash the app.
            System.err.println("Logging failed: " + e.getMessage());
        }
    }

    public static void info(String message) { log("INFO", message); }
    public static void error(String message) { log("ERROR", message); }
    public static void warn(String message) { log("WARN", message); }
}
