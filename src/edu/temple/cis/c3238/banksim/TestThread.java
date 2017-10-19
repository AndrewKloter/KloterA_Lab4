/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cis.c3238.banksim;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author tuf63516
 */
class TestThread {

    public Bank bank;

    //@override
    public void run() {
        
        try {
    bank.test();
    }
    catch (InterruptedException ex) {
    }
}
    
}
