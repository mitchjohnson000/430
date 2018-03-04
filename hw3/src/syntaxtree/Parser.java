package syntaxtree;

/**
 * Class representing an instance of a source file parser.
 */
public class Parser
{
  private final String m_filename;
  
  public Parser(String filename)
  {
    m_filename = filename;
  }

  /**
   * Parse the source file.  Takes a long time, involves
   * lots of I/O, etc.
   */
  public SyntaxTree parse()
  {
    System.out.println(Thread.currentThread().getName() + 
        " parsing file: " +  m_filename); 
    try
    {
      Thread.sleep(5000);
    }
    catch (InterruptedException ignore) 
    {
    }
    return new SyntaxTree(m_filename);
  }
}
