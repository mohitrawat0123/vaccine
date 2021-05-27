package com.example.vaccine.dto;

import com.example.vaccine.enums.District;
import com.example.vaccine.enums.Vaccine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDTO {

    @NotEmpty
    private List<UserDTO> users;

    @NotNull
    private District district;

}
