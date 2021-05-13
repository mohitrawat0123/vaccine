package com.example.vaccine.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CenterResponseDTO {

    private String name;
    private String address;

    @JsonAlias("district_name")
    private String districtName;

    private Integer pincode;

    private List<SessionDTO> sessions;
}
