package components;

import java.util.Scanner;

/**
 * Component that reads input from System.in and delivers it to a receiver
 * in the form of text messages.
 */
public class InputComponent extends Component
{
  private Component receiver;
  private Thread reader;
  private Scanner in;
  
  public InputComponent(Component receiver)
  {
    this.receiver = receiver;
    in = new Scanner(System.in);
    reader = new InputReaderThread();
  }  
  
  @Override
  public void send(IMessage message)
  {
    // do nothing
  }

  @Override
  public void start()
  {
    reader.start();
  }  
  
  private class InputReaderThread extends Thread
  {
    public void run()
    {
      receiver.send(new TextMessage(InputComponent.this, ""));
      
      while (true)
      {
        String text = in.nextLine();
        receiver.send(new TextMessage(InputComponent.this, text));
      }
    }
  }
  


}
