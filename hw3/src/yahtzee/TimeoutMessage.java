package yahtzee;

/**
 * Message used by timer component to indicate to a caller that
 * the requested timeout has expired.
 */
public class TimeoutMessage extends AbstractMessage
{
  private Status state;
  public TimeoutMessage(int correlationId, Component sender,Status state)
  {
    super(correlationId, sender);
    this.state = state;
  }
  
  @Override
  public void dispatch(Component receiver)
  {
    receiver.handleTimeout(this);
  }

  public Status getState() {
    return state;
  }
}
