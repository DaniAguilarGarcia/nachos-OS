package nachos.threads;

import nachos.machine.*;

import java.util.*; 

//Akeem Lake



/**
 * A scheduler that chooses threads based on their priorities.
 *
 * <p>
 * A priority scheduler associates a priority with each thread. The next thread
 * to be dequeued is always a thread with priority no less than any other
 * waiting thread's priority. Like a round-robin scheduler, the thread that is
 * dequeued is, among all the threads of the same (highest) priority, the
 * thread that has been waiting longest.
 *
 * <p>
 * Essentially, a priority scheduler gives access in a round-robin fassion to
 * all the highest-priority threads, and ignores all other threads. This has
 * the potential to
 * starve a thread if there's always a thread waiting with higher priority.
 *
 * <p>
 * A priority scheduler must partially solve the priority inversion problem; in
 * particular, priority must be donated through locks, and through joins.
 */
public class PriorityScheduler extends Scheduler {
    /**
     * Allocate a new priority scheduler.
     */
    public PriorityScheduler() {
    }

    /**
     * Allocate a new priority thread queue.
     *
     * @param	transferPriority	<tt>true</tt> if this queue should
     *					transfer priority from waiting threads
     *					to the owning thread.
     * @return	a new priority thread queue.
     */
    public ThreadQueue newThreadQueue(boolean transferPriority) {
	return new PriorityQueue(transferPriority);
    }

    public int getPriority(KThread thread) {
	Lib.assertTrue(Machine.interrupt().disabled());

	return getThreadState(thread).getPriority();
    }

    public int getEffectivePriority(KThread thread) {
	Lib.assertTrue(Machine.interrupt().disabled());

	return getThreadState(thread).getEffectivePriority();
    }

    public void setPriority(KThread thread, int priority) {
	Lib.assertTrue(Machine.interrupt().disabled());

	Lib.assertTrue(priority >= priorityMinimum &&
		   priority <= priorityMaximum);

	getThreadState(thread).setPriority(priority);
    }

    public boolean increasePriority() {
	boolean intStatus = Machine.interrupt().disable();

	KThread thread = KThread.currentThread();

	int priority = getPriority(thread);
	if (priority == priorityMaximum)
	    return false;

	setPriority(thread, priority+1);

	Machine.interrupt().restore(intStatus);
	return true;
    }

    public boolean decreasePriority() {
	boolean intStatus = Machine.interrupt().disable();

	KThread thread = KThread.currentThread();

	int priority = getPriority(thread);
	if (priority == priorityMinimum)
	    return false;

	setPriority(thread, priority-1);

	Machine.interrupt().restore(intStatus);
	return true;
    }

    /**
     * The default priority for a new thread. Do not change this value.
     */
    public static final int priorityDefault = 1;
    /**
     * The minimum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMinimum = 0;
    /**
     * The maximum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMaximum = 7;

    /**
     * Return the scheduling state of the specified thread.
     *
     * @param	thread	the thread whose scheduling state to return.
     * @return	the scheduling state of the specified thread.
     */
    protected ThreadState getThreadState(KThread thread) {
	if (thread.schedulingState == null)
	    thread.schedulingState = new ThreadState(thread);

	return (ThreadState) thread.schedulingState;
    }

    /**
     * A <tt>ThreadQueue</tt> that sorts threads by priority.
     */
    protected class PriorityQueue extends ThreadQueue {
	PriorityQueue(boolean transferPriority) {
	    this.transferPriority = transferPriority;
	}

	public void waitForAccess(KThread thread) {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    getThreadState(thread).waitForAccess(this);
	}

	public void acquire(KThread thread) {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    getThreadState(thread).acquire(this);

	    //ADDED THIS START

	    ThreadState state = getThreadState(thread);

	    /*if I have a holder and I transfer priority
	    remove myself from the holder's resource list */
	    if(this.holder != null && this.transferPriority){
	    	this.holder.reslist.remove(this);
	    }

	    this.holder = state;

	    state.acquire(this);
	} //ADDED THIS END

	public KThread nextThread() {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    // implement me
	    //return null

	    //ADDED THIS START
	    if(waitQueue.isEmpty()){
	    return null;
	    }

	    if(this.holder != null && this.transferPriority)
	    {
	    	this.holder.reslist.remove(this);
	    }

	    KThread firstThread = pickNextThread();
	    if(firstThread != null){
	    	waitQueue.remove(firstThread);
	    	getThreadState(firstThread).acquire(this);
	    }
	    return firstThread;
	} //ADDED THIS END


	/**
	 * Return the next thread that <tt>nextThread()</tt> would return,
	 * without modifying the state of this queue.
	 *
	 * @return	the next thread that <tt>nextThread()</tt> would
	 *		return.
	 */
	protected KThread pickNextThread() {
	    // implement me
	    //return null;

		KThread nextThread = null; //Added Start


		for(Iterator<KThread> ts = waitQueue.iterator(); ts.hasNext();){
			KThread thread = ts.next();
			int priority = getThreadState(thread).getEffectivePriority();

			if(nextThread == null || priority > getThreadState(nextThread).getEffectivePriority()){
				nextThread = thread;
			}
		}


		return nextThread;
	}


	public int getEffectivePriority(){ //ADDED START
		
		// return minimum priority, if do not transfer priority.
		if(transferPriority == false){
			
			return priorityMinimum;
		}

		if(dirty){
			effectivePriority = priorityMinimum;
			for(Iterator<KThread> it = waitQueue.iterator(); it.hasNext();){
				KThread thread = it.next();
				int priority = getThreadState(thread).getEffectivePriority();
				if(priority > effectivePriority){
					effectivePriority = priority;
				}
			}
			dirty = false;
		}
		return effectivePriority;
	} //ADDED END

