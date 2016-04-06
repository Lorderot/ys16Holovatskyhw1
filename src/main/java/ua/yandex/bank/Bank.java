package ua.yandex.bank;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mykola Holovatsky
 */
public class Bank {
    private Map<Integer, Account> accountMap =
            Collections.synchronizedMap(new HashMap<>());

    private class Account {
        private final int number;
        private String name;
        private String surname;
        private String passportSerialNumber;
        private AtomicInteger count;

        Account(int number, String name, String surname,
                       String passportSerialNumber) {
            this.number = number;
            this.name = name;
            this.surname = surname;
            this.passportSerialNumber = passportSerialNumber;
            this.count = new AtomicInteger(0);
        }

        synchronized boolean withdraw(int amount) {
            if (count.get() < amount) {
                return false;
            }
            count.getAndUpdate((count) -> count - amount);
            return true;
        }

        boolean deposit(int amount) {
            count.getAndUpdate((count) -> count + amount);
            return true;
        }
    }

    public Integer createNewAccount(String surname, String name,
                                    String passportSerialNumber)
            throws IllegalArgumentException {
        Integer accountNumber = surname.hashCode() + name.hashCode()
                + passportSerialNumber.hashCode();
        if (accountMap.containsKey(accountNumber)) {
            throw new IllegalArgumentException();
        }
        accountMap.put(accountNumber, new Account(accountNumber,
                surname, name, passportSerialNumber));

        return accountNumber;
    }

    public void transfer(Integer accountNumberFrom,
                         Integer accountNumberTo, int amount)
            throws IllegalArgumentException {
        Account from = accountMap.get(accountNumberFrom);
        Account to = accountMap.get((accountNumberTo));
        if (from == null || to == null) {
            throw new IllegalArgumentException();
        }
        transfer(from, to, amount);
    }

    public Integer deposit(Integer accountNumber, int amount)
            throws IllegalArgumentException {
        Account account = accountMap.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException();
        }
        account.deposit(amount);
        return account.count.get();
    }

    public Integer checkBalance(Integer accountNumber) {
        Account account = accountMap.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException();
        }
        return account.count.get();
    }

    private boolean transfer(Account from, Account to, int amount)
            throws IllegalArgumentException {
        if (from == null || to == null) {
            throw new IllegalArgumentException();
        }
        if (from.equals(to)) {
            return true;
        }

        synchronized (from) {
            if (!from.withdraw(amount)) {
                return false;
            }
        }
        synchronized (to) {
            to.deposit(amount);
        }
        return true;
    }
}
