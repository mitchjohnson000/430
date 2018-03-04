package list;
import list.SimpleIterator;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Partial implementation of the List interface using 
 * single links and a dummy node. 
 */
public class ImmutableList<E>
{
  private Node head;
  
  public ImmutableList()
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
    final Node next;

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
      synchronized (ImmutableList.this){
        Node newNode = new Node(item,cursor.next);
        Node prev = new Node(cursor.data,newNode);
        Node oldPrev = cursor;
        Node curr = head;

        while(true){
         boolean concurrentCheck = true;
         if(curr.next == oldPrev){
           concurrentCheck = false;
           Node n1 = new Node(curr.data,prev);
           prev = n1;
            oldPrev = curr;
            if(oldPrev == head){
              head = prev;
              break;
          }
           curr = head;
         }
         curr = curr.next;
         if(concurrentCheck && curr == null){
          }            throw new ConcurrentModificationException();

        }

      }
    }
  }

}
