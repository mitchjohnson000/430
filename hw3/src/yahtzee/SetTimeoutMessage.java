package yahtzee;

/**
 * Message type used to request a message from the
 * timer component.  The 'originalId' will be used
 * as the correlation id in the TimeoutMessage, so that the
 * sender can use this message to refer to a different
 * message.
 */
public class SetTimeoutMessage extends AbstractMessage
{
  private int originalId;
  private int timeout; // ms
  private Status state;
  
  public SetTimeoutMessage(Component sender, int originalId, int timeout,Status state)
  {
    super(sender);
    this.originalId = originalId;
    this.timeout = timeout;
    this.state = state;
  }
  
  @Override
  public void dispatch(Component receiver)
  {
    receiver.handleSetTimeout(this);
  }
  
  public int getOriginalId()
  {
    return originalId;
  }
  
  public int getTimeout()
  {
    return timeout;
  }

  public Status getState() {
    return state;
  }
}
