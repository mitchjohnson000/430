package yahtzee;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * User interface for simulation of Yahtzee Flash cubes.
 */
public class UniverseUI extends JPanel
{

  /**
   * Not used.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Width of box for each cube.
   */
  private static final int BOX_WIDTH = 100;
  
  /**
   * Height of box for each cube.
   */
  private static final int BOX_HEIGHT = 100;
  
  /**
   * Font size for text in boxes.
   */
  private static final int FONT_SIZE = 24;
  
  /**
   * Center to center separation between boxes.
   */
  private static final int SEPARATION = 100; 

  /**
   * Maximum vertical separation for neighbors.
   */
  private static final int VERTICAL_MAX = 10;
  
  /**
   * Maximum horizontal separation for neighbors.
   */
  private static final int HORIZONTAL_MAX = 5;
  
  /**
   * Array of movable rectangles for the cubes.
   */
  MoveableRectangle[] boxes;
  
  /**
   * Index of box currently clicked on.
   */
  private int pressed = -1;
  
  /**
   * Constructs a new panel instance.
   */
   public UniverseUI(int numCubes)
  {
    this.addMouseListener(new MyMouseListener());
    this.addMouseMotionListener(new MyMouseMotionListener());
    
    this.setPreferredSize(new Dimension(BOX_WIDTH * numCubes * 2, BOX_HEIGHT * 4));
    boxes = new MoveableRectangle[numCubes];
    for (int i = 0; i < boxes.length; ++i)
    {
      boxes[i] = new MoveableRectangle();
    }
    initializeBoxes();
  }

  /**
   * Position the boxes initially.
   */
  private void initializeBoxes()
  {
    
    // Center the word in the window, if possible; otherwise keep
    // a minimum left margin of one letter width
    int totalWidth = (boxes.length - 1) * SEPARATION  + BOX_WIDTH;
    int windowWidth = Math.max(getWidth(), totalWidth + 2 * BOX_WIDTH);
    int leftMargin = (windowWidth - totalWidth) / 2;
    leftMargin = Math.max(leftMargin, BOX_WIDTH); 

    // set character and x, y position for each rectangle
    for (int i = 0; i < boxes.length; ++i)
    {
      //boxes[i].ch = UNKNOWN;
      boxes[i].setText("?");
      int x = leftMargin + i * (SEPARATION + SEPARATION / 2);
      int y = BOX_HEIGHT;
      if (i % 2 == 1) y += BOX_HEIGHT;
      boxes[i].setLocation(x, y);
    }
  }
  
  @Override
  public void paintComponent(Graphics g)
  {
    
    Graphics2D g2 = (Graphics2D) g;
    Color savedColor = g2.getColor();
    g2.setBackground(Color.LIGHT_GRAY);
    g2.clearRect(0, 0, getWidth(), getHeight());

    // paint the rectangles...
    for (int i = 0; i < boxes.length; ++i)
    {
      MoveableRectangle r = boxes[i];
      g2.setColor(Color.WHITE);

      g2.fillRect(r.x + 1, r.y + 1, BOX_WIDTH - 2, BOX_HEIGHT - 2);
      g2.setColor(Color.BLACK);
      g2.draw(r);
      
      Font f = new Font(Font.SANS_SERIF, Font.PLAIN, FONT_SIZE);
      g2.setFont(f);
      FontMetrics fm = g2.getFontMetrics(f);
      String text = "" + r.ch;
      int h = fm.getHeight();
      int w = fm.stringWidth(text);
      int x = r.x + BOX_WIDTH / 2 - (w / 2);
      int y = r.y + BOX_HEIGHT / 2 + (h / 2) - 2;
      g2.drawString(text, x, y);

      g2.setColor(savedColor);
    }
  }
  
  /**
   * Update the value in the given box.  This method is thread-safe.
   * @param index
   * @param value
   */
  public void updateDisplay(final int index, final String text)
  {
    Runnable r = new Runnable()
    {
      public void run()
      {
        boxes[index].setText(text);
        repaint();
      }
    };
    SwingUtilities.invokeLater(r);
  }
  
