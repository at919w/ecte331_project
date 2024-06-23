package projectB; // Defines the package where this class is located 

public class TaskB { // Definition of the Public class TaskB
    public static void main(String[] args) { // Main method within class TaskB, where program execution starts
        
        Data my_sample = new Data(); // Creating an instance of the Data class
        int test_size = 100; // Setting the Integer variable test_size with value 100

        for (int i = 0; i < test_size; i++) { // Make a for loop that runs the number of times equal to test_size value
            System.out.println("\n" + "Loop " + (i+1) + "\n" + "--------" ); // Printing the Loop Number to track the number of Iterations run

            // Setting Flags as false (Resetting)
            my_sample.goFunB2 = false;
            my_sample.goFunA2 = false;
            my_sample.goFunB3 = false;
            my_sample.goFunA3 = false;

            // Creating instances of ThreadA and ThreadB with the Data instance created at the beginning
            ThreadA ta = new ThreadA(my_sample);
            ThreadB tb = new ThreadB(my_sample);

            ta.start(); // Starting ThreadA
            tb.start(); // Starting ThreadB
            try {  //try and catch error method
                ta.join(); // Wait for ThreadA to complete
                tb.join(); // Wait for ThreadB to complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

class Data { // Definition of the Data class, that stores variables and flags for the full program
    
    int A1, A2, A3, B1, B2, B3; // Integer variable
    
    // Declaring boolean flags, which are the functions given in the Project Manual
    boolean goFunA1 = false;
    boolean goFunA2 = false;
    boolean goFunA3 = false;
    boolean goFunB1 = false;
    boolean goFunB2 = false;
    boolean goFunB3 = false;
}

class ThreadA extends Thread { // Definition of ThreadA subclass extending Thread
    private Data sample; // Private member variable of type Data

    public ThreadA(Data sample) { // Constructor initializing ThreadA with a Data instance
        this.sample = sample;
    }

    public void run() { // Method executed when ThreadA starts
        synchronized (sample) { // Synchronizing on the Data instance
            sample.A1 = sum(0, 500); // Computing and storing result in A1 as per the instruction from guidelines
            sample.goFunB2 = true; // Setting a flag to indicate completion, Function B2 is turned to true
            sample.notifyAll(); // Notifying waiting threads
            System.out.println("A1: " + sample.A1); // Printing result of A1 samples
        }

        synchronized (sample) { // Synchronizing again on the Data instance
            while (!sample.goFunA2) { // Waiting until a specific condition is met, while the value of sample.goFunA2 to False
                try { //try and catch error method
                    sample.wait(); // Releasing the lock and waiting
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sample.A2 = sample.B2 + sum(0, 300); // Computing and storing result in A2 as per the instruction from guidelines
            sample.goFunB3 = true; // Setting a flag to indicate completion, Function B3 is turned to true
            sample.notifyAll(); // Notifying waiting threads
            System.out.println("A2: " + sample.A2); // Printing result of A2 samples
        }

        synchronized (sample) { // Synchronizing once more
            while (!sample.goFunA3) { // Waiting until another specific condition is met, while the value of sample.goFunA3 to False
                try { //try and catch error method
                    sample.wait(); // Releasing the lock and waiting
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sample.A3 = sample.B3 + sum(0, 400); // Computing and storing result in A3 as per the instruction from guidelines
            System.out.println("A3: " + sample.A3); // Printing result f A3 samples
        }
    }

    private int sum(int start, int end) { //Calculating the sum of numbers from start to end
        int sum = 0; // Sum variable is Set to 0
        for (int i = start; i <= end; i++) { // Loop to compute sum, the loop is set to run i number of times and iterate the i value 
            sum += i; // Accumulation sum
        }
        return sum; // Return the computed value of integer variable sum
    }
}

class ThreadB extends Thread { // Definition of ThreadB subclass extending Thread
    private Data sample; // Private member variable of type Data

    public ThreadB(Data sample) { // Constructor initializing ThreadB with a Data instance
        this.sample = sample;
    }

    public void run() { // Method executed when ThreadB starts
        synchronized (sample) { // Synchronizing on the Data instance
            sample.B1 = sum(0, 250); // Computing and storing result in B1
            sample.goFunA2 = true; // Setting a flag to indicate completion
            sample.notifyAll(); // Notifying waiting threads
            System.out.println("B1: " + sample.B1); // Printing result
        }

        synchronized (sample) { // Synchronizing again on the Data instance
            while (!sample.goFunB2) { // Waiting until a specific condition is met
                try {
                    sample.wait(); // Releasing the lock and waiting
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sample.B2 = sample.A1 + sum(0, 200); // Computing and storing result in B2
            sample.goFunA2 = true; // Setting a flag to indicate completion
            sample.notifyAll(); // Notifying waiting threads
            System.out.println("B2: " + sample.B2); // Printing result
        }

        synchronized (sample) { // Synchronizing once more
            while (!sample.goFunB3) { // Waiting until another specific condition is met
                try {
                    sample.wait(); // Releasing the lock and waiting
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sample.B3 = sample.A2 + sum(0, 400); // Computing and storing result in B3
            sample.goFunA3 = true; // Setting a flag to indicate completion
            sample.notifyAll(); // Notifying waiting threads
            System.out.println("B3: " + sample.B3); // Printing result
        }
    }

    private int sum(int start, int end) { // Method to compute sum of numbers from start to end
        int sum = 0; // Initializing sum variable
        for (int i = start; i <= end; i++) { // Loop to compute sum
            sum += i; // Accumulating sum
        }
        return sum; // Returning computed sum
    }
}
