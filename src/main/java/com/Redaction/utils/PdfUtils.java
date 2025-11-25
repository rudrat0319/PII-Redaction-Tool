package com.Redaction.utils;

import com.Redaction.exceptions.PdfProcessingException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfUtils {

    public static final String OUTPUT_DIR = "/mnt/data/output";

    public static final String TEST_INPUT_PDF = "/mnt/data/Take-home assignment (1).pdf";

    public static void ensureOutputDir() {
        try {
            Path p = Paths.get(OUTPUT_DIR);
            if (!Files.exists(p)) {
                Files.createDirectories(p);
            }
        } catch (Exception e) {
            throw new PdfProcessingException("Failed to create output directory: " + e.getMessage(), e);
        }
    }

    public static File fileFromPath(String path) {
        return new File(path);
    }
}