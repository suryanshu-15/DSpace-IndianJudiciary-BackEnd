//package org.dspace.app.rest.diracai.service.impl;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.dspace.app.rest.diracai.Repository.FileHashRecordRepository;
//import org.dspace.app.rest.diracai.service.JtdrIntegrationService;
//import org.dspace.content.Diracai.FileHashRecord;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import java.net.InetSocketAddress;
//import java.net.Proxy;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.HttpEntity;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//
//import java.io.File;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class JtdrIntegrationServiceImpl implements JtdrIntegrationService {
//
//
//    @Autowired
//    private FileHashRecordRepository repository;
//
//    @Override
//    public Map<String, Object> submitCase(String cnr, String zipHash) {
//        try {
//
//            String folderBasePath = "/home/dspace/dspace/jtdr/";
//            String zipFilePath = folderBasePath + cnr;
//
//            File zipFile = new File(zipFilePath);
//            if (!zipFile.exists()) {
//                return Map.of("error", "ZIP file not found at path", "path", zipFilePath);
//            }
//
//            String url = "https://orissa.jtdr.gov.in/api/add/case";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("cnr", cnr);
//            body.add("zipHash", zipHash);
//            body.add("caseZip", new FileSystemResource(zipFile));
//            body.add("userId","depositor_hc@orissa.hc.in");
//
//            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
//// Step 1: Create a request factory with proxy
//            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080)); // replace with actual VPN proxy IP & port
//            requestFactory.setProxy(proxy);
//
//// Step 2: Create RestTemplate with proxy
//            RestTemplate restTemplate = new RestTemplate(requestFactory);
//
//// Step 3: Use the same restTemplate for API call
//            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
//
//
//            Map<String, Object> responseMap = new ObjectMapper().readValue(response.getBody(), Map.class);
//
//            // Update FileHashRecord if ackId is present
//            if (responseMap.containsKey("ackId")) {
//                FileHashRecord record = repository.findByFileName(cnr);
//                if (record != null) {
//                    if (responseMap.containsKey("ackId")) {
//                        record.setAckId((String) responseMap.get("ackId"));
//                    }
//                    if (responseMap.containsKey("message")) {
//                        record.setPostResponse((String) responseMap.get("message"));
//                    }
//                    repository.save(record);
//                }
//
//            }
//
//            return responseMap;
//
//        } catch (Exception e) {
//            return Map.of("error", "Failed to submit case", "details", e.getMessage());
//        }
//    }
//
//
//    @Override
//    public Map<String, Object> checkStatus(String ackId) {
//        RestTemplate restTemplate = new RestTemplate();
//        try {
//            String url = "https://orissa.jtdr.gov.in/api/status/case?ackId=" + ackId;
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//            Map<String, Object> responseMap = new ObjectMapper().readValue(response.getBody(), Map.class);
//
//            // Set the 'message' in postResponse
//            if (responseMap.containsKey("message")) {
//                FileHashRecord record = repository.findByAckId(ackId);
//                if (record != null) {
//                    record.setGetCheckResponse((String) responseMap.get("message"));
//                    repository.save(record);
//                }
//            }
//
//            return responseMap;
//        } catch (Exception e) {
//            return Map.of("error", "Failed to get check response", "details", e.getMessage());
//        }
//    }
//
//}




package org.dspace.app.rest.diracai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.dspace.app.rest.diracai.Repository.FileHashRecordRepository;
import org.dspace.app.rest.diracai.service.JtdrIntegrationService;
import org.dspace.content.Diracai.FileHashRecord;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigInteger;



import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.dspace.app.rest.diracai.util.InsecureRestTemplateFactory.getInsecureRestTemplate;


@Service
@Slf4j
public class JtdrIntegrationServiceImpl implements JtdrIntegrationService {

    @Autowired
    private FileHashRecordRepository repository;

//    @Override
//    public Map<String, Object> submitCase(String cnr, String zipHash) {
//        try {
//            String folderBasePath = "/home/dspace/dspace/jtdr/";
//            String zipFilePath = folderBasePath + cnr ;
//            File zipFile = new File(zipFilePath);
//
//            if (!zipFile.exists()) {
//                return Map.of("error", "ZIP file not found", "path", zipFilePath);
//            }
//
//            String url = "https://orissa.jtdr.gov.in/api/add/case";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//            String number = cnr.replace(".zip","");
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("cnr", number);
//            body.add("zipHash", zipHash);
//            body.add("caseZip", new FileSystemResource(zipFile));
//            body.add("userId", "depositor_hc@orissa.hc.in");
//
//            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
//
//            // 🔒 Use the insecure RestTemplate (like curl --insecure)
//            RestTemplate restTemplate = getInsecureRestTemplate();
//
//            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
//            Map<String, Object> responseMap = new ObjectMapper().readValue(response.getBody(), Map.class);
//
//            if (responseMap.containsKey("ackId")) {
//                FileHashRecord record = repository.findByFileName(cnr);
//                if (record != null) {
//                    record.setAckId((String) responseMap.get("ackId"));
//                    record.setPostResponse((String) responseMap.getOrDefault("message", ""));
//                    repository.save(record);
//                }
//            }
//
//            return responseMap;
//
//        } catch (Exception e) {
//            FileHashRecord record = repository.findByFileName(cnr);
//            if (record != null) {
//                record.setPostResponse(e.getMessage());
//                repository.save(record);
//            }
//            return Map.of("error", "Failed to submit case", "details", e.getMessage());
//        }
//    }

