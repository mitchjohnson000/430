package syntaxtree;

/**
 * Class representing an abstract syntax tree resulting
 * from parsing a source file.
 */
public class SyntaxTree
{
  private final String m_filename;
  public SyntaxTree(String filename)
  {
    m_filename = filename;
  }
  
  // for testing
  public String getName()
  {
    return m_filename;
  }
}