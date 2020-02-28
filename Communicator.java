package nachos.threads;

import nachos.machine.*;

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
    	this.listenQueue = ThreadedKernel.scheduler.newThreadQueue(true);
    	this.speakQueue = ThreadedKernel.scheduler.newThreadQueue(true);
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
    	boolean intStatus = Machine.interrupt().disable();
    	
    	KThread thread = listenQueue.nextThread();
    	
    	while (thread == null) {
    		speakQueue.waitForAccess(KThread.currentThread());
    		
    		KThread.sleep();
    		
    		thread = listenQueue.nextThread();
    	}
    	
    	wordSent = word;
    	
    	Machine.interrupt().restore(intStatus);    	
    	
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
    	boolean intStatus = Machine.interrupt().disable();
    	
    	KThread thread = speakQueue.nextThread();
    	
    	listenQueue.waitForAccess(KThread.currentThread());
    	
    	if (thread != null) {
    		thread.ready();
    	}
    	
    	KThread.sleep();
    	
    	Machine.interrupt().restore(intStatus);
    	
    	
    	return wordSent;
    }

    private int wordSent;
    private ThreadQueue listenQueue;
    private ThreadQueue speakQueue;    
}

