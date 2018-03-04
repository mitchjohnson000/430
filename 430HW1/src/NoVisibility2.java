
/**
 * No, it is not properly synchronized, rule 2 is being violated. Both threads are accessing the same variable, number, but are not holding the same lock. They are holding locks with respect to their class, which happens to be different.
 * In order to fix this, simple create an object and set both locks to it.
 */
public class NoVisibility2
{
  private int number;
  private int count;
  private final int ITERATIONS = 10000;
  final private Object lock = new Object();
  
  public static void main(String[] args)
  {
    NoVisibility2 nv = new NoVisibility2();
    nv.go(); 
  }
  
  private void go()
  {
    new ReaderThread().start();
    
    System.out.println("Main thread starting loop");

    for (int i = 0; i <= ITERATIONS; ++i)
    {
      synchronized(lock)
      {
        number = i;
      }
      Thread.yield();
    }
  }
  
  class ReaderThread extends Thread
  {
    
    public int get()
    {
      while (true)
      {
        ++count;
        synchronized(lock)
        {
          if (number >= ITERATIONS) break;
        }
      }
      return number;
    }
    
    public void run()
    {
      System.out.println("Reader starting...");
      int result = get();
      System.out.println("Reader sees number: " + result);
    }
  }
}


