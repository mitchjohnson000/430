package list;
import java.util.NoSuchElementException;

/**
 * Partial implementation of the List interface using 
 * single links and a dummy node. 
 */
public class LinkedList<E>
{
  private final Node head;
  
  public LinkedList()
  {
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

    public LinkedIterator()
    {
      cursor = head; 
    }
    
    public boolean hasNext()
    {
      return cursor.next != null;
    }
    
    public E next()
    {
      if (!hasNext()) throw new NoSuchElementException();
      
      cursor = cursor.next;
      return cursor.data;
    }
        
    public void add(E item)
    {
      Node temp = new Node(item, cursor.next);
      cursor.next = temp;
      cursor = temp;
    }
  }

}
