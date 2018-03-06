package yahtzee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
  public static final String START = "START";
  public static final String DONE = "DONEE";

  private TimerComponent timer;
  private int numToLeft = 0;
  private Map<Integer,Integer> pending = new HashMap<Integer,Integer>();
  private boolean hasLeft = false;
  private boolean hasRight = false;
  private Status CURRENT_STATUS = Status.START;
  private int numInSeries = 0;
  private int currentValue = 0;
  private int currentTotal = 0;

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
     if (pending.containsKey(msg.getCorrelationId())) {
      Integer i = pending.remove(msg.getCorrelationId());
      if (i == 0) {
        hasRight = false;
        numInSeries = numToLeft;
      } else if (i == 1) {
        hasLeft = false;
        numToLeft = 0;
        currentTotal = currentValue;
      }
      if (!hasLeft && !hasRight) {
        if(CURRENT_STATUS == Status.START){
          numInSeries = 0;
          Universe.updateDisplay(this, "?");
        }else if(CURRENT_STATUS == Status.GENERATING && msg.getState() == CURRENT_STATUS){
            numInSeries = 0;
            currentValue = new Random().nextInt(6) + 1;
            CURRENT_STATUS = Status.SCORING;
            Universe.updateDisplay(this,currentValue + "");
        }else if(CURRENT_STATUS == Status.SCORING && msg.getState() == CURRENT_STATUS){
          numInSeries = 0;

        }else if(CURRENT_STATUS == Status.DONE && msg.getState() == CURRENT_STATUS){
          numInSeries = 0;
          Universe.updateDisplay(this,"?");
          CURRENT_STATUS = Status.START;
        }
      }
    }
  }

  @Override
  public void handlePing(PingMessage msg) {
    if (msg.isLeft()){
      //received ping from the right
      Universe.broadcastRight(new PingReplyMessage(msg.getId(),Cube.this,false,numToLeft,numInSeries,CURRENT_STATUS,currentTotal));
    } else {
      //received ping from the left
      Universe.broadcastLeft(new PingReplyMessage(msg.getId(),Cube.this,true,numToLeft,numInSeries,CURRENT_STATUS,currentTotal));
    }
  }

  @Override
    public void handlePingReply(PingReplyMessage msg) {
    if(pending.containsKey(msg.getCorrelationId())){
        pending.remove(msg.getCorrelationId());
    }
    if(!msg.isLeft()){
      //has a neighbor to the left
      this.currentTotal = msg.getTotal() + currentValue;
      hasLeft = true;
      this.numToLeft = msg.getNumToLeft() + 1;
      if(numToLeft > numInSeries){
        numInSeries = numToLeft;
      }
      if(CURRENT_STATUS == Status.START){
        Universe.updateDisplay(this,START.charAt(numToLeft) + "");
        if(numInSeries == 4){
          CURRENT_STATUS = Status.GENERATING;
        }
      }else if(CURRENT_STATUS == Status.GENERATING) {

      }else if(CURRENT_STATUS == Status.SCORING){
        Universe.updateDisplay(this,DONE.charAt(numToLeft) + "");
        if(numToLeft == 4){
          Universe.updateDisplay(this,currentTotal + "");
          CURRENT_STATUS = Status.DONE;
        }
        if(numInSeries == 4){
          CURRENT_STATUS = Status.DONE;
        }
      }
    }else if(msg.isLeft()){
      //has a neighbor to the right
      hasRight = true;
      numInSeries = msg.getNumInSeries();

      if(CURRENT_STATUS == Status.START){
        Universe.updateDisplay(this,START.charAt(numToLeft) + "");
        if(numInSeries == 4){
          CURRENT_STATUS = Status.GENERATING;
        }
      }else if(CURRENT_STATUS == Status.GENERATING ) {

      }else if(CURRENT_STATUS == Status.SCORING){
        Universe.updateDisplay(this,DONE.charAt(numToLeft) + "");
        if(numToLeft == 4){
          Universe.updateDisplay(this,currentTotal + "");
        }
        if(numInSeries == 4){
          CURRENT_STATUS = Status.DONE;
        }
      }
    }
  }

  @Override
  public void start()
  {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        IMessage rightMessage = new PingMessage(Cube.this,false);
        IMessage leftMessage = new PingMessage(Cube.this,true);

        pending.put(rightMessage.getId(),0);
        pending.put(leftMessage.getId(),1);

        Universe.broadcastRight(rightMessage);
        Universe.broadcastLeft(leftMessage);

        SetTimeoutMessage timeoutRight = new SetTimeoutMessage(Cube.this,rightMessage.getId(),TIMEOUT,CURRENT_STATUS);
        SetTimeoutMessage timeoutLeft = new SetTimeoutMessage(Cube.this,leftMessage.getId(),TIMEOUT,CURRENT_STATUS);

        timer.send(timeoutRight);
        timer.send(timeoutLeft);
      }
    };
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    scheduledExecutorService.scheduleAtFixedRate(runnable,0,POLL_INTERVAL, TimeUnit.MILLISECONDS);
  }
}
