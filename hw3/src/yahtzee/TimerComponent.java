package yahtzee;

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
    message.dispatch(this);
  }

  @Override
  public void start()
  {

  }

  @Override
  public void handleSetTimeout(SetTimeoutMessage msg) {

    final TimerComponent timerComponent = this;
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        msg.getSender().send(new TimeoutMessage(((SetTimeoutMessage) msg).getOriginalId(),timerComponent,msg.getState()));
      }
    };

    //Only spawns a max of one thread, so still single threaded
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.schedule(runnable,((SetTimeoutMessage) msg).getTimeout(), TimeUnit.MILLISECONDS);
    scheduler.shutdown();
  }

}

