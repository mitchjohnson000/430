
import java.util.NoSuchElementException;

/**
 * A standard singly-linked circular FIFO queue with tail pointer.
 * For an empty queue, the tail pointer is null; for a nonempty
 * queue, the head of the queue is tail.next.  (When the queue has 
 * one element, tail.next points to tail.)
 *
 * TODO: Put your response for Problem 4a here.
 * Lets say we have two threads, A and B, that are both accessing Circular Queue at the same time.
 * If thread A is on line 29, of add(T item) right after executing line 22 if(tail == null), while, at the same time, thread B is at line 39 if(tail.next == tail). This is a race condition that could lead to an error.
 * If thread B executes tail = null; before thread A executes temp.next = tail.next;, there will be a null pointer exception with respect to the tail variable.
 * TODO: Put your response for Problem 4b here.
 * Two calls to add() on different threads will not be able to see each other. This means that the tail will not be properly updated on the second add() call, which would result in the first add() being lost.
 * So when remove() is called, the second add() will be removed instead of the first, which is incorrect. This is a clear violation of rule 2 since two threads are accessing the same data without acquiring the same lock.
 */
public class CircularQueue<T>
{
  private Node tail = null;
  
  public void add(T item)
  {
    Node temp = new Node(item);
    if (tail == null)
    {
      // empty
      temp.next = temp;
    }
    else
    {
      temp.next = tail.next;
      tail.next = temp;
    }
    tail = temp;
  }
  
  public T remove()
  {
    if (tail == null) throw new NoSuchElementException();
    T ret = tail.next.data;
    if (tail.next == tail)
    {
      tail = null;
    }
    else
    {
      tail.next = tail.next.next;
    }
    
    return ret;
  }
  
  public boolean isEmpty()
  {
    return tail == null;
  }
  
  class Node
  {
    T data;
    Node next;
    
    public Node(T item)
    {
      data = item;
      next = null;
    }
  }
}
