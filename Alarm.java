package nachos.threads;

import java.util.PriorityQueue;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
    	long currTime = Machine.timer().getTime();
    	boolean intStatus = Machine.interrupt().disable();
    	/**
    	 * Place them on the ready queue in the timer interrupt handler after they 
    	 *have waited for some time. This will wake threads
    	 */
    	while(!queueWait.isEmpty() && queueWait.peek().wakeTime <= currTime){
    		threadTime tempTime = queueWait.poll();
    		KThread thread = tempTime.thread;
    		if(thread != null){
    			thread.ready();
    		}
    	}
    	
    	KThread.yield();
    	Machine.interrupt().restore(intStatus);
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
	long wakeTime = Machine.timer().getTime() + x;
	KThread thread = KThread.currentThread();
	
	threadTime tempTime = new threadTime();
	
	boolean intStatus = Machine.interrupt().disable();
	
	//Place new ThreadTime into queueWait while it checks its waketime
	queueWait.add(tempTime);
	
	thread.sleep();
	
	Machine.interrupt().restore(intStatus);
	
	while (wakeTime > Machine.timer().getTime())
	    KThread.yield();
    }
    /*
     * New class to compare time
     */
    private class threadTime implements Comparable<threadTime>{
    	
    	private KThread thread;
    	private long wakeTime;
    	
    	//similar to KThread implementation
    	public int compareTo(threadTime threadTime){
    		if(wakeTime > threadTime.wakeTime){
    			return 1;
    		} else if(wakeTime < threadTime.wakeTime){
    			return -1;
    		} else {
    			return 0;
    		}
    	}
    	
    }
    //This is used to track when threads wake
    private PriorityQueue<threadTime> queueWait = new PriorityQueue<threadTime>();
}
