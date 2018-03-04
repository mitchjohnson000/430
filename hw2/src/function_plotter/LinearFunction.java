package function_plotter;

/**
 * Class representing a linear function.
 */
public class LinearFunction implements SVFunction
{
  /**
   * Slope of this linear function.
   */
  private double slope;
  
  /**
   * The y-intercept for this linear function.
   */
  private double intercept;
  
  /**
   * Constructs a linear function with the given slope and intercept.
   * @param givenSlope
   * @param givenIntercept
   */
  public LinearFunction(double givenSlope, double givenIntercept)
  {
    slope = givenSlope;
    intercept = givenIntercept;
  }
  
  /**
   * Returns the y-value for the given x.
   */
  public double yValue(double x)
  {
    return slope * x + intercept;
  }
}
