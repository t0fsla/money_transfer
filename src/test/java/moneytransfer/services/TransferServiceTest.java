package moneytransfer.services;

import moneytransfer.model.Account;
import moneytransfer.model.Client;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class TransferServiceTest {

    @Test
    public void make_several_transfers_in_parallel() throws InterruptedException {
        ClientService clientService = new ClientService();
        AccountService accountService = new AccountService();
        TransferService transferService = new TransferService(accountService);

        Client client = new Client();
        client.setId("smith");
        client.setFullName("Anderson Smith");
        client.setEmail("anderson@m.com");
        clientService.saveClientIfNotExists(client);

        Account account1 = new Account();
        account1.setId("acnt1");
        account1.setAccountNumber("ACNT-1111111111");
        account1.setBalance(Money.of(CurrencyUnit.EUR, 111));
        account1.setOwner(client);
        accountService.saveAccountIfNotExists(account1);

        Account account2 = new Account();
        account2.setId("acnt2");
        account2.setAccountNumber("ACNT-2222222222");
        account2.setBalance(Money.of(CurrencyUnit.EUR, 222));
        account2.setOwner(client);
        accountService.saveAccountIfNotExists(account2);


        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        Money amount = Money.of(CurrencyUnit.EUR, new BigDecimal("1"));
        for (int i = 0; i < 10; i++) {
            threadPool.submit(() -> transferService.makeTransfer("acnt1", "acnt2", amount));
        }

        threadPool.shutdown();
        threadPool.awaitTermination(60, TimeUnit.SECONDS);

        Account account = accountService.findAccount("acnt1");
        assertThat(account.getBalance().getAmount().compareTo(new BigDecimal(101)), equalTo(0));
    }
}