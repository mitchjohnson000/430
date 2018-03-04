package worker;

import java.util.Random;

/**
 * Slow operation looks busy while generating an id number
 * and occupation for a given employee name.
 * @author smkautz
 *
 */
public class LookupService
{
  private static final String[] occupations = {
    "Doctor",
    "Lawyer",
    "Indian Chief",
    "Big Cheese",
    "Blunt Instrument",
    "Cat Herder",
    "Roadie",
    "Cookie Taster",
  };
  
  private static Random rand = new Random();
    
  public static StaffData lookup(String name)
  {
    long millis = 4000 + rand.nextInt(8000);
    try
    {
      lookBusy(millis);
      int id = Math.abs(name.hashCode());
      String occupation = occupations[rand.nextInt(occupations.length)];
      return new StaffData(name,id, occupation);
    }
    catch (InterruptedException ie)
    {
      return null;
    }
  }
  
  private static void lookBusy(long millis) throws InterruptedException
  {
    long interval = 300;
    long stop = System.currentTimeMillis() + millis;
    while(!Thread.currentThread().isInterrupted() && 
        System.currentTimeMillis() < stop)
    {
      System.out.print(".");
      Thread.sleep(interval);      
    }
  }

}
