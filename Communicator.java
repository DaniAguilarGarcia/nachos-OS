package nachos.threads;

import nachos.machine.*;

import java.util.*; //AL ADDED

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
    	this.isWordReady = false;  //AL ADDED
    	this.lock = new Lock();  //AL ADDED
    	
    	this.speakerCond = new Condition2(lock); //AL ADDED
    	this.listenerCond = new Condition2(lock); //AL ADDED
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
    	
    	lock.acquire(); //AL ADDED
    	speaker++; //AL ADDED
    	
    	//speaker acquires the lock //ADDED
    	
    	//System.out.print("Listener:" + listener + "\n"); //ADDED
    	
    	//while no available listener or word is ready(but listener hasn't fetched it) //ADDED
    	
    	while(isWordReady || listener == 0){ //AL ADDED
    		speakerCond.sleep(); //AL ADDED
    	}
    	
    	this.word = word; //AL ADDED
    	
    	isWordReady = true; //AL ADDED
    	
    	listenerCond.wakeAll(); //AL ADDED
    	
    	speaker--; //AL ADDED
    	
    	lock.release(); //AL ADDED
    	
    } 

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
	//return 0;
    	
    	//listener acquires the lock
    	lock.acquire(); //AL ADDED
    	//increase number of listener by one
    	listener++; //AL ADDED
    	
    	//System.out.print("Speaker: " + speaker + "\n");
    	
    	//at this point, don't know if any speaker is waiting, try to wake up all speakers
    	//if use speakerCond.wake() instead, it will hit exception at linkedlist.removeFirst because
    	//no element to be removed.
    	
    	//while word is not ready, listener goes to sleep 
    	while(isWordReady == false){ //AL ADDED
    		speakerCond.wakeAll(); //AL ADDED
    		listenerCond.sleep(); //AL ADDED
    	} //AL ADDED
    	
    	
    	//listener receives the word
    	int word = this.word; //AL ADDED
    	
    	//reset flag that word is invalid 
    	isWordReady = false; 
    	
    	//decrease listener number
    	listener--;
    	
    	lock.release();
    	
    	return word; 
    	
    }
   
    
    private int listener = 0;             // AL ADDED
    private int speaker  = 0;             // AL ADDED
    private int word = 0;                 // AL ADDED
    private boolean isWordReady;  //AL ADDED 
    
    private Lock lock;  //AL ADDED 
    private Condition2 speakerCond; //AL ADDED
    private Condition2 listenerCond; //AL ADDED
    
}
