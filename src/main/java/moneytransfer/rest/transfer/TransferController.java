package moneytransfer.rest.transfer;

import moneytransfer.model.Transfer;
import moneytransfer.rest.ResponseError;
import moneytransfer.services.TransferException;
import moneytransfer.services.TransferService;
import org.joda.money.Money;

import static moneytransfer.utils.JsonUtils.getMapper;
import static moneytransfer.utils.JsonUtils.json;
import static moneytransfer.utils.JsonUtils.toJson;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.post;

public class TransferController {

    public TransferController(TransferService transferService) {

        post("/transfers", (req, res) -> {
            TransferRequest transferRequest = getMapper().readValue(req.body(), TransferRequest.class);
            Transfer transfer = transferService.makeTransfer(transferRequest.sourceAccountId, transferRequest.destinationAccountId, transferRequest.amount);
            return TransferTO.fromTransfer(transfer);
        }, json());

        after((req, res) -> {
            res.type("application/json");
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
            res.type("application/json");
        });

        exception(TransferException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
            res.type("application/json");
        });
    }

    public static class TransferRequest {
        public String sourceAccountId;
        public String destinationAccountId;
        public Money amount;
    }
}
