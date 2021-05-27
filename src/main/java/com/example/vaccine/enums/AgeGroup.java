package com.example.vaccine.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum AgeGroup {

    ABOVE_45("18+", 45),
    ABOVE_18("45+", 18);

    private final String name;

    private final Integer age;

    AgeGroup(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    private static final Map<String, AgeGroup> nameMap = new HashMap<>();
    private static final Map<Integer, AgeGroup> ageMap = new HashMap<>();

    static {
        for(AgeGroup ageGroup : values()){
            nameMap.put(ageGroup.name, ageGroup);
            ageMap.put(ageGroup.age, ageGroup);
        }
    }

    public Integer getAge() {
        return age;
    }

    public static AgeGroup from(String name) {
        if(!nameMap.containsKey(name)) {
            throw new IllegalArgumentException("No such enum found.");
        }
        return nameMap.get(name);
    }

    @JsonCreator
    public static AgeGroup fromValue(String value) {
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
