package com.example.vaccine.dao;

import com.example.vaccine.dto.RegistrationRequestDTO;

import java.util.List;

public interface IRegistrationRepositoryService {

    List<String> getEmailForDistrict(Integer id);

    List<String> getEmailForPincode(Integer id);

    void registerEmail(RegistrationRequestDTO registrationRequestDTO);

    boolean deRegisterEmail(RegistrationRequestDTO registrationRequestDTO);
}
