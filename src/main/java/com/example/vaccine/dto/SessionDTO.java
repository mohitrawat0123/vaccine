package com.example.vaccine.dto;

import com.example.vaccine.enums.Vaccine;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionDTO {


    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date date;

    @JsonAlias("available_capacity")
    private Integer available;

    @JsonAlias("available_capacity_dose1")
    private Integer availableDose1;

    @JsonAlias("available_capacity_dose2")
    private Integer availableDose2;

    @JsonAlias("min_age_limit")
    private Integer minAge;

    private Vaccine vaccine;

    private List<String> slots;
}
