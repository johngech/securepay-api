package com.marakicode.securepay.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WalletDto {
    private Integer id;
    private Long userId;
    private BigDecimal balance;
}
