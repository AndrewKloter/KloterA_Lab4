package edu.temple.cis.c3238.banksim;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 */
public class BankSimMain {

    public static final int NACCOUNTS = 10;
    public static final int INITIAL_BALANCE = 10000;
    //public static final int TestingThread = 1;

    public static void main(String[] args) throws InterruptedException {
        Bank b = new Bank(NACCOUNTS, INITIAL_BALANCE);
        Thread[] threads = new Thread[NACCOUNTS];
        Thread testThread = new Thread();
        
        // Start a thread for each account
        for (int i = 0; i < NACCOUNTS; i++) {
            threads[i] = new TransferThread(b, i, INITIAL_BALANCE);
            //testThread = new TestThread();
            threads[i].start();
            //testThread.start();
        }
        testThread = new TestThread();
        //System.out.println("Between testThread = new,  and testThread.start()");
        testThread.start();
        
        // Wait for all threads to finish
        for (int i = 0; i < NACCOUNTS; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {
                // Ignore this
            }
        }
        b.test();
    }
}


