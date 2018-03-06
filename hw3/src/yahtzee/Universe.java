package yahtzee;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Simulates the physical world inhabited by a set of Yahtzee Flash cubes.
 * A cube can broadcast a message to its left or right, and based
 * on the physical positions of the cubes in the UI, the message
 * will be delivered to the left or right neighbor if any.  The
 * universe also observes the current state of a cube (by calls
 * from the cube to the <code>updateDisplay</code> method) and reports
 * it to the UI.
 */
public class Universe
{
  public static final int NUM_CUBES = 5;
  private static UniverseUI ui;
  
  // This array does not change after initialization, but we
  // use class-level sync to ensure visibility, since we don't
  // necessarily know how the component threads are started.
  private static Component[] cubes;
  
  public static void main(String[] args)
  {
    Runnable r = new Runnable()
    {
      public void run()
      {
        bigBang();
        createComponents();
      }
    };
    SwingUtilities.invokeLater(r);
  }
  
  /**
   * Creates the universe.
   */
  public static void bigBang()
  {   
    ui = new UniverseUI(NUM_CUBES);
    JFrame frame = new JFrame();
    frame.getContentPane().add(ui);

    // size the frame based on the preferred size of the panel
    frame.pack();

    // make sure it closes when you click the close button on the window
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    // rock and roll...
    frame.setVisible(true);
  }
  
  private static void createComponents()
  {
    TimerComponent timer = new TimerComponent();
    cubes = new Cube[NUM_CUBES];
    for (int i = 0; i < NUM_CUBES; ++i)
    {
      cubes[i] = new Cube(timer);
      cubes[i].start();
    }
    timer.start();
  }
  
  /**
   * Attempts to send a message to a neighbor on the left, if any.
   * @param msg
   *   message to send
   */
  public static void broadcastLeft(IMessage msg)
  {
    broadcast(msg, true);
  }

  /**
   * Attempts to send a message to a neighbor on the right, if any.
   * @param msg
   *   message to send
   */
  public static void broadcastRight(IMessage msg)
  {
    broadcast(msg, false);
  }

  /**
   * Helper method for broadcasting left or right.
   * @param msg
   * @param left
   */
  private static void broadcast(IMessage msg, boolean left)
  {
    int index = getIndex(msg.getSender());
    if (index < 0) return;
    int neighbor = ui.getNeighbor(index, left);
    if (neighbor >= 0)
    {
      getCube(neighbor).send(msg);
    }
  }
  
  /**
   * Updates the representation of the given component in the UI.
   * @param c
   * @param state
   */
  public static void updateDisplay(Component c, String text)
  {
    // update UI with value
    int index = getIndex(c);
    if (index < 0) return;
    ui.updateDisplay(index, text);
  }
  
  /**
   * Returns the component with the given index.
   * @param index
   * @return
   */
  private static synchronized Component getCube(int index)
  {
    return cubes[index];
  }
  
  /**
   * Returns the index of the given component, or -1 if
   * it is not present.
   * @param c
   * @return
   */
  private static synchronized int getIndex(Component c)
  {
    for (int i = 0; i < cubes.length; ++i)
    {
      if (c == cubes[i])
      {
        return i;
      }
    }
    return -1;
  }
}
