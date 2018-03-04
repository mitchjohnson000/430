package function_plotter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

/**
 * Try out the FunctionPlotter class.
 */
public class FunctionPlotterMain
{
  public static void main(String[] args)
  {
    FunctionPlotter calc = new FunctionPlotter(0.1);
    
    // A variable of type SVFunction can refer to 
    // any subtype (that is, any class that implements the 
    // SingleVariableFunction interface).
    SVFunction f = null;
    
    Scanner scanner = new Scanner(System.in);
    String s = getEntry(scanner);
    
    while (!s.equals("q"))
    {
      if (s.equals("a"))
      {
        f = new LinearFunction(1.0, 0.0);
      }
      else if (s.equals("b"))
      {
        f = new QuadraticFunction(1.0, 0.0, 0.0);
      }
      else if (s.equals("c"))
      {
        f = new SineFunction(3.0, 2.0);
      }
      else
      {
        System.out.println("Enter a, b, or c (or q to quit)");
      }
      if (f != null)
      {
        // Now whatever f refers to, we can plot it
        calc.plotFunction(f);
      }
      
      s = getEntry(scanner);
    }
  }

  /**
   * Prints a menu and gets the user's choice.
   * @param scanner
   *   Scanner to use for reading from the console
   * @return
   *   letter entered by the user
   */
  private static String getEntry(Scanner scanner)
  {
    System.out.println("What kind of function do you want to plot? (q to quit)");
    System.out.println("a) Linear function");
    System.out.println("b) Quadratic function");
    System.out.println("c) Sine function");
    System.out.print("Your choice: ");    
    return scanner.next();  
  }


}
