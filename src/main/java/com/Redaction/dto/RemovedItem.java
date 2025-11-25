package com.Redaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemovedItem {

    private String type;
    private String value;
    private double confidence;

    private int pageNumber;

    private float x;
    private float y;
    private float width;
    private float height;
}