	public void setDirty(){ //ADDED START
		if(transferPriority == false){
			return;
		}

		dirty = true;

		if(holder != null){
			holder.setDirty();
		}
	} //ADDED END

	public void print() {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    // implement me (if you want)

	    for (Iterator<KThread> it = waitQueue.iterator(); it.hasNext();){ //ADDED STARTED HERE
	    	KThread currentThread = it.next();
	    	int priority = getThreadState(currentThread).getPriority();

	    	System.out.print("Thread: " + currentThread
	    			+ "\t Priority: " + priority + "\n");
	    } //ADDED END HERE
	}

	/**
	 * <tt>true</tt> if this queue should transfer priority from waiting
	 * threads to the owning thread.
	 */
	public boolean transferPriority;

	//ADDED END HERE

	//queue waiting on this resource
	private LinkedList<KThread> waitQueue = new LinkedList<KThread>(); //AL ADDED

	//threadstate corresponds to the holder of the resource
	private ThreadState holder = null;

	//Set to true when a new thread is added to the queue
	//or any of the queues in the waitQueue flag themselves as dirty
	private boolean dirty;

	//The cached highest of the effective priorities in the waitQueue.
	//This value is invalidated while dirty is true
	private int effectivePriority;
    } //ADDED END

    /**
     * The scheduling state of a thread. This should include the thread's
     * priority, its effective priority, any objects it owns, and the queue
     * it's waiting for, if any.
     *
     * @see	nachos.threads.KThread#schedulingState
     */
    protected class ThreadState {
	/**
	 * Allocate a new <tt>ThreadState</tt> object and associate it with the
	 * specified thread.
	 *
	 * @param	thread	the thread this state belongs to.
	 */
	public ThreadState(KThread thread) {
	    this.thread = thread;

	    setPriority(priorityDefault);
	}

	/**
	 * Return the priority of the associated thread.
	 *
	 * @return	the priority of the associated thread.
	 */
	public int getPriority() {
	    return priority;
	}

	/**
	 * Return the effective priority of the associated thread.
	 *
	 * @return	the effective priority of the associated thread.
	 */
	public int getEffectivePriority() {
	    // implement me

		int maxEffective = this.priority; //FROM HERE - ADDED

		if(dirty){
			for(Iterator<ThreadQueue> it = reslist.iterator(); it.hasNext();){
				PriorityQueue pg = (PriorityQueue)(it.next());
				int effective = pg.getEffectivePriority();
				if(maxEffective < effective){
					maxEffective = effective;
				}
			}
		}
		//return priority;
		return maxEffective; //TO HERE -ADDED
	}

	/**
	 * Set the priority of the associated thread to the specified value.
	 *
	 * @param	priority	the new priority.
	 */
	public void setPriority(int priority) {
	    if (this.priority == priority)
		return;

	    this.priority = priority;

	    // implement me
	    setDirty(); //AL ADDED
	}

	/**
	 * Called when <tt>waitForAccess(thread)</tt> (where <tt>thread</tt> is
	 * the associated thread) is invoked on the specified priority queue.
	 * The associated thread is therefore waiting for access to the
	 * resource guarded by <tt>waitQueue</tt>. This method is only called
	 * if the associated thread cannot immediately obtain access.
	 *
	 * @param	waitQueue	the queue that the associated thread is
	 *				now waiting on.
	 *
	 * @see	nachos.threads.ThreadQueue#waitForAccess
	 */
	public void waitForAccess(PriorityQueue waitQueue) {
	    // implement me

		Lib.assertTrue(Machine.interrupt().disabled()); //ADDED START
		Lib.assertTrue(waitQueue.waitQueue.indexOf(thread) == -1);

		waitQueue.waitQueue.add(thread);
	  waitQueue.setDirty();

	  //set waitingOn
  	waitingOn = waitQueue;

	  //if the waitQueue was previously in reslist, remove it
	  //and set its holder to null

	  if(reslist.indexOf(waitQueue) != -1){
		reslist.remove(waitQueue);
		waitQueue.holder = null;
	    } //ADDED END

	}

	/**
	 * Called when the associated thread has acquired access to whatever is
	 * guarded by <tt>waitQueue</tt>. This can occur either as a result of
	 * <tt>acquire(thread)</tt> being invoked on <tt>waitQueue</tt> (where
	 * <tt>thread</tt> is the associated thread), or as a result of
	 * <tt>nextThread()</tt> being invoked on <tt>waitQueue</tt>.
	 *
	 * @see	nachos.threads.ThreadQueue#acquire
	 * @see	nachos.threads.ThreadQueue#nextThread
	 */
	public void acquire(PriorityQueue waitQueue) {
		
	   //Lib.assertTrue(waitQueue.waitQueue.isEmpty()); //AL
		
		//implement me
		Lib.assertTrue(Machine.interrupt().disabled()); //ADDED
		 

		//add Queue to resource list
		reslist.add(waitQueue); //ADDED

		// if Queue is waiting, clean
		if (waitQueue == waitingOn){ //ADDED
			waitingOn = null; //ADDED
		}
    //set dirty flag
		setDirty(); //ADDED
	}


	public void setDirty(){ //ADDED START
		if(dirty){
			return;
		}

		dirty = true;

		PriorityQueue pg = (PriorityQueue) waitingOn;
		if(pg != null){
			pg.setDirty();
		}
	} //ADDED END




	/** The thread with which this object is associated. */
	protected KThread thread;
	/** The priority of the associated thread. */
	protected int priority;

	protected int effectivePriority; //ADDED

	protected LinkedList<ThreadQueue> reslist = new LinkedList<ThreadQueue>(); //ADDED

	protected ThreadQueue waitingOn; //ADDED

	private boolean dirty = false; //ADDED


    }
}
