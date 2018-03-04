package worker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Demo using a JList with a custom implementation
 * of ListModel.  The model encapsulates the actual
 * data, and when the data is changed the JList
 * is notified.
 * <p>
 * In this version there is an "Add" button that starts
 * up a time-consuming lookup task that is performed in the
 * background using a helper thread.  An animated indicator
 * is shown while the background work is being
 * done, and the user has the opportunity to cancel the task.
 * THIS CODE HAS MAJOR RACE CONDITIONS
 */
public class ListDemo2 extends JFrame
{
  private JButton button;
  private JButton addButton;
  private JTextField addField;
  private JCheckBox checkBox;
  private JPanel panel;
  private JList<String> list;
  private JTextArea textArea;
  
  private LookupWorker worker;
  private JProgressBar progressBar;
  private JButton cancelButton;
  private JPanel bottomPanel;
  
  // The actual data is stored in this custom
  // instance of ListModel
  private DemoModel model;

  /**
   * Entry point. This method should normally do 
   * nothing except (possibly) parse command-line
   * arguments and invoke a helper method for creating
   * and starting up the UI.
   */
  public static void main(String[] args)
  {
    Runnable r = new Runnable()
    {
      public void run()
      {
        createAndShow();
      }
    };
    SwingUtilities.invokeLater(r);
  }
  
  /**
   * Static helper method creates the frame and
   * makes it visible.
   */
  private static void createAndShow()
  {
    // create the frame
    JFrame frame = new ListDemo2(createTestData());
    
    // give it a nonzero size
    //frame.setSize(400, 150);
    frame.pack();
    
    // we want to shut down the application if the 
    // "close" button is pressed on the frame
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    // make the frame visible and start the UI machinery
    frame.setVisible(true);
  }
  
  /**
   * Some fake data for testing.
   */
  private static String[] testData = {
    "Bud Tugly", "Makeup artist",
    "Mike Easter", "Seat cushion tester",
    "Pikop Andropov", "Russian limosine driver",
    "Ajustos Miros", "Quality control technician",
    "Erasmus B. Draggin", "Working mother support group",
    "Warren Peace", "Leo Tolstoy biographer",
    
  };
  
