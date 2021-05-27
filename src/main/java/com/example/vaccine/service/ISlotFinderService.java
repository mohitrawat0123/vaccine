package com.example.vaccine.service;

import com.example.vaccine.dto.RegistrationRequestDTO;
import com.example.vaccine.dto.VaccineResponseDTO;
import com.example.vaccine.enums.District;
import lombok.SneakyThrows;

public interface ISlotFinderService {

    void registerForNotification(RegistrationRequestDTO requestDTO);

    void deRegisterForNotification(RegistrationRequestDTO requestDTO);

    VaccineResponseDTO getAvailableSlots(District district, Integer age);

    @SneakyThrows
    VaccineResponseDTO getAvailableSlots(Integer pincode, Integer age);
}
