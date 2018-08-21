package moneytransfer.rest;

import moneytransfer.model.Account;
import moneytransfer.services.AccountService;

import static moneytransfer.utils.JsonUtils.json;
import static spark.Spark.get;
import static spark.Spark.after;

public class AccountController {
    public AccountController(final AccountService accountService) {

        get("/accounts/:id", (req, res) -> {
            String id = req.params(":id");
            Account account = accountService.findAccount(id);
            if (account == null) {
                res.status(404);
                return new ResponseError("Could not find account with id [" + id + "]");
            }
            return account;
        }, json());


        after((req, res) -> {
            res.type("application/json");
        });
    }
}
