Group Members and Student IDs:
Daniela Aguilar (6089603), Akeem Lake(6136179), Nivaldo DeMello (2132181),Michael Gonzalez(1584041).

Accomplishments per task(and credits):

Task 1:

(20%, 5 lines) Implement KThread.join(). Note that another thread does not have to call join(), but if it is called, it must be called only once. The result of calling join() a second time on the same thread is undefined, even if the second caller is a different thread than the first caller. A thread must finish executing normally whether or not it is joined.  

-This one basically involved using a LinkedList and integer variable to use for our threads. The LinkedList is created when KThread() is called. It is used in the join() function and adds the currentThread to the list. It keeps them there until finish() is called which will remove the first thread until complete. 

Credits:
Michael Gonzalez: Ran nachos with more than one PingTest to see how output would be affected.
Daniela Aguilar: Tested and manipulated different codes to find the best one to submit. 
Nivaldo DeMello: Updated join() with additional code. Updated finish() with the call to join().
Akeem Lake: Implemented lines to add current thread to join queue and sleep current thread 

Task 2: 

(20%, 20 lines) Implement condition variables directly, by using interrupt enable and disable to provide atomicity. We have provided a sample implementation that uses semaphores; your job is to provide an equivalent implementation without directly using semaphores (you may of course still use locks, even though they indirectly use semaphores). Once you are done, you will have two alternative implementations that provide the exact same functionality. Your second implementation of condition variables must reside in class nachos.threads.Condition2.  

-For this task, the Semaphore class was heavily used. It provided the basic structure but the P() and V() functions were replaced by wake() and sleep(). These functions used release() and acquire() instead which was realized from the original Condition class implementation.

Credits:
Michael Gonzalez: The semaphore class was what drew inspiration for changes however the functions were slightly different for Locks.
Daniela Aguilar:  Implemented Semaphore class, P() and V() were replace by wake() and sleep()
Nivaldo DeMello: Updated Condition2.java with code for interrupting without semaphores.
Akeem Lake: implemented lines in sleep method which add thread to wait queue, make current thread sleep  

Task 3:

(20%, 40 lines) Complete the implementation of the Alarm class, by implementing the waitUntil(long x) method. A thread calls waitUntil to suspend its own execution until time has advanced to at least now + x. This is useful for threads that operate in real-time, for example, for blinking the cursor once per second. There is no requirement that threads start running immediately after waking up; just put them on the ready queue in the timer interrupt handler after they have waited for at least the right amount of time. Do not fork any additional threads to implement waitUntil(); you need only modify waitUntil() and the timer interrupt handler. waitUntil is not limited to one thread; any number of threads may call it and be suspended at any one time.  

Test: add Alarm.selfTest() to selfTest() of ThreadedKernel.java file 

Credits:
Michael Gonzalez: A delay time was used to help complete the implementation of the Alarm class.
Daniela Aguilar: Implemented waitUntil(long x) method. 
Nivaldo DeMello: Updated Alarm.java to wait x amount of time	
Akeem Lake: Updated timeInterrupt to check if threads in waiting queue are ready then put threads into ready queue 

Task 4: 

(20%, 40 lines) Implement synchronous send and receive of one word messages (also known as Ada-style rendezvous), using condition variables (don't use semaphores!). Implement the Communicator class with operations, void speak(int word) and int listen(). speak() atomically waits until listen() is called on the same Communicator object, and then transfers the word over to listen(). Once the transfer is made, both can return. Similarly, listen() waits until speak() is called, at which point the transfer is made, and both can return (listen() returns the word). Your solution should work even if there are multiple speakers and listeners for the same Communicator (note: this is equivalent to a zero-length bounded buffer; since the buffer has no room, the producer and consumer must interact directly, requiring that they wait for one another). Each communicator should only use exactly one lock. If you're using more than one lock, you're making things too complicated.  

-For task 4, it required just following the text above to be able to track the necessary calls. One Lock was used alongside 2 Condition variables to keep track of speaker and listener. Thanks for task 2, we used Lock in a similar way.

Credits:
Michael Gonzalez: For this test, I created randomWord and delays to test between listeners and speakers. I use an Alarm to implement the delay. The speaker will "speak" their word (random int) and a listener will "listen" for that word. Depending on when finished, the listener or speaker will exit.
Daniela Aguilar:Tested my teamate work.
Nivaldo DeMello: Updated Communicator class
Akeem Lake: added lines into listen and speak methods

Task 5: 

(30%, 125 lines) Implement priority scheduling in Nachos by completing the PriorityScheduler class. Priority scheduling is a key building block in real-time systems. Note that in order to use your priority scheduler, you will need to change a line in nachos.conf that specifies the scheduler class to use. The ThreadedKernel.scheduler key is initially equal to nachos.threads.RoundRobinScheduler. You need to change this to nachos.threads.PriorityScheduler when you're ready to run Nachos with priority scheduling. Note that all scheduler classes extend the abstract class nachos.threads.Scheduler. You must implement the methods getPriority(), getEffectivePriority(), and setPriority(). You may optionally also implement increasePriority() and decreasePriority() (these are not required). In choosing which thread to dequeue, the scheduler should always choose a thread of the highest effective priority. If multiple threads with the same highest priority are waiting, the scheduler should choose the one that has been waiting in the queue the longest. An issue with priority scheduling is priority inversion. If a high priority thread needs to wait for a low priority thread (for instance, for a lock held by a low priority thread), and another high priority thread is on the ready list, then the high priority thread will never get the CPU because the low priority thread will not get any CPU time. A partial fix for this problem is to have the waiting thread donate its priority to the low priority thread while it is holding the lock. Implement the priority scheduler so that it donates priority, where possible. Be sure to implement Scheduler.getEffectivePriority(), which returns the priority of a thread after taking into account all the donations it is receiving. Note that while solving the priority donation problem, you will find a point where you can easily calculate the effective priority for a thread, but this calculation takes a long time. To receive full credit for the design aspect of this project, you need to speed this up by caching the effective priority and only recalculating a thread's effective priority when it is possible for it to change. It is important that you do not break the abstraction barriers while doing this part -- the Lock class does not need to be modified. Priority donation should be accomplished by creating a subclass of ThreadQueue that will accomplish priority donation when used with the existing Lock class, and still work correctly when used with the existing Semaphore and Condition classes.

Test: Prioritycheduler.selfTest(); to selfTest() of ThreadedKernel.java file 

Credits:
Michael Gonzalez: The priority handling was confusing to implement due to priority being needed to be passed and adjustment in nachos.conf requiring a switch.
Daniela Aguilar: Tested the code my friends built before submitting 
Nivaldo DeMello: Updated PriorityScheduler class	
Akeem Lake: Implemented Acquire and nextThread method 
