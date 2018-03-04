package function_plotter;

/**
 * A FunctionPlotter object plots the graph of a given
 * function on a GraphWindow window.  The function to be
 * plotted can be any instance of the SVFunction
 * interface.
 */
public class FunctionPlotter
{
  /**
   * The Plotter to be used.
   */
  private GraphWindow graphWindow;
  
  /**
   * Distance between x-values to be plotted.
   */
  private double gap;
  
  /**
   * Constructs a new GraphingCalculator that will use the 
   * given sampling gap.
   * @param samplingGap
   */
  public FunctionPlotter(double samplingGap)
  {
    gap = samplingGap;
    graphWindow = new GraphWindow();
  }
  

  /**
   * Sends a sequence of points to the plotter for
   * plotting the given function.
   * 
   * @param f
   *   the function to be plotted
   */
  public void plotFunction(SVFunction f)
  {
    graphWindow.clear();
    double x = -GraphWindow.VIEWPORT_MAX;
    while (x < GraphWindow.VIEWPORT_MAX + gap)
    {
      graphWindow.addPoint(x, f.yValue(x));
      x += gap;
    }
  }
}
