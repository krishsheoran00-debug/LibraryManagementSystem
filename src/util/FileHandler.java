package util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic CSV read/write helper shared by all DAO classes.
 * Keeps persistence logic in one place (maintainability requirement).
 */
public class FileHandler {

    public static List<String> readLines(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        Path p = Paths.get(path);
        if (!Files.exists(p)) {
            Files.createDirectories(p.getParent() == null ? Paths.get(".") : p.getParent());
            Files.createFile(p);
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    public static void writeLines(String path, List<String> lines) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path, false))) {
            for (String line : lines) {
                writer.println(line);
            }
        }
    }

    public static void appendLine(String path, String line) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path, true))) {
            writer.println(line);
        }
    }
}
