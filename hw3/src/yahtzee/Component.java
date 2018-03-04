package yahtzee;

/**
 * Base component type for an actor-style message-passing
 * programming model.
 */
public abstract class Component
{
  /** 
   * Sink for log messages.
   */
  protected Component logger;
  
  protected void log(String logString)
  {
    logString = this.getClass() + ": " + logString;
    System.out.println(logString);
    //TextMessage m = new TextMessage(this, logString);
    //logger.send(m);
  }
  
  /**
   * Sends a message to this component. The implementation
   * of this method could vary between components but in all
   * case it should accept messages without blocking.
   * @param message
   *   the message to send
   */
  public abstract void send(IMessage message);

  /**
   * Signals to this component that it may begin processing messages.
   */
  public abstract void start();
  
  /**
   * Default message handling method.
   * @param msg
   */
  public void handleDefault(IMessage msg)
  {
    log("Unhandled message: " + msg.toString());
  }

  // For each message received, a component should call one of the
  // following handle methods. Which one should be called depends on
  // the type of that message.
  //
  // Each of the handle methods below should call `handleDefault()`.
  // A concrete component type can override those handle methods in
  // which it is "interested" with custom behavior.

  public void handleTimeout(TimeoutMessage msg)
  {
    handleDefault(msg);
  }
  
  public void handleSetTimeout(SetTimeoutMessage msg)
  {
    handleDefault(msg);
  }
}
