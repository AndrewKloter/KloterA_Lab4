package edu.temple.cis.c3238.banksim;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 */
public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private boolean open;
    private int transactsCounter;
    private boolean testing=false;
    

    public Bank(int numAccounts, int initialBalance) {
        open = true;
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;
        transactsCounter = 0;
    }

    
    public synchronized void incTransacts() {
        transactsCounter++;
    }
    
    
    public synchronized void decTransacts() {
        transactsCounter--;
    }
    
   //READ LOCK for transfer threads, allows them all to execute concurrently, but block the test method.
    public void transfer(int from, int to, int amount) throws InterruptedException {
        accounts[from].waitForAvailableFunds(amount);
        /*
        synchronized(this) {
            while(testing) {
                System.out.println("Can't transfer funds while testing!");
                this.wait();
            }
        }
        */
        
        if (!open) return;
        if (accounts[from].withdraw(amount)) {
            incTransacts(); //We are withdrawing from an account, which is the beginning of a transaction.
            accounts[to].deposit(amount);
            decTransacts(); //We are depositing to an account, which is the end of a transaction.
        }
        if (shouldTest()) test();
        
        synchronized(this) {
            this.notifyAll();
        }
    }
    
//WRITE LOCK for test method, which will block all transfers. Write lock is exclusive, only 1.
    public synchronized void test() throws InterruptedException {
        int sum = 0;
        testing = true;
        while(transactsCounter != 0) {
            System.out.println("Can't test until all transactions are finished!");
            wait();
        }
        
        for (Account account : accounts) {
            System.out.printf("%s %s%n", 
                    Thread.currentThread().toString(), account.toString());
            sum += account.getBalance();
        }
        System.out.println(Thread.currentThread().toString() + 
                " Sum: " + sum);
        if (sum != numAccounts * initialBalance) {
            System.out.println(Thread.currentThread().toString() + 
                    " Money was gained or lost");
            System.exit(1);
        } else {
            System.out.println(Thread.currentThread().toString() + 
                    " The bank is in balance");
        }
        testing = false;
        notifyAll();
    }

    public int size() {
        return accounts.length;
    }
    
    public synchronized boolean isOpen() {return open;}
    
    public void closeBank() {
        synchronized (this) {
            open = false;
        }
        for (Account account : accounts) {
            synchronized(account) {
                account.notifyAll();
            }
        }
    }
    
    public synchronized boolean shouldTest() {
        return ++ntransacts % NTEST == 0;
    }

}
    
    /*
    public void transfer(int from, int to, int amount) throws InterruptedException {
        accounts[from].waitForAvailableFunds(amount);
     
        if (!open) return;
        if (accounts[from].withdraw(amount)) {
            incTransacts(); //We are withdrawing from an account, which is the beginning of a transaction.
            accounts[to].deposit(amount);
            decTransacts(); //We are depositing to an account, which is the end of a transaction.
        }
        if (shouldTest()) test(); 
    }
*/