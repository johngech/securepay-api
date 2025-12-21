package com.marakicode.securepay.wallets;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "SPRING")
public interface WalletMapper {
    @Mapping(target = "userId", source = "user.id")
    WalletDto toDto(Wallet wallet);
}
