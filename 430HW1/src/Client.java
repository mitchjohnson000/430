import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


/**
 * Part A) I create a new thread inside the doLookup() method so that when Client will access the remote database, it will start and run a new thread in order to keep the UI thread from freezing.
 *
 * Part B) Anytime the cache variable is accessed, I make sure that it is synchronized. This prevents race conditions in the add() and get() functions of the cache since it is being shared on multiple threads.
 */
public class Client
{
  public static final String HOST = "localhost";
  public static final int PORT = 2222;
  
  /**
   * Local cache of key/value pairs we've already looked up.
   */
  private ArrayList<Record> cache;
  
  /**
   * Scanner for console input.
   */
  private Scanner scanner;
  
  /**
   * Indicates whether the client should be shut down.
   */
  private boolean done = false;
  
  /**
   * Entry point.
   * @param args
   */
  public static void main(String[] args)
  {
    new Client().go();
  }
  
  public Client()
  {
    cache = new ArrayList<Record>();
    scanner = new Scanner(System.in);
  }
  
  /**
   * Main client loop.
   */
  public void go()
  {
    while (!done)
    {
      String response = getResponse();
      parseResponse(response);
    }
    
  }
  
  /**
   * Prints a menu and returns the text entered by user.
   * @return
   */
  private String getResponse()
  {
    System.out.println();
    System.out.println("Enter id number to look up, 'd' to display list, 'q' to quit");
    System.out.print("Your choice: ");
    return scanner.nextLine();
  }
  
  /**
   * Parses the string entered by user and takes appropriate action.
   * @param s
   */
  private void parseResponse(String s)
  {
    s = s.trim();
    if (isNumeric(s))
    {
      int key = Integer.parseInt(s);
      doLookup(key);
    }
    else
    {
      char ch = s.charAt(0);
      if (ch == 'd')
      {
        displayAll();
      }
      else if (ch == 'q')
      {
        done = true;
      }
      else
      {
        System.out.println("Please enter 'd', 'q', or an id number");
      }
    }
  }
  
  /**
   * Returns the value for the given key, retrieving it from the 
   * slow database if not present in the local list.
   * @param key
   * @return
   */
  private void doLookup(int key)
  {
    String value = getLocalValue(key);
    if (value != null)
    {
      display(key, value);
    }
    else
    {
      Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
          getValueFromDB(key);
        }
      });
      thread.start();
    }
  }
  
  /**
   * Look up given key in the slow database and add it to the local list
   * after displaying the result.
   * @param key
   */
  private  void getValueFromDB(int key)
  {
    Socket s = null;
    try
    {
      // open a connection to the server
      s = new Socket(HOST, PORT);

      // for line-oriented output we use a PrintWriter
      PrintWriter pw = new PrintWriter(s.getOutputStream());
      pw.println("" + key);
      pw.flush();  // don't forget to flush...    
      
      // read response, which we expect to be line-oriented text
      Scanner scanner = new Scanner(s.getInputStream());
      String value = scanner.nextLine();
      display(key, value);

      // make sure it's in the local cache
      if (getLocalValue(key) == null)
      {
        synchronized (this){
          cache.add(new Record(key, value));
          Collections.sort(cache);
        }

      }
    }
    catch (IOException e)
    {
      System.out.println(e);
    }
    finally
    {
      // be sure streams are closed
      try
      {
        s.close();
      }
      catch (IOException ignore){}
    }

  }

  /**
   * Returns true if the given string represents a positive integer.
   * @param s
   * @return
   */
  private boolean isNumeric(String s)
  {
    for (int i = 0; i < s.length(); ++i)
    {
      if (!Character.isDigit(s.charAt(i)))
      {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Displays one key/value pair.
   * @param key
   * @param value
   */
  private void display(int key, String value)
  {
    System.out.println("Value for id " + key + ": " + value);
  }
  
  /**
   * Displays all key/value pairs in local list.
   */
  private synchronized void displayAll()
  {
    for (int i =  0; i < cache.size(); ++i)
    {
      Record r = cache.get(i);     
      System.out.println(r.key() + " " + r.value());
    }
  }
  
  /**
   * Returns the value for given key, or null if not present in the list.
   * @param key
   * @return
   */
  private synchronized String getLocalValue(int key)
  {
    // binary search, since the list is sorted
    int start = 0;
    int end = cache.size() - 1;
    while (start <= end)
    {
      int mid = (start + end) / 2;
      Record currentRecord = cache.get(mid);
      if (key == currentRecord.key())
      {
        return currentRecord.value();
      }
      else if (key < currentRecord.key())
      {
        end = mid - 1;
      }
      else
      {
        start = mid + 1;
      }
    }
    
    // not found
    return null;      
  }
  
  /**
   * Key/value pair.
   */
  private static class Record implements Comparable<Record>
  {
    private final int key;
    private final String value;
    
    public Record(int key, String value)
    {
      this.key = key;
      this.value = value;
    }
    
    public int key()
    {
      return key;
    }
    
    public String value()
    {
      return value;
    }

    @Override
    public int compareTo(Record rhs)
    {
      return this.key - rhs.key;
    }
  }
}
