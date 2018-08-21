package moneytransfer.model;

import org.joda.money.Money;

import javax.persistence.*;

@Entity
public class Account {

    @Id
    private String id;
    private String accountNumber;
    private Money balance;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Client owner;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public Client getOwner() {
        return owner;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
