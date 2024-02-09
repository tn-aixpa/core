package it.smartcommunitylabdhub.commons.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {

    private static final String LOG_DIRECTORY = "<logs/path>";

    public static void writeLog(String fileName, String logMessage) {
        String filePath = LOG_DIRECTORY + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Create the log entry with timestamp
            String logEntry = logMessage + "\n";

            // Write the log entry to the file
            writer.write(logEntry);
            writer.newLine();

            // Flush the writer to ensure the log entry is written immediately
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
