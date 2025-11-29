package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class GameLogger {

    public enum LogLevel {
        INFO,
        WARNING,
        ERROR,
        DEBUG
    }

    private static final String LOG_FILE = "game.log";
    private static boolean consoleEnabled = true;

    private static boolean fileEnabled = true;

    public static void log(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().toString();
        String formatted = String.format("[%s] [%s] %s",
                timestamp,
                level.name(),
                message
        );

        if (consoleEnabled) {
            System.out.println(formatted);
        }

        if (fileEnabled) {
            writeToFile(formatted);
        }
    }

    public static void info(String message) {
        log(LogLevel.INFO, message);
    }

    public static void warn(String message) {
        log(LogLevel.WARNING, message);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public static void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    private static void writeToFile(String text) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(text);
        } catch (IOException e) {
            System.err.println("[LOGGER ERROR] Gagal menulis ke file log.");
        }
    }

    public static void enableConsole(boolean enabled) {
        consoleEnabled = enabled;
    }

    public static void enableFile(boolean enabled) {
        fileEnabled = enabled;
    }
}
