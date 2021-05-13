package com.example.vaccine.controller;

import com.example.vaccine.dto.VaccineResponseDTO;
import com.example.vaccine.enums.DistrictConverter;
import com.example.vaccine.service.SlotFinderService;
import com.example.vaccine.enums.District;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vaccine/slot")
public class VaccineSlotController {

    @Autowired
    private SlotFinderService slotFinderService;

    @PostMapping("/register")
    public ResponseEntity<String> registerForNotification(@RequestParam District district,
                                                          @RequestParam Integer age,
                                                          @RequestParam String email) {
        slotFinderService.registerForNotification(district, age, email);
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
