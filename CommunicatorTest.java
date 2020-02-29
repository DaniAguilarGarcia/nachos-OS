package nachos.threads;

import nachos.machine.*;

import java.util.Random;

/**
 * A Tester for the Communicator class
 */
public class CommunicatorTest {


    private static class CommTest implements Runnable {

        /* Constructor */
        CommTest(String name, Communicator comm, boolean isSpeaker,
                int howMany, Communicator commFinished, Random rng) {
            this.name = name;
            this.comm = comm;
            this.isSpeaker = isSpeaker;
            this.howMany = howMany;
            this.commFinished = commFinished;
            this.rng = rng;
        }

        /** Generate a random number of ticks that
         * needs to be spent waiting before attempting a 
         * speak/listen actions. The number of ticks is
         * generated to be between minDelay and maxDelay
         */
        private int randomDelay() {
            return minDelay+rng.nextInt(1+maxDelay - minDelay)-1; 
        }

        /** Generate a random word that a speaker
         * will speak through the Communicator.
         */
        private int randomWord() {
            return rng.nextInt(50); /* between 0 and 50 */
        }

        /** run() method for the CommTest. This method loops howMany
         * times. During this loop the thread waits some random
         * amount of time, then speaks or listen, depending on
         * whether this.isSpeaker is sets to true or false.
         */
        public void run() {

            System.out.println("** "+name+" begins.");

            /* Main loop  */
            for (int i=0; i < howMany; i++) {
                /* Sleep for some random delay */
                int randomDelay = randomDelay();
                System.out.println("** "+name+": Sleeping for "+randomDelay+
                        " (i.e., until time="+
                        (randomDelay+Machine.timer().getTime())+")");
                ThreadedKernel.alarm.waitUntil(randomDelay);
                System.out.println("** "+name+": Done sleeping! (time="+
                        Machine.timer().getTime()+")");

                if (isSpeaker) {
                    /* I am a speaker and I speak my word */
                    int randomWord = randomWord();
                    System.out.println("** "+name+": Speaking "+randomWord+
                            " (time="+Machine.timer().getTime()+")");
                    comm.speak(randomWord);
                    System.out.println("** "+name+": Spoke and Returned (time="+
                            Machine.timer().getTime()+")");
                } else {
                    /* I am a listener and I listen */
                    System.out.println("** "+name+": Listening (time="+
                            Machine.timer().getTime()+")");
                    int word = comm.listen();
                    System.out.println("** "+name+": Listened and got "+word+
                            " (time="+Machine.timer().getTime()+")");
                }
            }

            /* Exits and signals it to the main thread */
            commFinished.speak(-1);
            System.out.println("** "+name+" exits.");
        }

        /* My name */
        private String name;
        /* The Communicator for speaking/listening */
        private Communicator comm;
        /* True if I am a speaker, false if I am a listener */
        private boolean isSpeaker;
        /* The number of iterations */
        private int howMany;
        /* The Communicator for signaling that I am done */
        private Communicator commFinished;
        /* Random number generator */
        private Random rng;
    }

    /**
     * Tests whether this module is working.
     */
    public static void runTest() {
        System.out.println("**** Communicator testing begins ****");

        /* Create a random number generator */
        Random rng = new Random();

        /* Create the communicator on which CommTests communicate */
        Communicator comm = new Communicator();

        /* Create the communicator for listening to terminations */
        Communicator commFinished = new Communicator();

        /* Create rendezvous threads and fork them*/
        KThread rvs[] = new KThread[numCommTests];
        for (int i=0; i < numCommTests; i++) {
            if (i%2 == 0) {
                /* Creating a speaker */
                rvs[i] = new KThread(new CommTest("CommTest(speaker) #"+i,
                            comm,true,howMany,commFinished,rng));
                rvs[i].setName("CommTest(speaker) #"+i);
            } else {
                /* Creating a listener */
                rvs[i] = new KThread(new CommTest("CommTest(listener) #"+i,
                            comm,false,howMany,commFinished,rng));
                rvs[i].setName("CommTest(listener) #"+i);
            }
            /* fork() */
            rvs[i].fork();
        }

        /* Wait for all threads to signal that they're done,
         * which is done via a Communicator for good measures */
        for (int i=0; i < numCommTests; i++) {
            commFinished.listen();
            System.out.println("Acknowledged one thread exit.");
        }

        System.out.println("**** Communicator testing ends ****");

    }



    /* Number of Threads. Must be EVEN!! */
    private static final int numCommTests = 8;

    /* Number of RV actions per thread */
    private static final int howMany = 5;

    /* Bounds on delay between attempts to speak/listen */
    private static final int minDelay = 10000;
    private static final int maxDelay = 1000000;
}