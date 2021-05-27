package com.example.vaccine.dto;

import com.example.vaccine.enums.AgeGroup;
import com.example.vaccine.enums.Vaccine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotBlank
    private String email;

    @NotEmpty
    private String phone;

    @NotNull
    private AgeGroup ageGroup;

    private Vaccine vaccine = Vaccine.ANY;

}
