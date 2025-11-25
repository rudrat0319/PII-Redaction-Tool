package com.Redaction.service;

import com.Redaction.dto.PdfRedactionRequest;
import com.Redaction.dto.PdfRedactionResponse;
import com.Redaction.dto.RemovedItem;
import com.Redaction.dto.RedactionSummary;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfRedactionService {

    private final PIIDetectionService piiDetectionService;
    private final LLMService llmService;

    @Value("${redaction.output-dir:redacted-output/}")
    private String OUTPUT_DIR;

    public PdfRedactionResponse redactPdfInPlace(MultipartFile file, PdfRedactionRequest options)
            throws IOException {

        validatePdf(file);
        validatePdfHeader(file);

        new File(OUTPUT_DIR).mkdirs();

        PdfRedactionResponse response = new PdfRedactionResponse();

        try (PDDocument document = PDDocument.load(file.getInputStream())) {

            String fullText = extractFullText(document);
            List<RemovedItem> piiItems = piiDetectionService.detect(fullText, options);

            calculateBoundingBoxes(document, piiItems); // still fallback logic
            applyBlackoutRedaction(document, piiItems);

            String outputPath = OUTPUT_DIR + "redacted_" + System.currentTimeMillis() + ".pdf";
            document.save(outputPath);

            response.setRedactedText(null);
            response.setOriginalText(null);
            response.setRemovedItems(piiItems);

            RedactionSummary summary = buildSummary(piiItems);
            response.setSummary(summary);

            String llmInputForPrivacySafety = String.format(
                    "PII Redaction Summary:\nEmails: %d\nPhones: %d\nNames: %d\nAddresses: %d\nTotal: %d",
                    summary.getEmailCount(),
                    summary.getPhoneCount(),
                    summary.getNameCount(),
                    summary.getAddressCount(),
                    summary.getTotalPiiFound()
            );

            response.setLlmSummary(llmService.generateSummary(llmInputForPrivacySafety));
            response.setRedactedPdfUrl(outputPath);
        }

        return response;
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded.");
        }

        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed.");
        }

        if (file.getContentType() == null || !"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Must be PDF.");
        }
    }

    private void validatePdfHeader(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[5];
            int readBytes = is.read(header);
            if (readBytes < 5 || !new String(header).startsWith("%PDF-")) {
                throw new IllegalArgumentException("Invalid PDF structure.");
            }
        }
    }

    private String extractFullText(PDDocument document) throws IOException {
        return new org.apache.pdfbox.text.PDFTextStripper().getText(document);
    }

    private void calculateBoundingBoxes(PDDocument document, List<RemovedItem> items) throws IOException {
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        Rectangle area = new Rectangle(0, 0, 5000, 5000);
        stripper.addRegion("region", area);

        for (RemovedItem item : items) {
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                PDPage page = document.getPage(pageIndex);
                stripper.extractRegions(page);

                if (stripper.getTextForRegion("region").contains(item.getValue())) {
                    item.setPageNumber(pageIndex);
                    item.setX(30);
                    item.setY(30 + (pageIndex * 5));
                    item.setWidth(250);
                    item.setHeight(25);
                    break;
                }
            }

            if (item.getPageNumber() == -1) {
                item.setPageNumber(-1);
            }
        }
    }

    private void applyBlackoutRedaction(PDDocument document, List<RemovedItem> items) throws IOException {
        for (RemovedItem item : items) {
            if (item.getPageNumber() < 0 || item.getPageNumber() >= document.getNumberOfPages()) {
                continue;
            }

            PDPage page = document.getPage(item.getPageNumber());
            PDFStreamParser parser = new PDFStreamParser(page);
            parser.parse();
            List<Object> tokens = parser.getTokens();
            List<Object> newTokens = new ArrayList<>();

            for (Object token : tokens) {
                if (token instanceof org.apache.pdfbox.contentstream.operator.Operator) {
                    String op = ((org.apache.pdfbox.contentstream.operator.Operator) token).getName();
                    if ("Tj".equals(op)) {
                        newTokens.add(new COSString("█"));
                        continue;
                    }
                    if ("TJ".equals(op)) {
                        newTokens.add(new COSArray());
                        continue;
                    }
                }
                newTokens.add(token);
            }

            PDStream newStream = new PDStream(document);
            try (OutputStream out = newStream.createOutputStream(COSName.FLATE_DECODE)) {
                new ContentStreamWriter(out).writeTokens(newTokens);
            }

            page.setContents(newStream);

            try (PDPageContentStream contentStream = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.addRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
                contentStream.fill();
            }
        }
    }

    private RedactionSummary buildSummary(List<RemovedItem> piiItems) {
        return new RedactionSummary(
                countType(piiItems, "email"),
                countType(piiItems, "phone"),
                countType(piiItems, "name"),
                countType(piiItems, "address"),
                piiItems.size(),
                "Redacted successfully"
        );
    }

    private int countType(List<RemovedItem> list, String type) {
        return (int) list.stream().filter(i -> i.getType().equalsIgnoreCase(type)).count();
    }
}