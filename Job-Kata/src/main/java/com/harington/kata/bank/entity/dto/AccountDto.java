package com.harington.kata.bank.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Builder
@Getter
@Value
public class AccountDto {
    String accountNumber;
    String currentBalance;
    String ownerName;
    String createdAt;
}
