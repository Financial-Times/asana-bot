package com.ft.asanaapi.model;

import lombok.Data;

@Data
public class AsanaEntity {
    private String id;
    private String name;

    @Override
    public String toString() {
        return getName();
    }
}