  /**
   * Creates an initial list of the fake data
   * for testing.
   */
  private static List<StaffData> createTestData()
  {
    List<StaffData> ret = new ArrayList<StaffData>();
    int i = 0;
    int id = 0;
    while (i < testData.length)
    {
      String name = testData[i++];
      String title = testData[i++];
      ret.add(new StaffData(name, id++, title));
    }
    return ret;
  }
  
  
  /**
   * Constructor creates all components that will
   * be contained in this frame, but does not 
   * set the size or attempt to make itself visible.
   * 
   * @param initialData
   *   data with which the list is populated when first
   *   displayed
   */
  public ListDemo2(List<StaffData> initialData)
  {
    if (initialData != null)
    {
      model = new DemoModel(initialData);
    }
    else
    {
      model = new DemoModel(new ArrayList<StaffData>());
    }
    
    // Create a list box with the underlying model.  
    // The list box will add itself as a listener 
    // to be notified of changes in the model's data.
    model.sortUp();
    list = new JList<String>(model);

    // Allow only one list item to be selected at a time
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Initially the list comes up with the first item selected
    list.setSelectedIndex(0);

    // Start out with vertical space for four items
    list.setVisibleRowCount(4);
    
    // Create the selection listener
    list.addListSelectionListener(new MyListSelectionListener());

    // panel to contain the checkbox and Delete button
    panel = new JPanel();
        
    // check box initially checked
    checkBox = new JCheckBox("sort ascending");
    checkBox.setSelected(true);
    checkBox.addActionListener(new MyCheckBoxListener());
    panel.add(checkBox);

    // the Delete button
    button = new JButton("Delete");
    ActionListener buttonListener = new MyButtonListener();
    button.addActionListener(buttonListener);
    panel.add(button);
    
    // Add button
    addButton = new JButton("Add");
    addButton.addActionListener(buttonListener);
    panel.add(addButton);    
    addField = new JTextField(15);
    panel.add(addField);

    // textArea for display of full data for the highlighted
    // item in the list.  Make sure it is at least 25 columns wide. 
    // Display initial data from element 0 because we set up the
    // JList to start with item 0 selected
    textArea = new JTextArea(0, 25);
    textArea.setText(model.getElement(0).toString());

    // progress bar and cancel button
    progressBar = new JProgressBar(0, 100);
    progressBar.setIndeterminate(true);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(buttonListener);
    bottomPanel = new JPanel();
    bottomPanel.add(progressBar);
    bottomPanel.add(cancelButton);
 
    // Put the list box and the text area in scroll
    // panes, and put them both in a JSplitPane.  
    // The preferred width of the list will 
    // set the initial position of the divider
    // for the JSplitPane.
    JSplitPane splitPane = new JSplitPane();
    JScrollPane scroll1 = new JScrollPane(list);
    JScrollPane scroll2 = new JScrollPane(textArea);
    splitPane.setLeftComponent(scroll1);
    splitPane.setRightComponent(scroll2);

    // add panel as the NORTH component and use 
    // CENTER for the JSplitPane to take up 
    // all remaining space
    getContentPane().add(panel, BorderLayout.NORTH);
    getContentPane().add(splitPane, BorderLayout.CENTER);    
    getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    
    // bottom panel for progress/cancellation initially invisible
    bottomPanel.setVisible(false);
  }
  
  
  

  
  /**
   * Listener for delete buttons. 
   */
  private class MyButtonListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent event)
    {
      if (event.getSource() == button)
      {
        // Delete button
        int index = list.getSelectedIndex();
        if (index >= 0 && index < model.getSize())
        {
          model.removeElement(index);
          if (model.getSize() > 0)
          {
            list.setSelectedIndex(0);
            textArea.setText(model.getElement(0).toString());
          }
          else
          {
            textArea.setText("");
          }
        }
      }
      else if (event.getSource() == addButton)
      {
        // Add button          
        String name = addField.getText();
        addField.setText("");
        addButton.setEnabled(false);
        bottomPanel.setVisible(true);

        worker = new LookupWorker(name);
        SwingUtilities.invokeLater(worker);
      
      }
      else
      {
        // Cancel button
        System.out.println("cancelling");
        if (worker != null)
        {
          worker.cancel();
        }
      }
    }
    
  }
  
  /**
   * Listener for the check box causes the list to be
   * sorted in ascending or descending order.
   */
  private class MyCheckBoxListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {       
      if (checkBox.isSelected())
      {
        model.sortUp();
      }
      else
      {
        model.sortDown();
      }
      
      list.setSelectedIndex(0);
      list.ensureIndexIsVisible(0);
      textArea.setText(model.getElement(0).toString());
    }
  }

  /**
   * List selection listener.  When a list item is selected, 
   * we display the full text describing the highlighted 
   * item in the text area.
   */
  private class MyListSelectionListener implements ListSelectionListener
  {
    @Override
    public void valueChanged(ListSelectionEvent event)
    {
      int index = list.getSelectedIndex();
      if (index >= 0 && index < model.getSize())
      {
        StaffData d = model.getElement(index);
        textArea.setText(d.toString());
      }
    }      
  }


  
  
  /**
   * Helper thread to do employee lookup in background.
   */
  private class LookupWorker extends Thread
  {
    private boolean canceled;
    private String arg;
    
    public LookupWorker(String arg)
    {
      this.arg = arg;
      canceled = false;
    }
    
    public void cancel()
    {
      canceled = true;
      this.interrupt();
    }
    
    public void run()
    {
      StaffData newRecord = LookupService.lookup(arg);
      
      if (!canceled)
      {
        // update model
        model.addElement(newRecord);
        int i = model.getSize();
        list.setSelectedIndex(i - 1);
        list.ensureIndexIsVisible(i - 1);
      }
      // re-enable add button and remove progress bar
      addButton.setEnabled(true);
      bottomPanel.setVisible(false); 
    }
  }
}
