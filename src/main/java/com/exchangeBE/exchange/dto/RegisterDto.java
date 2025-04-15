package com.exchangeBE.exchange.dto;

import com.exchangeBE.exchange.dto.report.Base64ImageDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RegisterDto {
    private String email;
    private String password;
    private String nickname;

    private String exchangeUniversity;
    private LocalDateTime exchangeStartDate;
    private LocalDateTime exchangeEndDate;
    private Base64ImageDto image;

    private List<String> exchangePurpose;
    private List<String> travelStyle;
    private List<String> disposition;
}
