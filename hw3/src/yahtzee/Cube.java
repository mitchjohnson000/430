package yahtzee;

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
  
  public Cube(TimerComponent timer)
  {
    this.timer = timer;
  }
  
  @Override
  public void send(IMessage message)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void start()
  {
    // TODO Auto-generated method stub
    
  }

}
