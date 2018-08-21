package moneytransfer.rest;

import moneytransfer.model.Account;
import moneytransfer.services.AccountService;
import moneytransfer.model.Client;
import moneytransfer.services.ClientService;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import static moneytransfer.utils.JsonUtils.json;
import static spark.Spark.after;
import static spark.Spark.post;

public class TestController {
    public TestController(ClientService clientService, AccountService accountService) {

        post("/test", (req, res) -> {

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

            return "SUCCESS";
        }, json());

        after((req, res) -> {
            res.type("application/json");
        });
    }
}
