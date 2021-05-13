package com.example.vaccine.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionDTO {


    @JsonFormat(pattern = "dd-MM-YYYY")
    private Date date;

    @JsonAlias("available_capacity")
    private Integer available;

    @JsonAlias("min_age_limit")
    private Integer minAge;

    private List<String> slots;
}
