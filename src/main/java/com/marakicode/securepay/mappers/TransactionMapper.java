package com.marakicode.securepay.mappers;

import com.marakicode.securepay.dtos.TransactionDto;
import com.marakicode.securepay.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "SPRING")
public interface TransactionMapper {
    @Mapping(target = "provider", source = "provider.name")
    TransactionDto toDto(Transaction transaction);

    List<TransactionDto> toDtoList(List<Transaction> transactions);
}
