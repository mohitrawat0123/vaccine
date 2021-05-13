package com.example.vaccine.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum District {

    CENTRAL_DELHI("CentralDelhi", 141),
    WEST_DELHI("WestDelhi", 142),
    EAST_DELHI("EastDelhi", 145),
    SHAHDARA("Shahdara", 148);

    private final String name;
    private final Integer id;

    private static final Map<String, District> nameMap = new HashMap<>();

    District(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    static {
        for(District district : values()){
            nameMap.put(district.name.toLowerCase(), district);
        }
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public static District from(String name) {
        String key = name.toLowerCase();
        if(!nameMap.containsKey(key))
            throw new IllegalArgumentException("No such enum found.");
        return nameMap.get(key);
    }

    @JsonCreator
    public static District fromValue(String value) {
        try {
            return from(value);
        } catch (IllegalArgumentException ex){
            return valueOf(value);
        }
    }

}
