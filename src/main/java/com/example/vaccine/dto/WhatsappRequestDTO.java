package com.example.vaccine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WhatsappRequestDTO {

    private String to;

    private String body;
}
