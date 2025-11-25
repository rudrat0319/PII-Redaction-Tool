package com.Redaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedactionSummary {
    private int emailCount;
    private int phoneCount;
    private int nameCount;
    private int addressCount;
    private int totalPiiFound;

    private String explanation;
}
