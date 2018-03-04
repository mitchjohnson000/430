package function_plotter;


/**
 * Interface representing attributes and behavior
 * of a single-variable function that can be displayed
 * by a graphing utility.
 */
public interface SVFunction
{
  /**
   * Returns the value of the function for the given input.
   * @param x
   *   the x-value at which to evaluate the function
   * @return
   *   the y-value for the given x
   */
  double yValue(double x);
}
