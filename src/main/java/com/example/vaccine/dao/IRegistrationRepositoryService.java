package com.example.vaccine.dao;

import com.example.vaccine.dto.RegistrationRequestDTO;
import com.example.vaccine.dto.UserDTO;
import com.example.vaccine.enums.Vaccine;

import java.util.List;

public interface IRegistrationRepositoryService {

    List<UserDTO> getUserForDistrict(Integer id, Vaccine vaccine);

    List<String> getPhoneNumberForDistrict(Integer id, Vaccine vaccine);

    List<String> getEmailForDistrict(Integer id, Vaccine vaccine);

    List<String> getEmailForPincode(Integer id, Vaccine vaccine);

    void registerEmail(RegistrationRequestDTO registrationRequestDTO);

    boolean deRegisterEmail(RegistrationRequestDTO registrationRequestDTO);
}
