package com.Redaction.service;

import com.Redaction.dto.PdfRedactionRequest;
import com.Redaction.dto.RemovedItem;
import com.Redaction.dto.TextRedactionRequest;
import com.Redaction.utils.RegexPatterns;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Service
@RequiredArgsConstructor
public class PIIDetectionService {
    private final LLMService llmService;

    public List<RemovedItem> detectFromText(String text, TextRedactionRequest request) {

        List<RemovedItem> items = new ArrayList<>();

        if (request.isEmail()) {
            Matcher m = RegexPatterns.EMAIL_PATTERN.matcher(text);
            while (m.find()) {
                items.add(new RemovedItem(
                        "email",
                        m.group(),
                        1.0,
                        -1, -1, -1, -1, -1 // coordinates will be set later for PDF only
                ));
            }
        }

        if (request.isPhone()) {
            Matcher m = RegexPatterns.PHONE_PATTERN.matcher(text);
            while (m.find()) {
                items.add(new RemovedItem(
                        "phone",
                        m.group(),
                        1.0,
                        -1, -1, -1, -1, -1
                ));
            }
        }

        if (request.isName()) {
            List<RemovedItem> foundNames = simpleNameHeuristic(text);
            items.addAll(foundNames);
        }

        if (request.isAddress()) {
            List<RemovedItem> foundAddresses = simpleAddressHeuristic(text);
            items.addAll(foundAddresses);
        }

        return items;
    }

    public List<RemovedItem> detect(String text, PdfRedactionRequest request) {

        List<RemovedItem> items = new ArrayList<>();

        if (request.isRedactEmail()) {
            Matcher m = RegexPatterns.EMAIL_PATTERN.matcher(text);
            while (m.find()) {
                items.add(new RemovedItem(
                        "email",
                        m.group(),
                        1.0,
                        -1, -1, -1, -1, -1
                ));
            }
        }

        if (request.isRedactPhone()) {
            Matcher m = RegexPatterns.PHONE_PATTERN.matcher(text);
            while (m.find()) {
                items.add(new RemovedItem(
                        "phone",
                        m.group(),
                        1.0,
                        -1, -1, -1, -1, -1
                ));
            }
        }

        if (request.isRedactName()) {
            items.addAll(simpleNameHeuristic(text));
        }

        if (request.isRedactAddress()) {
            items.addAll(simpleAddressHeuristic(text));
        }

        return items;
    }

    private List<RemovedItem> simpleNameHeuristic(String text) {
        List<RemovedItem> result = new ArrayList<>();
        Matcher m = RegexPatterns.NAME_SIMPLE_PATTERN.matcher(text);

        while (m.find()) {
            result.add(new RemovedItem(
                    "name",
                    m.group(),
                    0.5,
                    -1, -1, -1, -1, -1
            ));
        }
        return result;
    }

    private List<RemovedItem> simpleAddressHeuristic(String text) {
        List<RemovedItem> result = new ArrayList<>();
        Matcher m = RegexPatterns.ADDRESS_SIMPLE_PATTERN.matcher(text);

        while (m.find()) {
            result.add(new RemovedItem(
                    "address",
                    m.group(),
                    0.5,
                    -1, -1, -1, -1, -1
            ));
        }
        return result;
    }
}
