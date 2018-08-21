package moneytransfer.services;

import moneytransfer.model.Account;
import moneytransfer.model.Transfer;
import moneytransfer.utils.JpaUtils;
import org.h2.util.StringUtils;
import org.joda.money.Money;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Date;

public class TransferService {

    private final AccountService accountService;

    public TransferService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Transfer makeTransfer(String srcAccountId, String dstAccountId, Money amount) {
        checkParameters(srcAccountId, dstAccountId, amount);

        Account srcAccount = accountService.findAccount(srcAccountId);
        if (srcAccount == null) {
            throw new IllegalArgumentException("Could not find Source Account by ID [" + srcAccountId + "]");
        }

        Account dstAccount = accountService.findAccount(dstAccountId);
        if (dstAccount == null) {
            throw new IllegalArgumentException("Could not find Destination Account by ID [" + dstAccountId + "]");
        }

        if (!amount.getCurrencyUnit().equals(srcAccount.getBalance().getCurrencyUnit())) {
            throw new TransferException("Could not make transfer in [" + amount.getCurrencyUnit().getCode() + "]" +
                    "from source account in [" + srcAccount.getBalance().getCurrencyUnit().getCode() + "]");
        }

        if (!amount.getCurrencyUnit().equals(dstAccount.getBalance().getCurrencyUnit())) {
            throw new TransferException("Could not make transfer in [" + amount.getCurrencyUnit().getCode() + "]" +
                    "to destination account in [" + srcAccount.getBalance().getCurrencyUnit().getCode() + "]");
        }

        return makeTransferInTransaction(srcAccountId, dstAccountId, amount);
    }

    private synchronized Transfer makeTransferInTransaction(String srcAccountId, String dstAccountId, Money amount) {

        EntityManager manager = JpaUtils.createEntityManager();

        try {
            manager.getTransaction().begin();
            Account srcAccount = manager.find(Account.class, srcAccountId);
            Account dstAccount = manager.find(Account.class, dstAccountId);

            if (srcAccount.getBalance().isLessThan(amount)) {
                throw new TransferException("Source account does not have enough amount");
            }

            srcAccount.setBalance(srcAccount.getBalance().minus(amount));
            dstAccount.setBalance(dstAccount.getBalance().plus(amount));

            Transfer transfer = new Transfer();
            transfer.setAmount(amount);
            transfer.setSource(srcAccount);
            transfer.setDestination(dstAccount);
            transfer.setTransactionTime(new Date());
            manager.persist(transfer);
            manager.getTransaction().commit();

            return transfer;

        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw e;
        } finally {
            manager.close();
        }
    }

    private void checkParameters(String srcAccountId, String dstAccountId, Money amount) {
        if (StringUtils.isNullOrEmpty(srcAccountId)) {
            throw new IllegalArgumentException("Source Account ID should not be null for Money Transfer operation");
        }
        if (StringUtils.isNullOrEmpty(dstAccountId)) {
            throw new IllegalArgumentException("Destination Account ID should not be null for Money Transfer operation");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount parameter should not be null for Money Transfer operation");
        }
        if (BigDecimal.ZERO.equals(amount.getAmount())) {
            throw new IllegalArgumentException("Amount for Money Transfer should not be zero");
        }
    }
}
