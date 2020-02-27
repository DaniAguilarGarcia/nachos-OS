package nachos.threads;

import nachos.machine.*;

import java.util.TreeSet; //AL Added
import java.util.Iterator; //AL Added
import java.util.SortedSet; //AL Added


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
    	waiting = new TreeSet<WaitingThread>(); //AL Added
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
	//KThread.currentThread().yield();

	long time = Machine.timer().getTime(); // AL ADDED

	if(waiting.isEmpty()) //AL ADDED
		return; //AL ADDED

	if(((WaitingThread) waiting.first()).time > time) //AL ADDED
	    return;  //AL ADDED

	Lib.debug(dbgInt, "Invoking Alarm.timerInterrupt at time = " + time); //AL ADDED

	while(!waiting.isEmpty() && ((WaitingThread) waiting.first()).time <= time){//ADDED
		WaitingThread next = (WaitingThread) waiting.first(); //ADDED

		//ADDED - move due thread to waiting thread
		next.thread.ready(); //ADDED

		waiting.remove(next); //ADDED

		Lib.assertTrue(next.time <= time); //ADDED

		Lib.debug(dbgInt," " + next.thread.getName()); //ADDED
	}

	Lib.debug(dbgInt, " (end of Alarm.timerInterrupt)"); //ADDED
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
//---------------------------------------------------
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
	long wakeTime = Machine.timer().getTime() + x;

	//while (wakeTime > Machine.timer().getTime()) //included- might delete
	    //KThread.yield(); //included, might delete

	boolean intStatus = Machine.interrupt().disable(); //added-1
	//Lib.debug(dbgAlarm,"At " + (wakeTime-x) + " cycles, sleeping thread " + KThread.currentThread().toString() + "until" + wakeTime + " cycles");//added-2
	//set.add(new Pair(KThread.currentThread(), wakeTime));  //added-2
	WaitingThread toAlarm = new WaitingThread(wakeTime, KThread.currentThread()); //added

	Lib.debug(dbgInt, "Wait thread "+ KThread.currentThread().getName() + "until " +
	wakeTime); //added -1

	waiting.add(toAlarm); //added-1

	KThread.sleep(); //added -1
	Machine.interrupt().restore(intStatus); //Added -1
    }
//---------------------------------------------------------------------------------------------------

private static final char dbgInt = 'i'; //Added
private TreeSet<WaitingThread> waiting; //Added

//Added
private class WaitingThread implements Comparable{
	 WaitingThread(long time, KThread thread){
		 this.time = time;
		 this.thread = thread;
	 }

	 public int compareTo(Object o)
{
		 WaitingThread toOccur = (WaitingThread) o;

		 //can't return 0 for unequal objects, so check all fields
		 if(time < toOccur.time)
			 return -1;
		 else if(time > toOccur.time)
			 return 1;
		 else
			 return thread.compareTo(toOccur.thread);
}

	 long time;
	 KThread thread;
}

private static class AlarmTest implements Runnable {
	AlarmTest(long x){
		this.time = x;
	}

	public void run(){
		System.out.print(KThread.currentThread().getName()+ "alarm\n");
		ThreadedKernel.alarm.waitUntil(time);
		System.out.print(KThread.currentThread().getName()+ "woken up\n");
	}

	private long time;
}
//Added might delete later
/* public static void selfTest(){
	System.out.print("Enter Alarm.selfTest\n");

	Runnable r = new Runnable(){
		public void run(){
			KThread t[] = new KThread[10];

			for(int i = 0; i<10; i++){
				t[i] = new KThread(new AlarmTest(160+ i*20));
				t[i].setName("Thread" + i).fork();
			}
			for(int i=0; i<10000; i++){
				KThread.yield();
			}
		}
	};

	KThread t = new KThread(r);
	t.setName("Alarm SelfTest");
	t.fork();
	KThread.yield();

	t.join();

	System.out.print("Leave Alarm.selfTest\n");
} */

//ADDED START
public static void selfTest() {
KThread t1 = new KThread(new Runnable() {
public void run() {
long time1 = Machine.timer().getTime();
int waitTime = 10000;
System.out.println("Thread 1 calling wait at time:" + time1);
ThreadedKernel.alarm.waitUntil(waitTime);
System.out.println("Thread 1 woken up after:" + (Machine.timer().getTime() - time1));
Lib.assertTrue((Machine.timer().getTime() - time1) >= waitTime, " thread woke up too early.");

}
});
KThread t2 = new KThread(new Runnable() {
public void run() {
long time1 = Machine.timer().getTime();
int waitTime = 7000;
System.out.println("Thread 2 calling wait at time:" + time1);
ThreadedKernel.alarm.waitUntil(waitTime);
System.out.println("Thread 2 woken up after:" + (Machine.timer().getTime() - time1));
Lib.assertTrue((Machine.timer().getTime() - time1) >= waitTime, " thread woke up too early.");

}
});
t1.setName("T1");
t2.setName("T2");
t1.fork(); t2.fork();
t1.join(); t2.join();
}
//ADDED END


}
