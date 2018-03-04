/**
 * Simple demonstration of a race condition in incorrectly
 * synchronized code.
 */
import java.util.concurrent.CountDownLatch;
public class Incrementer
{

  private static final int length = 100000;
  private static final int numberOfThreads = 100;
  private static final int numberOfOperations = 10000;
  
  private int[] theArray;
  private Thread[] threads = new Thread[numberOfThreads];

  public static void main(String[] args)
  {
    Incrementer i = new Incrementer();
    i.go();
  }
  
  public Incrementer()
  {
  }
  
  public void go()
  {
    theArray = new int[length];
    
    // Create a bunch of incrementer threads
    int numberOfIterations = numberOfOperations / numberOfThreads;
    CountDownLatch startSignal = new CountDownLatch(1);
    CountDownLatch doneSignal = new CountDownLatch(100);
    for (int i = 0; i < numberOfThreads; ++i)
    {
      threads[i] = new IncrementWorker(numberOfIterations, this,startSignal,doneSignal);
    }
    
    // Crude attempt to make sure all worker threads are started
    // more or less at once (this would be better done using
    // java.util.concurrent.CountDownLatch)
    //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    
    System.out.println("Starting " + numberOfThreads + " threads to increment all elements of a " +
        length + " element array " + numberOfIterations + " times");
    
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < numberOfThreads; ++i)
    {
      threads[i].start();
    }
    startSignal.countDown();

    // Crude mechanism to wait for everyone to finish
    // (this would be better done using java.util.concurrent.CountDownLatch)
//    for (int i = 0; i < numberOfThreads; ++i)
//    {
//      try
//      {
//        threads[i].join();
//      }
//      catch (InterruptedException e)
//      {
//        // shouldn't happen
//      }
//    }
    try{
      doneSignal.wait();
    }catch (InterruptedException e){

    }
    long elapsed = System.currentTimeMillis() - startTime;
    
    // Examine array contents
    int expected = numberOfOperations;
    int count = 0;
    for (int i = 0; i < length; ++i)
    {
      if (theArray[i] != expected)
      {
        ++count;
        System.out.println("theArray[" + i + "] = " + theArray[i]);
      }
    }
    System.out.println("There were " + count + " cells not containing " + expected);
    
    System.out.println("Time: " + elapsed);

  }


  /**
   * Iterates through the shared array and increments the value
   * in each cell.
   */
  public synchronized void incrementArray()
  {
    for (int i = 0; i < length; ++i)
    {
      ++theArray[i];
    }
  }


  /**
   * Thread that will attempt to increment each cell
   * of the shared array.
   */
  private class IncrementWorker extends Thread
  {
    private int iterations;
    private Incrementer incrementer;
    private CountDownLatch startSignal;
    private CountDownLatch doneSignal;
    
    public IncrementWorker(int iterations, Incrementer incrementer, CountDownLatch startSignal, CountDownLatch doneSignal)
    {
      this.iterations = iterations;
      this.incrementer = incrementer;
      this.startSignal = startSignal;
      this.doneSignal = doneSignal;

    }
    
    public void run()
    {
      try {
        startSignal.await();
        for (int i = 0; i < iterations; ++i) {
          doIncrement();
        }
        doneSignal.countDown();
      }catch (InterruptedException e){

      }
    }
    
    private void doIncrement()
    {
      incrementer.incrementArray();
    }
  }

}

