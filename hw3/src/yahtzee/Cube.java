package yahtzee;

import javax.print.attribute.standard.RequestingUserName;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Component representing a Yahtzee flash cube.  Each cube will broadcast
 * a PingMessage to the left and right every POLL_INTERVAL ms.  If no reply
 * is received within TIMEOUT ms, the cube assumes it has no neighbor
 * in that direction.  If a reply is received, the PingReplyMessage contains
 * the sender's current display value.  This is ignored for a right ping,
 * but for a left ping this value is the number of cubes to the receiver's left.
 */
public class Cube extends ThreadedComponent
{
  public static final int POLL_INTERVAL = 50; // ms
  public static final int TIMEOUT = 250; 
  
  private TimerComponent timer;
  private int numToLeft = 0;
  
  public Cube(TimerComponent timer)
  {
    this.timer = timer;
  }
  
  @Override
  public void send(IMessage message)
  {
    message.dispatch(this);
  }

  @Override
  public void handleTimeout(TimeoutMessage msg) {
    Universe.updateDisplay(this,"?");
  }

  @Override
  public void handlePing(PingMessage msg) {
    if (msg.isLeft()){
      //received ping from the right
      Universe.broadcastRight(new PingReplyMessage(Cube.this,false,numToLeft));
    } else {
      //received ping from the left
      Universe.broadcastLeft(new PingReplyMessage(Cube.this,true,numToLeft));
    }
  }

  @Override
    public void handlePingReply(PingReplyMessage msg) {
    if(!msg.isLeft()){
      //has a neighbor to the left
      this.numToLeft = msg.getNumToLeft() + 1;
    }else{
      //has a neighbor to the right

    }
    Universe.updateDisplay(this,msg.getNumToLeft() + "");
    System.out.println("NumToLeft: " + msg.getNumToLeft());
  }

  @Override
  public void start()
  {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        Universe.broadcastRight(new PingMessage(Cube.this,false));
        Universe.broadcastLeft(new PingMessage(Cube.this,true));
        timer.send(new SetTimeoutMessage(Cube.this,0,TIMEOUT));
      }
    };
    ScheduledExecutorService  scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    scheduledExecutorService.scheduleAtFixedRate(runnable,0,POLL_INTERVAL, TimeUnit.MILLISECONDS);

  }

}
