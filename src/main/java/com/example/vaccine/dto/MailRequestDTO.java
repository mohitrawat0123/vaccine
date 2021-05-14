package com.example.vaccine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MailRequestDTO {

    private String from = "mohitrawat0123@cowinbot.com";

    private List<String> to;

    private String subject;

    private String body;

}
