package function_plotter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Displays a polyline in a window of the Cartesian plane, with x and y in
 * (VIEWPORT_MIN, VIEWPORT_MAX).
 */
public class GraphWindow {

    /**
     * Maximum coordinate of plotter window.
     */
    public static final double VIEWPORT_MAX = 10.0;

    /**
     * Minimum coordinate of plotter window.
     */
    public static final double VIEWPORT_MIN = -10.0;

    /**
     * Points of polyline.
     */
    private ArrayList<Point2D.Double> points;

    /**
     * The window for the plotters.
     */
    private JFrame frame;

    /**
     * Creates a plotter with an empty polyline.
     */
    public GraphWindow() {
      points = new ArrayList<Point2D.Double>();
      createAndShowWindow();
    }


    /**
     * Extends the polyline to the specified
     * point.
     * 
     * @param x
     *            X-coordinate of the new point.
     * @param y
     *            Y-coordinate of the new point.
     */
    public void addPoint(final double x, final double y) {
      Runnable runnable = new Runnable() {
          @Override
          public void run() {
              points.add(new Point2D.Double(x, y));
          }
      };
      SwingUtilities.invokeLater(runnable);
      frame.repaint();


    }

    /**
     * Erases the current polyline.
     */
    public void clear() {
      points.clear();
      frame.repaint();
    }    
    
    /**
     * Instantiates and initializes the Swing components for the plotter.
     */
    private void createAndShowWindow() {
      frame = new JFrame();
      PlotterPanel panel = new PlotterPanel();
      frame.getContentPane().add(panel);
      frame.setSize(500, 500);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setTitle("Polyline Plotter");
      frame.setResizable(false);
      frame.setAlwaysOnTop(true);
      frame.setVisible(true);
    }

    /**
     * Inner class extends JPanel to provide custom rendering of the axes and
     * polyline.
     */
    private class PlotterPanel extends JPanel {
        
        /**
         * Draws the polyline.
         */
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(Color.white);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setStroke(new BasicStroke(0));

            // All our coordinates are in (VIEWPORT_MIN, VIEWPORT_MAX), 
            // and these need to be transformed into pixel coordinates. 
            // The y-axis also needs to be flipped since the origin starts 
            // off at the top left instead of the bottom left.
            int half_width = getWidth() / 2;
            int half_height = getHeight() / 2;
            g2.setTransform(new AffineTransform());
            g2.translate(half_width, half_height);
            g2.scale(half_width / VIEWPORT_MAX, -half_height / VIEWPORT_MAX);

            // Draw grid in light gray.
            g2.setPaint(Color.lightGray);
            for (double j = VIEWPORT_MIN; j < VIEWPORT_MAX; ++j) {
                g2.draw(new Line2D.Double(j, VIEWPORT_MIN, j, VIEWPORT_MAX));
                g2.draw(new Line2D.Double(VIEWPORT_MIN, j, VIEWPORT_MAX, j));
            }
            
            // Draw axes in black
            g2.setPaint(Color.black);
            g2.draw(new Line2D.Double(VIEWPORT_MIN, 0, VIEWPORT_MAX, 0));
            g2.draw(new Line2D.Double(0, VIEWPORT_MIN, 0, VIEWPORT_MAX));

            // Draw polyline in black.
            g2.setStroke(new BasicStroke(0.1f));
            g2.setPaint(Color.black);
            for (int i = 0; i < points.size() - 1; ++i) {
                g2.draw(new Line2D.Double(points.get(i).x, points.get(i).y,
                        points.get(i + 1).x, points.get(i + 1).y));
            }
        }
    }

}