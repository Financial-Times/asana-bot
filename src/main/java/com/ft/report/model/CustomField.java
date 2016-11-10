package com.ft.report.model;

import lombok.Data;

@Data
public class CustomField {
    private String id;
    private String name;
    private String type;
    private CustomFieldEnumValue enum_value;
    private String text_value;
    private String number_value;

}

