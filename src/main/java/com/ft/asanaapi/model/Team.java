package com.ft.asanaapi.model;

import lombok.Data;

@Data
public class Team {
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
