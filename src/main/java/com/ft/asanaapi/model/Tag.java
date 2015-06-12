package com.ft.asanaapi.model;

import lombok.Data;

@Data
public class Tag {
    private String id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
