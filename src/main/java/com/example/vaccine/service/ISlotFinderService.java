package com.example.vaccine.service;

import com.example.vaccine.dto.VaccineResponseDTO;
import com.example.vaccine.enums.District;
import lombok.SneakyThrows;

public interface ISlotFinderService {

    void registerForNotification(District district, Integer age, String email);

    VaccineResponseDTO getAvailableSlots(District district, Integer age);

    @SneakyThrows
    VaccineResponseDTO getAvailableSlots(Integer pincode, Integer age);
}
