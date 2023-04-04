import java.util.Random;

public class main {

    public static void main(String[] args) throws InterruptedException {
        int[] arr = generateRandomArray(20000000, 1, 10);	// Generate array with our generation method

        long start = System.nanoTime();
        int sumParallel = parallelSum(arr);	// Call multi-thread
        long end = System.nanoTime();
        long timeParallel = end - start;

        start = System.nanoTime();
        int sumSingle = singleSum(arr);	// Call single thread
        end = System.nanoTime();
        long timeSingle = end - start;

        System.out.println("Array: " + java.util.Arrays.toString(arr));
        System.out.println("Parallel sum: " + sumParallel);
        System.out.println("Parallel time: " + timeParallel + " nanoseconds");
        System.out.println("Single sum: " + sumSingle);
        System.out.println("Single time: " + timeSingle + " nanoseconds");
    }

    
    
    // Function to generate random array
    public static int[] generateRandomArray(int size, int min, int max) {
        int[] arr = new int[size];							// Declaring array of size "size"
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(max - min + 1) + min; 	// This 'randomly' chooses a num between 1-10
        }
        return arr;
    }

    // Execute threading
    // This method divides the array into equal chunks based on the # of threads available
    // Then each thread computes the sum of its assigned chunk
    public static int parallelSum(int[] arr) throws InterruptedException { // This takes the array declared in main
        int numThreads = Runtime.getRuntime().availableProcessors();	// Gets the number of available processors on the system(CPU cores)
        SumThread[] threads = new SumThread[numThreads];				// Create our SumThread object
        // Remember here we are creating numThreads number of threads to utilize
        // These threads will run concurrently
        
        for (int i = 0; i < numThreads; i++) {	// Ex: numThreads = 6 because I have a 6-core cpu
            
        	// This is creating a new object PER index in the threads array
        	threads[i] = new SumThread(arr, i * arr.length / numThreads, (i + 1) * arr.length / numThreads);
        	
        	
        	// Now all of the threads start to trigger here
            threads[i].start(); // This triggers the run() method in the Thread class.
            
        }
        
        // This line makes certain all of the parallel computations have completed before we make the final sum
        for (int i = 0; i < numThreads; i++) {
            threads[i].join(); // Join is a Thread method which halts the current thread until
        }
        
        int sum = 0;
        
        for (int i = 0; i < numThreads; i++) {
            sum += threads[i].getSum();	// Calling getSum method for our SumThread object
        }
        
        return sum;
    }

    // Single for loop to determine the speed of just adding all values normally with a for loop
    public static int singleSum(int[] arr) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }

    
    // This class will add the indices of the threads
    private static class SumThread extends Thread {
        private int[] arr;
        private int start;
        private int end;
        private int sum;	// This represents the added array which is returned in getSum()

        // Constructor
        public SumThread(int[] arr, int start, int end) {
            this.arr = arr;
            this.start = start;
            this.end = end;
        }
        
        // Overwrites run() in the Thread class
        public void run() {
            sum = 0;
            for (int i = start; i < end; i++) {
                sum += arr[i];
            }
        }
        
        public int getSum() {
            return sum;
        }
    }
}
