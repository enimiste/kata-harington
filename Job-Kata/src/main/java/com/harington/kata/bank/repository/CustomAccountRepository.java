package com.harington.kata.bank.repository;

import com.harington.kata.bank.entity.Account;

import java.util.List;
import java.util.UUID;

public interface CustomAccountRepository {
    List<Account> trouverAccountsBalance(UUID number, int minBalance);
}
