package moneytransfer.rest.transfer;

import moneytransfer.model.Transfer;
import org.joda.money.Money;

import java.util.Date;

public class TransferTO {
    public String id;
    public String sourceAccountId;
    public String destinationAccountId;
    public Date transactionTime;
    public Money amount;

    public static TransferTO fromTransfer(Transfer transfer) {
        TransferTO to = new TransferTO();

        to.id = transfer.getId();
        to.amount = transfer.getAmount();
        to.transactionTime = transfer.getTransactionTime();
        to.sourceAccountId = transfer.getSource().getId();
        to.destinationAccountId = transfer.getDestination().getId();

        return to;
    }
}
