package list;
import list.SimpleIterator;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Partial implementation of the List interface using 
 * single links and a dummy node. 
 */
public class VersionedList<E>
{
  private final Node head;
  public AtomicInteger version;
  
  public VersionedList()
  {
    version = new AtomicInteger(0);
    head = new Node(null, null);
  }   
  
  public SimpleIterator<E> listIterator()
  {
    return new LinkedIterator();
  }
  
  /**
   * Node type for this class.
   */
  private class Node
  {
    final E data;
    Node next;
    int v;


    public Node(E pData, Node pNext)
    {
      data = pData;
      next = pNext;
    }
  }
  
  /**
   * Implementation of ListIterator for this class
   */
  private class LinkedIterator implements SimpleIterator<E>
  {
    // points to node preceding the next element
    private Node cursor;
    private int ver;

    public LinkedIterator()
    {
      int max = 0;
      cursor = head; 
      while(hasNext()){
        if(max < cursor.v){
          max = cursor.v;
        }
        cursor = cursor.next;
      }
      ver = max;

    }
    
    public boolean hasNext()

    {
      synchronized (cursor){
        synchronized (cursor.next){
          while(cursor.next.v > ver){
            cursor = cursor.next;
          }
          return cursor.next != null;
          }
      }
    }
    
    public E next()
    {
      if (!hasNext()) throw new NoSuchElementException();
      synchronized (cursor){
        synchronized (cursor.next){
          while(cursor.next.v > ver){
            cursor = cursor.next;
          }
          E t = cursor.data;
          return t;
          }
      }
    }
        
    public void add(E item)
    {
      Node temp = new Node(item, cursor.next);
      temp.v = version.incrementAndGet();
      synchronized (cursor.next){
        synchronized (cursor){
          cursor.next = temp;
          cursor = temp;
        }
      }

    }
  }

}
