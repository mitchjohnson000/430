package components;

/**
 * Message used by timer component to indicate to a caller that
 * the requested timeout has expired.
 */
public class TimeoutMessage extends AbstractMessage
{
  public TimeoutMessage(int correlationId, Component sender)
  {
    super(correlationId, sender);
  }
  
  @Override
  public void dispatch(Component receiver)
  {
    receiver.handleTimeout(this);
  }
}
