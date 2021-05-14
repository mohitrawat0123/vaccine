package com.example.vaccine.dto;

import com.example.vaccine.enums.Vaccine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotBlank
    private String email;

    private Vaccine vaccine = Vaccine.ANY;

}
