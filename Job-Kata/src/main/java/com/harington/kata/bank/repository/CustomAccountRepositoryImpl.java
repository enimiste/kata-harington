package com.harington.kata.bank.repository;

import com.harington.kata.bank.entity.Account;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

public class CustomAccountRepositoryImpl implements CustomAccountRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Account> trouverAccountsBalance(UUID number, int minBalance) {
        return entityManager.createQuery("select a from Account a").getResultList();
    }
}
