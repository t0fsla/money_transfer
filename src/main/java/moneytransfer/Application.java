package moneytransfer;

import moneytransfer.rest.AccountController;
import moneytransfer.services.AccountService;
import moneytransfer.services.ClientService;
import moneytransfer.rest.TestController;
import moneytransfer.rest.transfer.TransferController;
import moneytransfer.services.TransferService;
import moneytransfer.utils.JpaUtils;

public class Application {
    public static void main(String[] args) {

        JpaUtils.init();

        ClientService clientService = new ClientService();
        AccountService accountService = new AccountService();
        TransferService transferService = new TransferService(accountService);

        if (System.getProperty("devMode") != null) {
            new TestController(clientService, accountService);
        }

        new AccountController(accountService);
        new TransferController(transferService);
    }
}
