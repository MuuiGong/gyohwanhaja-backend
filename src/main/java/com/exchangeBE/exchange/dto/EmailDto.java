package com.exchangeBE.exchange.dto;

import lombok.Data;

@Data
public class EmailDto {
    private String email;
    private String verificationCode;
}
