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
    	locker = new Lock();
    	speaker = new Condition(locker);
    	listener = new Condition(locker);
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
    	locker.acquire();
    	numSpeakers += 1;
    	while(numListeners == 0){
    		listener.sleep();
    	}
    	numListeners -= 1;
    	messages = word;
    	speaker.wake();
    	locker.release();    	
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
    	locker.acquire();
    	numListeners += 1;
    	listener.wake();
    	while(numSpeakers == 0){
    		speaker.sleep();
    	}
    	numSpeakers -= 1;
    	messageHold = messages;
    	locker.release();
    	
    	return messageHold;
    }
    
    public static void selfTest() {
        CommunicatorTest.runTest();
    }
    
    private Condition speaker;
    private Condition listener;
    private int numSpeakers = 0;
    private int numListeners = 0;
    private int messages = 0;
    private int messageHold = 0;
    private Lock locker;
}
