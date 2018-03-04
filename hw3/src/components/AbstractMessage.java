package components;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base message implementation.
 */
public abstract class AbstractMessage implements IMessage
{
  protected final int id;
  protected final int correlationId;
  protected final Component sender;
  private static final AtomicInteger generator = new AtomicInteger();
  
  protected AbstractMessage(int id, int correlationId, Component sender)
  {
    this.id = id;
    this.correlationId = correlationId;
    this.sender = sender;
  }
  
  public AbstractMessage(int correlationId, Component sender)
  {
    this(generator.incrementAndGet(), correlationId, sender);
  }

  public AbstractMessage(Component sender)
  {
    this(0, sender);
  }

  @Override
  public Component getSender()
  {
    return sender;
  }

  @Override
  public int getId()
  {
    return id;
  }

  @Override
  public int getCorrelationId()
  {
    return correlationId;
  }

  @Override
  public abstract void dispatch(Component receiver);

  @Override
  public String toString()
  {
    return this.getClass().getName() + " (" + id + ")";
  }
}
