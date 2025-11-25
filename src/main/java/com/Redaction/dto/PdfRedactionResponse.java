package com.Redaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfRedactionResponse {

    private String originalText;
    private String redactedText;
    private String redactedPdfUrl;
    private List<RemovedItem> removedItems;
    private RedactionSummary summary;
    private String llmSummary;
}
