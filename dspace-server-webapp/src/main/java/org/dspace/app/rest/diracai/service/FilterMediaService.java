package org.dspace.app.rest.diracai.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

@Service
public class FilterMediaService {

    private static final String DIRECTORY_PATH = "/home/diracai/dspace/main/root/bin";
    private static final String BASE_COMMAND = "sudo ./dspace filter-media";

    public String runFilterMedia() {
        String command = BASE_COMMAND;

        try {
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", command);
            pb.directory(new File(DIRECTORY_PATH));
            pb.redirectErrorStream(true);

            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            return exitCode == 0
                    ? "Success:\n" + output
                    : "Command failed (exit code " + exitCode + "):\n" + output;

        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred while executing command: " + e.getMessage();
        }
    }
}
