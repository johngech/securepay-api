package com.marakicode.securepay.mappers;

import com.marakicode.securepay.dtos.WalletDto;
import com.marakicode.securepay.entities.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "SPRING")
public interface WalletMapper {
    @Mapping(target = "userId", source = "user.id")
    WalletDto toDto(Wallet wallet);
}
