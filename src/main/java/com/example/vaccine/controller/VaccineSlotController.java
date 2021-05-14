package com.example.vaccine.controller;

import com.example.vaccine.dto.RegistrationRequestDTO;
import com.example.vaccine.dto.VaccineResponseDTO;
import com.example.vaccine.enums.District;
import com.example.vaccine.enums.DistrictConverter;
import com.example.vaccine.service.SlotFinderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/vaccine/slot")
public class VaccineSlotController {

    @Autowired
    private SlotFinderService slotFinderService;

    @PostMapping("/register")
    public ResponseEntity<String> registerForNotification(@Valid @RequestBody RegistrationRequestDTO requestDTO) {
        slotFinderService.registerForNotification(requestDTO);
        return ResponseEntity.ok("User Registered Successfully !!");
    }

    @DeleteMapping("/register")
    public ResponseEntity<String> deRegisterForNotification(@Valid @RequestBody RegistrationRequestDTO requestDTO) {
        slotFinderService.deRegisterForNotification(requestDTO);
        return ResponseEntity.ok("User Registered Successfully !!");
    }

    @GetMapping("/get/district")
    public ResponseEntity<VaccineResponseDTO> getSlots(@RequestParam District district,
                                                       @RequestParam(required = false) Integer age) {
        return ResponseEntity.ok(slotFinderService.getAvailableSlots(district, age));
    }

    @GetMapping("/get/pincode")
    public ResponseEntity<VaccineResponseDTO> getSlots(@RequestParam Integer pincode,
                                                       @RequestParam(required = false) Integer age) {
        return ResponseEntity.ok(slotFinderService.getAvailableSlots(pincode, age));
    }

    @InitBinder
    public void initBinder(final WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(District.class, new DistrictConverter());
    }
}
