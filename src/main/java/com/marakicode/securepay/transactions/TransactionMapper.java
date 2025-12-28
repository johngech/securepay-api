package com.marakicode.securepay.transactions;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "SPRING")
public interface TransactionMapper {
    @Mapping(target = "provider", source = "provider.providerType")
    TransactionDto toDto(Transaction transaction);

    List<TransactionDto> toDtoList(List<Transaction> transactions);
}
