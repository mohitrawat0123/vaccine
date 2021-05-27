package com.example.vaccine.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum Vaccine {

    ANY("any"),
    COVAXIN("covaxin"),
    COVISHIELD("covishield");

    private final String name;

    Vaccine(String name) {
        this.name = name;
    }

    private static final Map<String, Vaccine> nameMap = new HashMap<>();

    static {
        for(Vaccine vaccine : values()) {
            nameMap.put(vaccine.name.toLowerCase(), vaccine);
        }
    }

    public String getName() {
        return name;
    }

    public static Vaccine from(String name) {
        String key = name.toLowerCase();
        if(!nameMap.containsKey(key))
            throw new IllegalArgumentException("No such enum found.");
        return nameMap.get(key);
    }

    @JsonCreator
    public static Vaccine fromValue(String value) {
        try {
            return from(value);
        } catch (IllegalArgumentException ex){
            return valueOf(value);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
