package function_plotter;
/**
 * Class representing a quadratic function.
 */
public class QuadraticFunction implements SVFunction
{
  /**
   * Leading coefficient.
   */
  private double a;
  
  /**
   * Middle coefficient.
   */
  private double b;
  
  /**
   * Constant term.
   */
  private double c;
  
  /**
   * Constructs a quadratic function with the given coefficients.
   * @param givenA
   * @param givenB
   * @param givenC
   */
  public QuadraticFunction(double givenA, double givenB, double givenC)
  {
    a = givenA;
    b = givenB;
    c = givenC;
  }
  
  /**
   * Returns the y-value for the given x.
   */
  public double yValue(double x)
  {
    return a * x * x + b * x + c;
  }
}
