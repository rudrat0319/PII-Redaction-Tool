package com.Redaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextRedactionRequest {
    private String text;
    private boolean email;
    private boolean name;
    private boolean address;
    private boolean phone;
}
