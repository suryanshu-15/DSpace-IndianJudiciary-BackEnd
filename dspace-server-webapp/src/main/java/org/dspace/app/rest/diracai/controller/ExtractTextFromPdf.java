package org.dspace.app.rest.diracai.controller;

import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ExtractTextFromPdf {

    private static final String DIRECTORY_PATH = "/home/diracai/dspace/main/root/bin";
    private static final String BASE_COMMAND = "sudo ./dspace filter-media";

    // PostgreSQL connection info (update credentials if needed)
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/dspace";
    private static final String DB_USER = "dspace";
    private static final String DB_PASS = "root@123#"; 
    // @GetMapping("/filter-media/{prefix}/{suffix}")
    @GetMapping("/filter-media")
    public String filterByHandle() {
        // String handle = prefix + "/" + suffix;
    
    
        // String command = "sudo ./dspace filter-media -i " + handle;
        String command = "sudo ./dspace filter-media ";
    
        try {
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", command);
            pb.directory(new File(DIRECTORY_PATH)); // must point to dspace /bin
            pb.redirectErrorStream(true); // merge stderr with stdout
    
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
    private Optional<String> getItemUuidByHandle(String handle) {
        String sql = """
            SELECT i.uuid
            FROM handle h
            JOIN item i ON h.resource_id = i.uuid
            WHERE h.resource_type_id = 2 AND h.handle = ?
            """;        
                
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, handle);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}