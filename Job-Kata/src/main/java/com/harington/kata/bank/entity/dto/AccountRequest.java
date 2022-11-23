package com.harington.kata.bank.entity.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Builder
public class AccountRequest {
    @Min(0)
    int initialBalanceInCents;
    @NotNull
    @NotEmpty
    @Size(min = 3)
    String ownerName;
}
