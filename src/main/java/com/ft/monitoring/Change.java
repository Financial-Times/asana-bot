package com.ft.monitoring;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@EqualsAndHashCode
public class Change {
    private String oldValue;
    private String newValue;
    private ChangeType type;

    public Change(String oldValue, String newValue, ChangeType type) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.type = type;
    }

    public String toString() {
        return type.toString() + " changed from '" + oldValue + "' to '" + newValue + "'";
    }
}
