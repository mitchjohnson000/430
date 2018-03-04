package components;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * General supertype for a component that has its own explicit
 * thread of execution and an unbounded queue for incoming
 * messages.
 */
public class ThreadedComponent extends Component
{
  protected BlockingQueue<IMessage> queue;
  protected Thread reader;
  
  protected ThreadedComponent()
  {
    queue = new LinkedBlockingQueue<IMessage>();
    reader = new Thread(new Reader());
  }  

  @Override
  public void send(IMessage message)
  {
    queue.offer(message);
  }

  @Override
  public void start()
  {
    reader.start();
  }
  
  protected class Reader implements Runnable
  {
    public void run()
    {
      while (true)
      {
        try
        {
          IMessage message = queue.take();
          message.dispatch(ThreadedComponent.this);
        }
        catch (InterruptedException e)
        {
          log(e.toString());
        }
        catch (Throwable t)
        {
          log(t.toString());          
        }
      }
    }
  }


}
