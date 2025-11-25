package com.Redaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfRedactionRequest {
    private boolean redactEmail;
    private boolean redactPhone;
    private boolean redactAddress;
    private boolean redactName;
    private String redactionStyle;
}
