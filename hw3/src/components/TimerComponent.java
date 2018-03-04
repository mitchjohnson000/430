package components;

import java.util.concurrent.*;

/**
 * Timer component.  Sending a SetTimeoutMessage to this component
 * will cause a TimeoutMessage to be sent to the caller after
 * the timeout value given in the message.  The TimeoutMessage
 * will contain the "original ID" from the SetTimeoutMessage
 * as its correlation id.
 */
public class TimerComponent extends Component
{

  @Override
  public void send(IMessage message)
  {
    final TimerComponent timerComponent = this;

    if(message instanceof SetTimeoutMessage){

      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          message.getSender().send(new TimeoutMessage(((SetTimeoutMessage) message).getOriginalId(),timerComponent));
          System.out.println(Thread.currentThread().getName());
        }
      };

      //Only spawns a max of one thread, so still single threaded
      ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
      scheduler.schedule(runnable,((SetTimeoutMessage) message).getTimeout(), TimeUnit.SECONDS);
    }   
  }

  @Override
  public void start()
  {

  }

}
