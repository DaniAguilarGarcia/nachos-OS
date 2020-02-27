package nachos.threads;

import nachos.machine.*;

import java.util.*;

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
    	MyThread myThread = priorityQ.peek();
    	
    	while (myThread != null && myThread.getWakeTime() <= currTime) {
    		priorityQ.poll();
    		myThread.getThread().ready();    		
    	}
    	
    	KThread.currentThread().yield();
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
		
		boolean intStatus = Machine.interrupt().disable();
		
		
		if (wakeTime == Machine.timer().getTime()) {
			priorityQ.add(new MyThread(thread, wakeTime));		
			thread.sleep();
		}
		
		Machine.interrupt().enable();
		Machine.interrupt().restore(intStatus);
		
		timerInterrupt();
    }
    
    private class MyThread {
    	private KThread thr = null;
    	private long wakeTime = 0;
    	
    	public MyThread(KThread thr, long wakeTime) {
    		this.thr = thr;
    		this.wakeTime = wakeTime;
    	}
    	
    	public long getWakeTime() {
    		return wakeTime;
    	}
    	
    	public KThread getThread() {
    		return thr;
    	}
    }
    
    private PriorityQueue<MyThread> priorityQ = new PriorityQueue<>();
}

