package org.igov.io.db.kv.temp.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

public class ByteArrayMultipartFile implements MultipartFile, Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] content;
    private String name;
    private String contentType;
    private String exp;
    private String originalFilename;

    public ByteArrayMultipartFile() {
    }

    public ByteArrayMultipartFile(byte[] content, String name, String originalFilename,
            String contentType) {
        this.content = content;
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        String fileExp = getFileExp(originalFilename);
        if (fileExp != null) {
            this.exp = fileExp;
        } else {
            this.exp = "txt";
        }
    }

    /**
     * возращает расширение файла
     *
     * @param nameFile
     * @return
     */
    private static String getFileExp(String nameFile) {
        final Pattern oPattern = Pattern.compile("^[-a-zA-Z0-9+&#/%?=~:.;\"_*]+$");
        if (nameFile == null || nameFile.trim().isEmpty()) {
            return null;
        }
        Matcher m = oPattern.matcher(nameFile);
        if (m.find()) {
            String exp = null;
            for (String part : m.group(m.groupCount()).split("\\.")) {
                exp = part;
            }
            return exp;
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public String getExp() {
        return exp;
    }

    @Override
    public boolean isEmpty() {
        return content != null && content.length > 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException {
        new FileOutputStream(dest).write(content);
    }
}
