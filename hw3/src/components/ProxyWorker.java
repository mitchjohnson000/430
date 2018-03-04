package components;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Component type used by database proxy for blocking calls
 * on the server.
 */
public class ProxyWorker extends ThreadedComponent
{
  public static final String HOST = "localhost";
  public static final int PORT = 2222;

  @Override
  public void handleRequest(RequestMessage msg)
  {
    String result = null;
    
    // this blocks; requestor should avoid sending more requests until reply is received
    try
    {
      result = getResultFromDB(msg.getKey());
    }
    catch (IOException e)
    {
      // fall through and send null result
    }   
    IMessage reply = new ResultMessage(msg.getId(), this, result);
    msg.getSender().send(reply);
  }
  
  // copied from hw1
  private String getResultFromDB(int key) throws IOException
  {
    Socket s = null;
    try
    {
      // open a connection to the server
      s = new Socket(HOST, PORT);

      // for line-oriented output we use a PrintWriter
      PrintWriter pw = new PrintWriter(s.getOutputStream());
      pw.println("" + key);
      pw.flush();  // don't forget to flush...    

      // read response, which we expect to be line-oriented text
      Scanner scanner = new Scanner(s.getInputStream());
      String result = scanner.nextLine();
      return result;
    }
    finally
    {
      // be sure streams are closed
      try
      {
        s.close();
      }
      catch (IOException ignore){}
    }
  }
}
