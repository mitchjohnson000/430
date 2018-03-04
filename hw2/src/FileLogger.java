import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;


public class FileLogger
{
  private String filename;
  private ArrayBlockingQueue<String> queue;

  public FileLogger(String filename)
  {
    this.filename = filename;
    this.queue = new ArrayBlockingQueue<String>(10);
  }
  
  public void log(String msg)
  {
    if(queue == null) return;
    Date d = new Date();
    Writer writer = new Writer();
    String total = d + "" + msg;
    queue.offer(total);
    writer.run();
    // timestamp when log method was called with this message
  }

  public class Writer implements Runnable {
    @Override
    public void run() {
        try
        {
          // argument 'true' means append to existing file
          OutputStream os = new FileOutputStream(filename, true);
          PrintWriter pw = new PrintWriter(os);
          String str = queue.poll();
          if(str == null){
            pw.close();
            return;
          }
          pw.println(str);
          pw.close();
        }
        catch (FileNotFoundException e)
        {
          System.err.println("Unable to open log file: " + filename);
        }
      }

  }

  
}
