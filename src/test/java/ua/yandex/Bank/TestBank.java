package ua.yandex.bank;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class TestBank {
    private Bank bank = new Bank();

    @Test
    public void testBank_multiThreadingTransfer() {
        int numberOfThreads = 15000;
        int numberOfAccount = 100;
        int limitMoney = 10000;
        int[] accountNumbers = createPseudoAccounts(numberOfAccount);
        int expectedTotalMoney = putRandomMoneyOnDeposit(accountNumbers, limitMoney * 2);

        Thread[] threads = startNewThreads(accountNumbers,
                numberOfThreads, limitMoney);

        for (int i = 0; i < numberOfAccount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int totalMoney = countTotalMoney(accountNumbers);

        assertEquals(expectedTotalMoney, totalMoney);
    }

    private int[] createPseudoAccounts(int numberOfAccount) {
        int[] accountNumbers = new int[numberOfAccount];
        Random generator = new Random(System.nanoTime());
        for (int i = 0; i < numberOfAccount; i++) {
            String pseudoData = String.valueOf(i);
            accountNumbers[i] =
                    bank.createNewAccount(pseudoData, pseudoData, pseudoData);
        }
        return accountNumbers;
    }

    private int putRandomMoneyOnDeposit(int[] accountNumbers, int limit) {
        int total = 0;
        Random generator = new Random(System.nanoTime());
        for (int i = 0; i < accountNumbers.length; i++) {
            int moneyOnDeposit = generator.nextInt(limit);
            bank.deposit(accountNumbers[i], moneyOnDeposit);
            total += moneyOnDeposit;
        }
        return total;
    }

    private Thread[] startNewThreads(int[] accountNumbers,
                                     int numberOfThreads, int limit) {
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                Random generator = new Random(System.nanoTime());
                int moneyToTransfer = generator.nextInt(limit);
                int accountNumberFrom = accountNumbers[generator
                        .nextInt(accountNumbers.length)];
                int accountNumberTo = accountNumbers[generator
                        .nextInt(accountNumbers.length)];
                bank.transfer(accountNumberFrom, accountNumberTo,
                        moneyToTransfer);
            });
        }
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].start();
        }
        return threads;
    }

    private int countTotalMoney(int[] accountNumbers) {
        int total = 0;

        for (int i = 0; i < accountNumbers.length; i++) {
            total += bank.checkBalance(accountNumbers[i]);
        }

        return total;
    }

}
