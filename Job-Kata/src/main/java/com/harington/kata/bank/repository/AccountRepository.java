package com.harington.kata.bank.repository;

import com.harington.kata.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, CustomAccountRepository {
    Optional<Account> findOneByAccountNumber(UUID number);
}
