package org.loudsheep.psio_project.backend.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.loudsheep.psio_project.backend.trading.TradingFormula;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SaveFormulaService {
    private static String createUserDataDirectory(String subDirName) throws IOException {
        // Get the project directory
        String projectDir = System.getProperty("user.dir");

        // Create the full path for the subdirectory
        File directory = new File(projectDir, subDirName);

        // Check if the directory exists, if not create it
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + directory.getAbsolutePath());
        }

        return directory.getAbsolutePath();
    }

    private static void saveToFile(String directoryPath, String fileName, String content) throws IOException {
        // Create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create the file object
        File file = new File(directory, fileName);

        // Write content to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    private static String readFromFile(String directoryPath, String fileName) throws IOException {
        // Create the file object
        File file = new File(directoryPath, fileName);

        // Check if the file exists
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }

        // Read the content of the file
        return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
    }

    private static boolean fileExists(String directoryPath, String fileName) {
        File file = new File(directoryPath, fileName);
        return file.exists();
    }

    private static List<Map<String, Object>> removeSavedWithName(List<Map<String, Object>> saved, String name) {
        for (Map<String, Object> i: saved) {
            if (Objects.equals(i.get("name"), name)) {
                System.out.println("SAME NAME " + i);
                saved.remove(i);
                return saved;
            }
        }
        return saved;
    }

    public static List<Map<String, Object>> getSavedTradingFormulas() {
        try {
            String path = createUserDataDirectory("methods");

            if (!fileExists(path, "formulas.json")) return new ArrayList<>();

            String data = readFromFile(path, "formulas.json");

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();

            List<Map<String, Object>> parsed = gson.fromJson(data, listType);
            if (parsed == null) return new ArrayList<>();
            return parsed;
        } catch (IOException _) {
        }
        return new ArrayList<>();
    }

    public static boolean saveTradingFormulaToFile(TradingFormula method, String name) {
        List<Map<String, Object>> saved = removeSavedWithName(getSavedTradingFormulas(), name);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, Object> data = method.getMethodParams();
        data.put("formulaName", method.getSignature());
        data.put("name", name);

        saved.add(data);

        try {
            String path = createUserDataDirectory("methods");
            saveToFile(path, "formulas.json", gson.toJson(saved));
            return true;
        } catch (IOException _) {
            return false;
        }
    }

    public static void deleteSavedFormula(String name) {
        List<Map<String, Object>> saved = removeSavedWithName(getSavedTradingFormulas(), name);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String path = createUserDataDirectory("methods");
            saveToFile(path, "formulas.json", gson.toJson(saved));
        } catch (IOException _) {
        }
    }
}
