package com.Redaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextRedactionResponse {
    private String originalText;
    private String redactedText;
    private RedactionSummary summary;
    private List<RemovedItem> removedItems;
    private String llmSummary;
}
