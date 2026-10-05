package com.fieldsync.model;

public class Field {
    private int fieldId;
    private String fieldName;
    private String location;
    private boolean isActive;

    public Field(int fieldId, String fieldName, String location, boolean isActive) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.location = location;
        this.isActive = isActive;
    }

    public int getFieldId() {
        return fieldId;
    }

    


    public String getFieldName() {
        return fieldName;
    }

    public String getLocation() {
        return location;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return fieldName;
    }
}