package com.example.vaccine.dto;

import com.example.vaccine.enums.District;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
public class RegistrationRequestDTO {

    @NotEmpty
    List<String> email;

    @NotNull
    District district;

    Integer pincode;

    @NotNull
    Integer age;

}