  /**
   * Returns the index of the left or right neighbor of the given box, or
   * -1 if there isn't one. This method is thread-safe.
   * @param index
   * @param left
   * @return
   */
  public int getNeighbor(final int index, final boolean left)
  {
    final BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(1);
    
    Runnable r = new Runnable()
    {
      public void run()
      {
        int neighbor = findNeighbor(index, left);
        queue.add(neighbor);
      }
    };
    SwingUtilities.invokeLater(r);
    
    try
    {
      // wait for UI to set result
      return queue.take();
    }
    catch (InterruptedException e)
    {
      return -1;
    }
  }
  
  /**
   * Finds a left or right neighbor of the box with given index.
   * @param index
   * @param left
   * @return
   */
  private int findNeighbor(int index, boolean left)
  {
    MoveableRectangle current = boxes[index];
    int target = left ? current.x - BOX_WIDTH : current.x + BOX_WIDTH;
    for (int i = 0; i < boxes.length; ++i)
    {
      if (i != index)
      {
        if (Math.abs(target - boxes[i].x) <= HORIZONTAL_MAX  &&
            Math.abs(current.y - boxes[i].y) <= VERTICAL_MAX)
        {
          return i;
        }
      }
    }
    return -1;
  }
  
  /**
   * Mouse callback updates 'pressed' state if the mouse button is 
   * pressed while within the corresponding rectangle
   */
  private class MyMouseListener implements MouseListener
  {
    @Override
    public void mouseClicked(MouseEvent arg0)
    {
    }

    @Override
    public void mouseEntered(MouseEvent arg0)
    {
    }

    @Override
    public void mouseExited(MouseEvent arg0)
    {
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
      int mouseX = event.getX();
      int mouseY = event.getY();
      
      // see if we're within one of the rectangles
      for (int i = 0; i < boxes.length; ++i)
      {
        if (boxes[i].contains(mouseX, mouseY))
        {
          pressed = i;
          break;
        }
      }
      
      // if so, record the offsets to upper left corner
      // so we can correctly track motion
      if (pressed >= 0)
      {
        boxes[pressed].xOffset = mouseX - boxes[pressed].x;
        boxes[pressed].yOffset = mouseY - boxes[pressed].y;
        repaint();
      }
    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
      if (pressed < 0) return;
      pressed = -1;
      
//      // get the updated display values
//      for (int i = 0; i < letters.length; ++i)
//      {
//        Universe.getDisplay(i);
//      }
      repaint();
    }

  }
  
  /**
   * Mouse motion callback updates x, y position of selected
   * rectangle when mouse is moved with button held down.
   */
  private class MyMouseMotionListener implements MouseMotionListener
  {
    @Override
    public void mouseDragged(MouseEvent event)
    {
      if (pressed < 0) return; 

      int x = event.getX() - boxes[pressed].xOffset;
      int y = event.getY() - boxes[pressed].yOffset;
      boxes[pressed].setLocation(x, y);
      repaint();    
    }

    @Override
    public void mouseMoved(MouseEvent arg0)
    {
    }    
  }
  
  /**
   * Subtype of Rectangle adds a fields to store a character and
   * to record a current x and y offset to track mouse motion.
   */
  private class MoveableRectangle extends Rectangle
  {
    /**
     * Default constructor sets width and height; position
     * is set in initializeLetters.
     */
    public MoveableRectangle()
    {
      super(0, 0, BOX_WIDTH, BOX_HEIGHT);
    }  
    
    /**
     * Serial version number, not used.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * String to be drawn within this rectangle.
     */
    private String ch;
    
    /**
     * Offset from left side to mouse x position.
     */
    private int xOffset;
    
    /**
     * Offset from top to mouse y position.
     */
    private int yOffset;
    
    // assuming single digit, 0 means unknown
    public void setText(String text)
    {
      ch = text;
//      if (i == 0)
//      {
//        ch = UNKNOWN;
//      }
//      else
//      {
//        ch = ("" + i).charAt(0);
//      }
    }
  }


  
}
