import java.util.Random;

/**
 * Simulated database.  Integer keys are mapped to a record consisting
 * of a name.
 */
public class FakeDatabase
{
  private static Random rand = new Random();
  
  /**
   * Return the record corresponding to the given key.
   * @param key
   * @return
   * @throws NoSuchEntryException
   */
  public static String retrieve(int key) throws NoSuchEntryException
  {
    lookBusy(2000 + rand.nextInt(4000));
    if (key >= 0 && key < data.length)
    {
      return data[key];
    }
    throw new NoSuchEntryException("No database entry for key " + key);
  }
  
  /**
   * As the name implies...
   * @param millis
   */
  private static void lookBusy(long millis)
  {
    final long interval = 300;
    long stop = System.currentTimeMillis() + millis;
    System.out.print("Working");
    try
    {
      while(System.currentTimeMillis() < stop)
      {
        System.out.print(".");
        Thread.sleep(interval);      
      }
    }
    catch (InterruptedException ie)
    {}
  }

  /**
   * The data.
   */
  private static final String[] data = {
    "Ken Thompson",
    "Brian Kernighan",
    "Dennis Ritchie",
    "Grace Hopper",
    "John Backus",
    "John McCarthy",
    "James Gosling",
    "Doug Lea",
    "Bjorn Stroustrup",
    "Alan Turing",
    "Hal Abelson",
    "Al Aho",
    "John Hopcroft",
    "Juris Hartmanis",
    "Nicklaus Wirth",
    "Edsger Dijkstra"   
  };
}
