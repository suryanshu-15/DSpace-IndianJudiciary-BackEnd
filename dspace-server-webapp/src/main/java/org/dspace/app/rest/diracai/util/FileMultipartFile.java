package org.dspace.app.rest.diracai.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FileMultipartFile implements MultipartFile {

    private final File file;

    public FileMultipartFile(File file) {
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return file.getName();
    }

    @Override
    public String getContentType() {
        return "application/zip";  // adjust if needed
    }

    @Override
    public boolean isEmpty() {
        return file.length() == 0;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return java.nio.file.Files.readAllBytes(file.toPath());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public void transferTo(File dest) throws IOException {
        java.nio.file.Files.copy(file.toPath(), dest.toPath());
    }
}
