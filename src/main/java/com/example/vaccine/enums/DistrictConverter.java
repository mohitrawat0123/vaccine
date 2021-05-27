package com.example.vaccine.enums;

import java.beans.PropertyEditorSupport;

public class DistrictConverter extends PropertyEditorSupport {

    public void setAsText(String value) {
        setValue(District.fromValue(value));
    }

}
