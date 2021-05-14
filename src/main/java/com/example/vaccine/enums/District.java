package com.example.vaccine.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.models.auth.In;

import java.util.HashMap;
import java.util.Map;

public enum District {


    //DELHI
    CENTRAL_DELHI("CentralDelhi", 141),
    WEST_DELHI("WestDelhi", 142),
    NORTH_WEST_DELHI("NorthWestDelhi", 143),
    SOUTH_EAST_DELHI("SouthEastDelhi", 144),
    EAST_DELHI("EastDelhi", 145),
    NORTH_DELHI("NorthDelhi", 146),
    NORTH_EAST_DELHI("NorthEastDelhi", 147),
    SHAHDARA("Shahdara", 148),
    SOUTH_DELHI("SouthDelhi", 149),
    SOUTH_WEST_DELHI("SouthWestDelhi", 150),

    //HARYANA
    GURGAON("Gurgaon", 188),

    //UP
    GAUTAM_BUDDH_NAGAR("GautamBuddhaNagar", 650),
    GHAZIABAD("Ghaziabad", 651);



    private final String name;
    private final Integer id;

    private static final Map<String, District> nameMap = new HashMap<>();

    private static final Map<Integer, District> idMap = new HashMap<>();

    District(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    static {
        for(District district : values()){
            nameMap.put(district.name.toLowerCase(), district);
            idMap.put(district.id, district);
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

    public static District fromId(Integer id) {
        if(!idMap.containsKey(id))
            throw new IllegalArgumentException("No such enum found.");
        return idMap.get(id);
    }

    @JsonCreator
    public static District fromValue(String value) {
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
