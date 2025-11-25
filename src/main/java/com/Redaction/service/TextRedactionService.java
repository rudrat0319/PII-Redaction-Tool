package com.Redaction.service;

import com.Redaction.dto.RedactionSummary;
import com.Redaction.dto.RemovedItem;
import com.Redaction.dto.TextRedactionRequest;
import com.Redaction.dto.TextRedactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TextRedactionService {

    private final PIIDetectionService piiDetectionService;
    private final LLMService llmService;

    public TextRedactionResponse redactText(TextRedactionRequest request) {

        String original = request.getText() == null ? "" : request.getText();

        List<RemovedItem> removedItems = piiDetectionService.detectFromText(original, request);

        String redacted = applyRedactions(original, removedItems);

        RedactionSummary summary = buildSummary(removedItems);

        String llmSummary = llmService.generateSummary(redacted);

        return new TextRedactionResponse(original, redacted, summary, removedItems, llmSummary);
    }

    private String applyRedactions(String text, List<RemovedItem> removedItems) {

        String result = text;
        int emailCounter = 1, phoneCounter = 1, nameCounter = 1, addressCounter = 1;

        for (RemovedItem item : removedItems) {
            String replacement;

            switch (item.getType().toLowerCase()) {
                case "email":
                    replacement = "[EMAIL_" + (emailCounter++) + "]";
                    break;
                case "phone":
                    replacement = "[PHONE_" + (phoneCounter++) + "]";
                    break;
                case "name":
                    replacement = "[NAME_" + (nameCounter++) + "]";
                    break;
                case "address":
                    replacement = "[ADDRESS_" + (addressCounter++) + "]";
                    break;
                default:
                    replacement = "[REDACTED]";
            }

            result = result.replaceFirst(Pattern.quote(item.getValue()), replacement);
        }

        return result;
    }

    private RedactionSummary buildSummary(List<RemovedItem> removedItems) {

        RedactionSummary summary = new RedactionSummary();

        for (RemovedItem item : removedItems) {

            switch (item.getType().toLowerCase()) {
                case "email":
                    summary.setEmailCount(summary.getEmailCount() + 1);
                    break;
                case "phone":
                    summary.setPhoneCount(summary.getPhoneCount() + 1);
                    break;
                case "name":
                    summary.setNameCount(summary.getNameCount() + 1);
                    break;
                case "address":
                    summary.setAddressCount(summary.getAddressCount() + 1);
                    break;
            }

            summary.setTotalPiiFound(summary.getTotalPiiFound() + 1);
        }

        return summary;
    }
}