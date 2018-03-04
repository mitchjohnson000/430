package components;

/**
 * Message containing a string of text.
 */
public class TextMessage extends AbstractMessage
{
  protected final String text;
  
  public TextMessage(Component sender, String text)
  {
    super(sender);
    this.text = text;
  }

  public TextMessage(int correlationId, Component sender, String text)
  {
    super(correlationId, sender);
    this.text = text;
  }

  public String getText()
  {
    return text;
  }
  
  @Override
  public void dispatch(Component receiver)
  {
    receiver.handleText(this);
  }
}
