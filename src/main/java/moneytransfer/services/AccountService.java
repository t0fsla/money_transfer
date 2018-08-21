package moneytransfer.services;

import moneytransfer.model.Account;
import moneytransfer.utils.JpaUtils;

import javax.persistence.EntityManager;

public class AccountService {

    public Account findAccount(String accountId) {
        EntityManager manager = JpaUtils.createEntityManager();
        try {
            manager.getTransaction().begin();
            Account account = manager.find(Account.class, accountId);
            return account;
        } finally {
            manager.close();
        }
    }

    public void saveAccountIfNotExists(Account account) {
        EntityManager manager = JpaUtils.createEntityManager();
        try {
            manager.getTransaction().begin();
            manager.merge(account);
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw e;
        } finally {
            manager.close();
        }
    }
}