    @Autowired
    private ConfigurationService configurationService;

    private Map<String, String> responseMessages = new HashMap<>();

    public String Mappers(String status) {
        // Initialize the response messages map
        responseMessages.put("200", "Case Received Successfully");
        responseMessages.put("401", "CNR must not be null or empty.");
        responseMessages.put("402", "Zip hash must not be null or empty.");
        responseMessages.put("403", "Invalid Zip File");
        responseMessages.put("404", "Invalid Zip: Zip name does not match with CNR");
        responseMessages.put("405", "Provided ZIP hash does not match actual file hash.");
        responseMessages.put("406", "Invalid userId");
        responseMessages.put("407", "Provided userId does not match any user in JTDR application");
        responseMessages.put("409", "Duplicate case detected. Case already exists for provided CNR.");
        responseMessages.put("500", "Internal Server Error");

        return responseMessages.get(status);
    }

    @Override
    public Map<String, Object> submitCase(String cnr) {
        try {

            String dspaceDir = configurationService.getProperty("dspace.dir");
            String folderBasePath = dspaceDir + "/jtdr";
            String zipFilePath = folderBasePath + "/" + cnr + ".zip";
            File zipFile = new File(zipFilePath);

            generateZip(cnr, zipFile);

            if (!zipFile.exists()) {
                return Map.of("error", "ZIP file not found", "path", zipFilePath);
            }

            String url = "https://orissa.jtdr.gov.in/api/add/case";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);


            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            String calculatedZipHash = calculateSHA256(zipFile);
            body.add("zipHash", calculatedZipHash);
            body.add("cnr", cnr);
            body.add("caseZip", new FileSystemResource(zipFile));
            body.add("userId", "depositor_hc@orissa.hc.in");

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = getInsecureRestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            Map<String, Object> responseMap = new ObjectMapper().readValue(response.getBody(), Map.class);

            String responseText = response.getBody();

            // Extract status code and message from the response text
            String statusCode = extractStatusCode(responseText);


            if (responseMap.containsKey("ackId")) {
                FileHashRecord record = repository.findByFileName(cnr);
                if (record != null) {
                    record.setAckId((String) responseMap.get("ackId"));
                    record.setPostResponse((String) responseMap.getOrDefault("message", ""));
                    repository.save(record);
                }
            } else {
                FileHashRecord record = repository.findByFileName(cnr);
                if (record != null) {
                    record.setPostStatus(statusCode);
                    record.setPostResponse(Mappers(statusCode));
                    repository.save(record);
                }
            }


            return responseMap;

        } catch (Exception e) {
            FileHashRecord record = repository.findByFileName(cnr);
            if (record != null) {
                record.setPostResponse(e.getMessage());
                repository.save(record);
            }
            return Map.of("error", "Failed to submit case", "details", e.getMessage());
        }
    }

    // Extracts the status code from the response text (e.g., "409")
    private String extractStatusCode(String responseText) {
        String[] parts = responseText.split(" ");
        return parts[0];
    }


    @Override
    public Map<String, Object> checkStatus(String ackId) {
        try {
            String url = "https://orissa.jtdr.gov.in/api/status/case?ackId=" + ackId;

            RestTemplate restTemplate = getInsecureRestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            Map<String, Object> responseMap = new ObjectMapper().readValue(response.getBody(), Map.class);

            if (responseMap.containsKey("message")) {
                FileHashRecord record = repository.findByAckId(ackId);
                if (record != null) {
                    record.setGetCheckResponse((String) responseMap.get("message"));
                    repository.save(record);
                }
            }

            return responseMap;

        } catch (Exception e) {
            return Map.of("error", "Failed to get check response", "details", e.getMessage());
        }
    }



    private void generateZip(String cnr, File zipFile) throws IOException {
        String cleanCnr = cnr.replaceAll("\\.zip$", ""); // Remove .zip if present

        File folderToZip = new File(zipFile.getParent(), cleanCnr); // e.g., /home/dspace/dspace/jtdr/ODHC010004612666
        if (!folderToZip.exists() || !folderToZip.isDirectory()) {
            throw new IOException("Folder to zip not found: " + folderToZip.getAbsolutePath());
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            File[] files = folderToZip.listFiles();
            if (files == null) throw new IOException("Failed to list files in folder: " + folderToZip);

            for (File file : files) {
                String zipEntryName = cleanCnr + "/" + file.getName(); // ✅ Keep consistent folder name inside zip
                zos.putNextEntry(new ZipEntry(zipEntryName));
                Files.copy(file.toPath(), zos);
                zos.closeEntry();
            }
        }
    }

    private String calculateSHA256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] hashBytes = digest.digest(fileBytes);
        BigInteger number = new BigInteger(1, hashBytes);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }


}
