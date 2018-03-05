package components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Client component receives text messages from an input
 * component and sends requests to a database proxy.
 */
public class ClientComponent extends ThreadedComponent
{

  private Component tc;
  
  /**
   * Reference to database proxy. 
   */
  private Component db;
  
  /**
   * Local cache of key/value pairs we've already looked up.
   */
  private ArrayList<Record> cache;

  /**
   * Map of message id to key of requests for which we're waiting for a result.
   */
  private Map<Integer, Integer> pending;
  
  public ClientComponent(Component db,Component tc)
  {
    this.db = db;
    cache = new ArrayList<Record>();
    pending = new HashMap<Integer, Integer>();
    this.tc = tc;
}
  
  public void handleText(TextMessage msg)
  {
    parseResponse(msg.getText());
  }

  @Override
  public void handleTimeout(TimeoutMessage msg) {
    if(pending.containsKey(msg.correlationId)){
      Integer i = pending.remove(msg.correlationId);
      System.out.println("request for id " + i + " timed out");

    }
  }

  public void handleResult(ResultMessage msg)
  {
    int id = msg.getCorrelationId();
    Integer key = pending.remove(id);
    if (key != null)
    {
      String result = getLocalValue(key);
      if (result == null)
      {
        result = msg.getResult();
        cache.add(new Record(key, result));
      }
      System.out.println("Value for id " + key + ": " + result);
      System.out.println("Enter id number to look up, 'd' to display list, 'q' to quit");
    }
  }

  /**
   * Parses the string entered by user and takes appropriate action.
   * @param s
   */
  private void parseResponse(String s)
  {
    s = s.trim();
    if (s.length() == 0)
    {
      System.out.println("Enter id number to look up, 'd' to display list, 'q' to quit");
    }
    else if (isNumeric(s))
    {
      int key = Integer.parseInt(s);
      doLookup(key);
    }
    else
    {
      char ch = s.charAt(0);
      if (ch == 'd')
      {
        display();
      }
      else
      {
        System.out.println("Please enter 'd' or an id number");
      }
    }
  }
  
  /**
   * Looks up the value for the given key, retrieving it from the 
   * slow database if not present in the local list.
   * @param key
   */
  private void doLookup(int key)
  {
    String value = getLocalValue(key);
    if (value == null)
    {
      IMessage msg = new RequestMessage(this, key);
      SetTimeoutMessage timeoutMessage = new SetTimeoutMessage(this,msg.getId(),2);
      int id = msg.getId();
      pending.put(id, key);
      db.send(msg);
      tc.send(timeoutMessage);
    }
    else
    {
      System.out.println("Value for id " + key + ": " + value);
      System.out.println("Enter id number to look up, 'd' to display list, 'q' to quit");
    }
  }
 
  /**
   * Returns the value for given key, or null if not present in the list.
   * @param key
   * @return
   */
  private String getLocalValue(int key)
  {
    for (Record r : cache)
    {
      if (r.key() == key)
      {
        return r.value();
      }
    }
    return null;
  }

  
  /**
   * Displays all key/value pairs in local list.
   */
  private void display()
  {
    for (int i =  0; i < cache.size(); ++i)
    {
      Record r = cache.get(i);     
      System.out.println(r.key() + " " + r.value());
    }
    System.out.println("Enter id number to look up, 'd' to display list, 'q' to quit");
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
