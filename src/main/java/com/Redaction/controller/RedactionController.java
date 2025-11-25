package com.Redaction.controller;

import com.Redaction.dto.PdfRedactionRequest;
import com.Redaction.dto.PdfRedactionResponse;
import com.Redaction.dto.TextRedactionRequest;
import com.Redaction.dto.TextRedactionResponse;
import com.Redaction.service.TextRedactionService;
import com.Redaction.service.PdfRedactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/redact")
public class RedactionController {

    private final TextRedactionService textRedactionService;
    private final PdfRedactionService pdfRedactionService;

    @PostMapping(
            value = "/text",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TextRedactionResponse> redactText(
            @RequestBody TextRedactionRequest request
    ) {
        TextRedactionResponse response = textRedactionService.redactText(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/pdf",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> redactPdf(
            @RequestPart("file") MultipartFile file,
            @RequestParam boolean redactEmail,
            @RequestParam boolean redactName,
            @RequestParam boolean redactPhone,
            @RequestParam boolean redactAddress,
            @RequestParam(defaultValue = "blackout") String redactionStyle
    ) {
        try {
            PdfRedactionRequest options = new PdfRedactionRequest(
                    redactEmail,
                    redactPhone,
                    redactAddress,
                    redactName,
                    redactionStyle
            );

            PdfRedactionResponse response = pdfRedactionService.redactPdfInPlace(file, options);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}