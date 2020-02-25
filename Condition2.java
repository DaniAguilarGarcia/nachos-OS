package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 * This Condition class emulates Condition.java using Lock
 * instead of Semaphore.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
	
	
    public Condition2(Lock conditionLock) {
	this.conditionLock = conditionLock;
	// Initiate a linkedlist to track our conditionlocks
	waitQueue = new LinkedList<Lock>();
	}
    

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
//	Lock variable to simulate Semaphore class in Threads
	Lock waiter = new Lock();
//  add our new Lock condition to list
	waitQueue.add(waiter);
	
	conditionLock.release();
	waiter.acquire();
	conditionLock.acquire();
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	
	if (!waitQueue.isEmpty())
		//release first Lock  
	    ((Lock) waitQueue.removeFirst()).release();
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	
	while (!waitQueue.isEmpty())
	    wake();
    }
    /*
     * 
     * waitQueue is to place our Locks in their order they arrive
     */
    private Lock conditionLock;
    private LinkedList<Lock> waitQueue;
}
