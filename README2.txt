Group Members and Student IDs: Daniela Aguilar (6089603), Akeem, Nivaldo DeMello,M.

 Accomplishments per task(and credits):

Task 1:

(20%, 5 lines) Implement KThread.join(). Note that another thread does not have to call join(), but if it is called, it must be called only once. The result of calling join() a second time on the same thread is undefined, even if the second caller is a different thread than the first caller. A thread must finish executing normally whether or not it is joined.  

-This one basically involved using a LinkedList and integer variable to use for our threads. The LinkedList is created when KThread() is called. It is used in the join() function and adds the currentThread to the list. It keeps them there until finish() is called which will remove the first thread until complete. 

Credits:
M:
Daniela Aguilar: Tested and manipulated different codes to find the best one to submit. 
Nivaldo DeMello:	Updated join() with additional code. Updated finish() with the call to join().
Akeem:
