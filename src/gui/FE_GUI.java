package ucd_2001_fe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import javax.swing.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import roadway_dispersion.*;
import utilities.convert_unit;

/**
 * <b>Overview</b><br>
 * The <code>FE_GUI</code> class provides a front end to the UCD_2001 Roadway DIspersion Model.
 * This class and related ucd_2001_fe package classes provied a simple graphical front end
 * to allow users to determine the concentration of pollutants near roadways based on
 *  meteorological information, roadway geometry and usage, and receptor location.
 *
 * @author Tony Held
 * @version 1.0 (December 2001)
 */

public class FE_GUI extends JFrame {

/**
 * Static members are used to determine the FE status.
 */

public static final int DEFAULT_DATA = 0;
public static final int SAVED = 1;
public static final int UNSAVED_DATA = 2;
public static final int RUN_NOT_SAVED = 3;
public static final int RUNNING = 4;
public static final int BAD_RUN = 5;

/**
 * The model_status member indicates if any data model has been changed.
 * This is useful in determining if a front end should be saved.
 */
 private static int FE_status = FE_GUI.DEFAULT_DATA;

   JPanel contentPane;
   JToolBar jToolBar = new JToolBar();
   JButton Load_Input = new JButton();
   JButton Save_Input = new JButton();
   ImageIcon open_icon, save_icon, help_icon, about_icon, run_icon;
   JLabel statusBar = new JLabel();
   BorderLayout borderLayout1 = new BorderLayout();
   JTabbedPane jTabbedPane1 = new JTabbedPane();
   JPanel Pollutant = new JPanel();
   JPanel Receptors = new JPanel();
   JPanel Links = new JPanel();
   JPanel Meteorology = new JPanel();
   JRadioButton jRadioButton_Other = new JRadioButton();
   JRadioButton jRadioButton_SF6 = new JRadioButton();
   JRadioButton jRadioButton_CO = new JRadioButton();
   ButtonGroup pollutant_group = new ButtonGroup();
   JButton Save_Output = new JButton();
   JPanel Run_Results = new JPanel();
   JScrollPane Receptor_Scroll = new JScrollPane();
   BorderLayout borderLayout2 = new BorderLayout();

   JTable Receptor_Table, Link_Table, MET_Table, Run_Table;
   FE_table_model Receptor_model, Link_model, MET_model, Run_Model;

   // This will allow cut and paste of the Jtables to excel
   ExcelAdapter link_copy, run_copy, receptor_copy, met_copy;

   JPanel Receptor_Buttons = new JPanel();
   JButton Receptor_Add = new JButton();
   JButton Receptor_Delete = new JButton();
   JButton Link_Delete = new JButton();
   JButton Link_Add = new JButton();

   JScrollPane Link_Scroll = new JScrollPane();
   JPanel Link_Buttons = new JPanel();
   BorderLayout borderLayout3 = new BorderLayout();
   BorderLayout borderLayout5 = new BorderLayout();

   JScrollPane MET_Scroll = new JScrollPane();
   BorderLayout borderLayout4 = new BorderLayout();
   JPanel Select_Pollutant_Panel = new JPanel();
   JPanel Pollutant_Info = new JPanel();
   JLabel Pollutant_Name = new JLabel();
   JLabel Pollutant_MW1 = new JLabel();
   JLabel Pollutant_MW2 = new JLabel();
   JTextField Pollutant_Description = new JTextField();
   JTextField Pollutant_MW = new JTextField();

   JScrollPane Run_Scroll = new JScrollPane();

   // file filter and chooser information
    JFileChooser fc;
    ExampleFileFilter filter;
   JButton run_model_button = new JButton();
   JButton About_button = new JButton();


   /**Construct the frame*/
   public FE_GUI() {
      enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
   /**Component initialization*/
   private void jbInit() throws Exception  {

   // set the file input and output directory info
   // let the user select the path and file name to save the data to
	 fc = new JFileChooser();

	 // filter files so that only valid UCD 2001 are accepted
       filter = new ExampleFileFilter(
                  "ucd_2001", "UCD_2001 Files");
      Run_Table = new JTable(Run_Model);
      fc.addChoosableFileFilter(filter);

	 // load in the default data
	 load_default_data();

      open_icon = new ImageIcon(ucd_2001_fe.FE_GUI.class.getResource("openFile.gif"));
      save_icon = new ImageIcon(ucd_2001_fe.FE_GUI.class.getResource("saveFile.gif"));
//      help_icon = new ImageIcon(ucd_2001_fe.FE_GUI.class.getResource("help.gif"));
	 about_icon = new ImageIcon(ucd_2001_fe.FE_GUI.class.getResource("help.gif"));
	 run_icon = new ImageIcon(ucd_2001_fe.FE_GUI.class.getResource("start.gif"));

      //setIconImage(Toolkit.getDefaultToolkit().createImage(FE_GUI.class.getResource("[Your Icon]")));
      contentPane = (JPanel) this.getContentPane();
      contentPane.setLayout(borderLayout1);
      this.setSize(new Dimension(599, 326));
      this.setTitle("UCD 2001 Roadway Dispersion Model Front End");
      this.addKeyListener(new java.awt.event.KeyAdapter() {
         public void keyTyped(KeyEvent e) {
            this_keyTyped(e);
         }
      });
      statusBar.setText(" ");
      Load_Input.setIcon(open_icon);
      Load_Input.setText("Open");
      Load_Input.setVerticalAlignment(SwingConstants.TOP);
      Load_Input.setVerticalTextPosition(SwingConstants.BOTTOM);
      Load_Input.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Load_Input_actionPerformed(e);
         }
      });
      Load_Input.setToolTipText("Load model input data from a file");
      Save_Input.setIcon(save_icon);
      Save_Input.setText("Save");
      Save_Input.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Save_Input_actionPerformed(e);
         }
      });
      Save_Input.setToolTipText("Save model input to a file");
      Save_Input.setActionCommand("Save Input");
      jRadioButton_Other.setPreferredSize(new Dimension(175, 25));
      jRadioButton_Other.setText("Other Pollutant");
      jRadioButton_Other.setBounds(new Rectangle(18, 88, 135, 25));
      jRadioButton_Other.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            jRadioButton_Other_itemStateChanged(e);
         }
      });

      jRadioButton_SF6.setPreferredSize(new Dimension(175, 25));
      jRadioButton_SF6.setMinimumSize(new Dimension(175, 25));
      jRadioButton_SF6.setSelected(true);
      jRadioButton_SF6.setText("SF6");
      jRadioButton_SF6.setMaximumSize(new Dimension(175, 25));
      jRadioButton_SF6.setBounds(new Rectangle(18, 30, 100, 25));
      jRadioButton_SF6.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            jRadioButton_SF6_itemStateChanged(e);
         }
      });
      jRadioButton_CO.setPreferredSize(new Dimension(175, 25));
      jRadioButton_CO.setMinimumSize(new Dimension(175, 25));
      jRadioButton_CO.setText("CO");
      jRadioButton_CO.setMaximumSize(new Dimension(175, 25));
      jRadioButton_CO.setBounds(new Rectangle(18, 59, 100, 25));
      jRadioButton_CO.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            jRadioButton_CO_itemStateChanged(e);
         }
      });
      Pollutant.setLayout(null);
      Save_Output.setActionCommand("Save Input");
      Save_Output.setToolTipText("Save model output to a file");
      Save_Output.setText("Output Text File");
      Save_Output.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Save_to_text_file_actionPerformed(e);
         }
      });
      Save_Output.setIcon(save_icon);
      contentPane.setMinimumSize(new Dimension(600, 200));
      contentPane.setPreferredSize(new Dimension(500, 200));
      jToolBar.setFloatable(false);
      Receptors.setLayout(borderLayout2);
      Receptor_Add.setMnemonic('A');
      Receptor_Add.setText("Add Receptor");
      Receptor_Add.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Receptor_Add_actionPerformed(e);
         }
      });
      Receptor_Delete.setMnemonic('D');
      Receptor_Delete.setText("Delete Receptor");
      Receptor_Delete.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Receptor_Delete_actionPerformed(e);
         }
      });
      Receptor_Table.setToolTipText("Enter the receptor name and coordinates");

      Link_Delete.setMnemonic('D');
      Link_Delete.setText("Delete Link");
      Link_Delete.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Link_Delete_actionPerformed(e);
         }
      });

      Link_Add.setMnemonic('A');
      Link_Add.setText("Add Link");
      Link_Add.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Link_Add_actionPerformed(e);
         }
      });
      Link_Table.setToolTipText("Enter the receptor name and coordinates");
      Links.setLayout(borderLayout3);
      MET_Table.setToolTipText("Enter the receptor name and coordinates");
      Meteorology.setLayout(borderLayout4);
      Select_Pollutant_Panel.setBorder(new TitledBorder ( new EtchedBorder(), "Select Pollutant"));
      Select_Pollutant_Panel.setBounds(new Rectangle(8, 19, 178, 136));
      Select_Pollutant_Panel.setLayout(null);
      Pollutant_Info.setLayout(null);
      Pollutant_Info.setBounds(new Rectangle(202, 19, 258, 136));
      Pollutant_Info.setBorder(new TitledBorder ( new EtchedBorder(), "Pollutant Info"));
      Pollutant_Name.setText("Name");
      Pollutant_Name.setBounds(new Rectangle(14, 36, 70, 24));
      Pollutant_MW1.setBounds(new Rectangle(14, 73, 60, 24));
      Pollutant_MW1.setText("Molecular");
      Pollutant_MW2.setText("Weight");
      Pollutant_MW2.setBounds(new Rectangle(14, 94, 60, 24));
      Pollutant_Description.setEnabled(false);
      Pollutant_Description.setText("Sulfur Hexaflouride");
      Pollutant_Description.setBounds(new Rectangle(99, 39, 150, 21));
      Pollutant_MW.setEnabled(false);
      Pollutant_MW.setText("146.0504");
      Pollutant_MW.setBounds(new Rectangle(99, 84, 98, 21));
      Run_Results.setLayout(borderLayout5);


      run_model_button.setEnabled(false);
      run_model_button.setIcon(run_icon);
      run_model_button.setText("Run Model");
      run_model_button.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            run_model_button_actionPerformed(e);
         }
      });
      Run_Table.setEnabled(false);
      About_button.setIcon(about_icon);
      About_button.setText("About");
      About_button.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            About_button_actionPerformed(e);
         }
      });
      contentPane.add(jToolBar, BorderLayout.NORTH);
      jToolBar.add(Load_Input);
	 jToolBar.add(Save_Input);
      jToolBar.add(Save_Output, null);
      jToolBar.add(run_model_button, null);
      jToolBar.add(About_button, null);

      contentPane.add(statusBar, BorderLayout.SOUTH);
      contentPane.add(jTabbedPane1, BorderLayout.CENTER);

	 jTabbedPane1.add(Pollutant, "Pollutant");
      jTabbedPane1.add(Receptors, "Receptors");
      Receptors.add(Receptor_Scroll, BorderLayout.CENTER);
      Receptors.add(Receptor_Buttons, BorderLayout.SOUTH);
      Receptor_Buttons.add(Receptor_Add, null);
      Receptor_Buttons.add(Receptor_Delete, null);
      Receptor_Scroll.getViewport().add(Receptor_Table, null);
      jTabbedPane1.add(Links, "Links");
      Links.add(Link_Scroll, BorderLayout.CENTER);
      Links.add(Link_Buttons, BorderLayout.SOUTH);
      Link_Buttons.add(Link_Add, null);
      Link_Buttons.add(Link_Delete, null);
      Link_Scroll.getViewport().add(Link_Table, null);
      jTabbedPane1.add(Meteorology, "Meteorology");
      Meteorology.add(MET_Scroll, BorderLayout.CENTER);
      MET_Scroll.getViewport().add(MET_Table, null);
      jTabbedPane1.add(Run_Results, "Run Results (PPM)");
      Run_Results.add(Run_Scroll, BorderLayout.CENTER);
      Run_Scroll.getViewport().add(Run_Table, null);

      Pollutant.add(Select_Pollutant_Panel, null);
      Select_Pollutant_Panel.add(jRadioButton_Other, null);
      Select_Pollutant_Panel.add(jRadioButton_CO, null);
      Select_Pollutant_Panel.add(jRadioButton_SF6, null);
      Pollutant.add(Pollutant_Info, null);
      Pollutant_Info.add(Pollutant_MW1, null);
      Pollutant_Info.add(Pollutant_MW2, null);
      Pollutant_Info.add(Pollutant_Name, null);
      Pollutant_Info.add(Pollutant_MW, null);
      Pollutant_Info.add(Pollutant_Description, null);
      pollutant_group.add(jRadioButton_CO);
      pollutant_group.add(jRadioButton_SF6);
      pollutant_group.add(jRadioButton_Other);

	 // System.out.println("done with initialize");
	 Set_FE_status(DEFAULT_DATA);  //indicate if input data is valid on the status bar

   }  // end of initialization

   /**File | Exit action performed*/
   public void jMenuFileExit_actionPerformed(ActionEvent e) {
      System.exit(0);
   }
   /**Help | About action performed*/
   public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
      FE_GUI_AboutBox dlg = new FE_GUI_AboutBox(this);
      Dimension dlgSize = dlg.getPreferredSize();
      Dimension frmSize = getSize();
      Point loc = getLocation();
      dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
      dlg.setModal(true);
      dlg.show();
   }
   /**Overridden so we can exit when window is closed*/
   protected void processWindowEvent(WindowEvent e) {
      super.processWindowEvent(e);
      if (e.getID() == WindowEvent.WINDOW_CLOSING) {
         jMenuFileExit_actionPerformed(null);
      }
   }

   /**
    * Add an additional receptor to the receptor list.
    */
   void Receptor_Add_actionPerformed(ActionEvent e) {
		 Receptor_model.add_named_row("New Receptor");
		 Receptor_model.fireTableDataChanged();  // update the table
		 Set_FE_status(UNSAVED_DATA);            // update the status display
   }

   /**
    * Remove a row from the receptor table.
    */
   void Receptor_Delete_actionPerformed(ActionEvent e) {
      Remove_Model_Row (Receptor_Table,Receptor_model, "Receptor");
	 Receptor_model.fireTableDataChanged();  // update the table
	 Set_FE_status(UNSAVED_DATA);            // update the status display
  }

   /**
    * Add an additional Link to the Link list.
    */
   void Link_Add_actionPerformed(ActionEvent e) {
	   Link_model.add_named_row("New Link");
	   Link_model.fireTableDataChanged();      // update the table
	   Set_FE_status(UNSAVED_DATA);            // update the status display
	    }

   /**
    * Remove a row from the link table.
    */
   void Link_Delete_actionPerformed(ActionEvent e) {
	   Remove_Model_Row (Link_Table,Link_model, "Link");
	   Link_model.fireTableDataChanged();       // update the table
	   Set_FE_status(UNSAVED_DATA);            // update the status display
	      }

   /**
    * Remove a row of a Jtables model data if the user selected a valid row.
    * This routine will ensure that a valid row has been selected and that
    * the table will still have at least one row of valid data.  The String
    * parameter is used to display valid warning messages.  For the UCD FE,
    * the String should either be "Receptor" or "Link"
    */
   void Remove_Model_Row (JTable table, FE_table_model model, String row_descriptor) {
	   // find the row of the table that is currently selected
	   // this will return the index of the row (rember it is zero based index)
	   // this will be -1 if no row is selected
	   int selected_row = table.getSelectedRow();
	   // find total number of rows
	   int total_row_count = table.getRowCount();

	   switch (selected_row)
	   {
	    case -1:
		    //user does not have a row selected
		    JOptionPane.showMessageDialog(this,
			"You must select the row you want to delete first.",
			"Delete " + row_descriptor + " Row Warning",JOptionPane.WARNING_MESSAGE);

		    break;
	    default:
		  // remove the selected row
		  // you must have at least one row of data
		  if (total_row_count>1) {
			model.removeData_Row(selected_row);
			model.fireTableDataChanged();  // update the table
			Set_FE_status(UNSAVED_DATA);            // update the status display
		  }
		  else
		  {
			JOptionPane.showMessageDialog(this,
			"Delete " + row_descriptor + " canceled. You must have at least one "
			+ row_descriptor + ".", "Delete " + row_descriptor + " Row Warning",
			JOptionPane.WARNING_MESSAGE);
		  }
	   }
   }



/**
 *
 * This routine is called if the "other pollutant" radio button was selected.
 * This routine enables GUI text boxes which allow the user to specify a custom
 * pollutant name and molecular weight.
 */

   void jRadioButton_Other_itemStateChanged(ItemEvent e) {
        if (e.getStateChange()==ItemEvent.SELECTED)
	   {
	    // indicate that the FE data has been modified
	    Set_FE_status(UNSAVED_DATA);            // update the status display

	    // Enable the Pollutant Attributes Text
	    Pollutant_Description.enable();
	    Pollutant_MW.enable();

	   // set the Pollutant Attributes Text
	    Pollutant_Description.setText("Other");
	    Pollutant_MW.setText("9999.99");

	    // move the focus over the the pollutant description box
	    Pollutant_Description.selectAll();
	    Pollutant_Description.grabFocus();
	   }
   }

   /**
    * Routine called when user selects the CO radio button.
    */

   void jRadioButton_CO_itemStateChanged(ItemEvent e) {
        if (e.getStateChange()==ItemEvent.SELECTED)
	   {
	    // indicate that the FE data has been modified
	    Set_FE_status(UNSAVED_DATA);            // update the status display


	    // Enable the Pollutant Attributes Text
	    Pollutant_Description.disable();
	    Pollutant_MW.disable();

	   // set the Pollutant Attributes Text
	    Pollutant_Description.setText("Carbon Monoxide");
	    Pollutant_MW.setText("28.01");
	   }
   }

      /**
    * Routine called when user selects the SF6 radio button.
    */
   void jRadioButton_SF6_itemStateChanged(ItemEvent e) {
        if (e.getStateChange()==ItemEvent.SELECTED)
	   {

	    // indicate that the FE data has been modified
	    Set_FE_status(UNSAVED_DATA);            // update the status display

	    // Enable the Pollutant Attributes Text
	    Pollutant_Description.disable();
	    Pollutant_MW.disable();

	   // set the Pollutant Attributes Text
	    Pollutant_Description.setText("Sulfur Hexaflouride");
	    Pollutant_MW.setText("146.0504");
	   }
   }

   /**
    * Save the GUI information to a file.  This routine uses the fact that most
    * objects in the GUI are serializable and can be read and stored to files
    * without significant overhead.
    */

   public void save_site_info(){

	 // format of the input file
	 // Int which indicates if a numbered pollutant was used
	    // CO = 0, SF6 = 1, Other  =-1

	 // String Pollutant Name
	 // String Pollutant MW
	 // Vector Receptor Info
	 // Vector Link Info
	 // Vector MET data
	 // Vector Run Info data
	 // String[] Run info column headings

	 try
	 {

	 // find out where the user wants to save the data file
	 int reutrnVal = fc.showSaveDialog(this);

	 if (reutrnVal == JFileChooser.APPROVE_OPTION)
	    {

	    // open a connenction to the the file
	    File out_file = fc.getSelectedFile();

	    // make sure that the file has the correct extension
		  if ( out_file.getAbsolutePath().endsWith(".UCD_2001") != true)
		  {
			out_file = new File ( out_file.getAbsolutePath() + ".UCD_2001" );
		  }

	    FileOutputStream output = new FileOutputStream(out_file);
	    ObjectOutputStream objectOut = new ObjectOutputStream(output);

	    // write the data to the file
		  // find out what pollutant type was selected
		  Integer pollutant_type = null;
		  if (jRadioButton_CO.isSelected()==true)
			{pollutant_type = new Integer(0);}
		  if (jRadioButton_SF6.isSelected()==true)
			{pollutant_type = new Integer(1);}
		  if (jRadioButton_Other.isSelected()==true)
			{pollutant_type = new Integer(-1);}

	    objectOut.writeObject(pollutant_type);                      //pollutant type
	    objectOut.writeObject(Pollutant_Description.getText());     //pollutant description
	    objectOut.writeObject(Pollutant_MW.getText());              //pollutant MW

	    objectOut.writeObject(Receptor_model.getData());            //receptor Vector
	    objectOut.writeObject(Link_model.getData());                //link Vector
	    objectOut.writeObject(MET_model.getData());                 //MET Vector
	    objectOut.writeObject(Run_Model.getData());                 //Run output data Vector
	    objectOut.writeObject(Run_Model.getColumnNames());          //Run output column names

	    // flush and close the stream and output file
	    objectOut.flush();
	    objectOut.close();
	    output.flush();
	    output.close();
	    }
	 }
	 catch (Exception e)
	 {
	    System.out.println(e);
	    e.printStackTrace();
	 }
   }

   /**
    * Read the GUI input and output data from a file with a UCD_2001 extension.
    */

    public void load_site_info(){

	 // format of the input file
	 // Int which indicates if a numbered pollutant was used
	    // CO = 0, SF6 = 1, Other  =-1

	 // String Pollutant Name
	 // String Pollutant MW
	 // Vector Receptor Info
	 // Vector Link Info
	 // Vector MET data

	 try
	 {

	// load an input file from a file
	 int reutrnVal = fc.showDialog(this,"Load");

	 if (reutrnVal == JFileChooser.APPROVE_OPTION)
	    {

	    // open a connenction to the the file
	    File input_file = fc.getSelectedFile();
	    FileInputStream input = new FileInputStream(input_file);
	    ObjectInputStream objectIn = new ObjectInputStream(input);

	    // read the data from the file
	    Object temp = objectIn.readObject();

	    // find out what pollutant type was selected
	    int pollutant_type = ((Integer) temp).intValue();

	    switch (pollutant_type) {
		  case 0:              // CO
			  jRadioButton_CO.doClick();
			  break;
		  case 1:              // SF6
			  jRadioButton_SF6.doClick();
			  break;
		  case -1:             // Other
			  jRadioButton_Other.doClick();
			  break;
	    }

	    temp = objectIn.readObject(); //pollutant description
	    Pollutant_Description.setText((String) temp);

	    temp = objectIn.readObject(); //pollutant MW
	    Pollutant_MW.setText((String) temp);

	    temp = objectIn.readObject(); //receptor Vector
	    Receptor_model.setData( (Vector) temp);
	    Receptor_model.fireTableDataChanged();

	    temp = objectIn.readObject(); //link Vector
	    Link_model.setData( (Vector) temp);
	    Link_model.fireTableDataChanged();

	    temp = objectIn.readObject(); //MET Vector
	    MET_model.setData( (Vector) temp);
	    MET_model.fireTableDataChanged();

	    Initialize_Run();              // -99.9 out the run data
	    temp = objectIn.readObject(); //Run output data Vector
	    Run_Model.setData( (Vector) temp);
	    Run_Model.fireTableDataChanged();


	    temp = objectIn.readObject(); //Run output column names
	    Run_Model.setColumnNames( (String[]) temp);
	    // close the stream and input file
	    objectIn.close();
	    input.close();

	    Set_FE_status(SAVED);            // update the status display
	    }
	 }
	 catch (Exception e)
	 {
	    System.out.println(e);
	    e.printStackTrace();
	 }
   }

   /**
    * Save the UCD_2001 input information to a file on the hard drive.
    */

   void Save_Input_actionPerformed(ActionEvent e) {
        save_site_info();
	   Set_FE_status(SAVED);            // update the status display

   }

   void Load_Input_actionPerformed(ActionEvent e) {
	   load_site_info();

   }

   /**
    * Set the run results table so that it has the proper headings and labels
    * with all data set to -99.9 to indicate that data is not valid.
    */
   private String[] InitializeRunHeadings()
   {
	 String[] link_names, header_names;
	 String temp_name;

	 link_names = Link_model.getColumnNames();

	 // There will be one more run heading than links because of the link
	 // totals column
	 int num_links = Link_model.getRowCount();        // number of links in the Link_model
	 header_names = new String[num_links + 2];

	 header_names[0] = "Receptor Name";
	 header_names[1] = "Total";          // run data has totals column in the header

	 for (int i=0; i<num_links; i++)
	 {
	   // get the link name from the link model data
	   temp_name =   ( (String) ( (Vector) Link_model.getData().get(i) ).get(0));
	   header_names[i+2]=temp_name;    // need to offset the header names because of the total column
	 }

	 return header_names;
   }

   /**
    * Return an object with the proper row headins and all data set to -99.9
    * to facilitate the initialization of the GUI display.
    */

   private Object[][] Initialize_Run_Data()
   {
     int num_receptors, num_links;

	 num_receptors = Receptor_model.getRowCount();
	 num_links = Link_model.getRowCount();

	 Object[][] data = new Object[num_receptors][num_links+2];

	 // save the run data so that the receptor name appears in the first column

	 for (int i=0; i<num_receptors; i++){
		     data[i][0] =  ( (String) ( (Vector) Receptor_model.getData().get(i) ).get(0) );

		for (int j=1; j<num_links+2; j++){ // populate the initialization data with -99.9
		  data[i][j] = new Double(-99.9);
		}
	 }
	return data;

   }

   /**
    * Save the GUI input and output information to a text file so that
    * UCD 2001 model results can be easily imported into another program
    * (such as excel) for analysis.
    */

   public void save_to_text_file(){
	 try
	    {

	    // find out where the user wants to save the data file
	    int reutrnVal = fc.showSaveDialog(this);

	    if (reutrnVal == JFileChooser.APPROVE_OPTION)
		  {

		  // open a connenction to the the file
		  File out_file = fc.getSelectedFile();

		  // make sure that the file has the correct extension
		  if ( out_file.getAbsolutePath().endsWith(".UCD_2001.txt") != true)
		  {
			out_file = new File ( out_file.getAbsolutePath() + ".UCD_2001.txt" );
		  }

		  PrintWriter output = new PrintWriter ( new BufferedWriter ( new FileWriter(out_file) ) );

		  output.println( "UCD 2001 Roadway Dispersion Model Output Information \n" );

		  // print the pollutant information
		  output.println("The pollutant simulated was "
		                + Pollutant_Description.getText()
					 + " with a molecular weight of "
					 + Pollutant_MW.getText()
					 + " grams/mole. \n");

		  // print the meteorological info

			// take the vector data and convert it to doubles
			double ws =   ( (Double)  ( (Vector) MET_model.getData().get(0) ).get(0) ).doubleValue();
			double wd =   ( (Double)  ( (Vector) MET_model.getData().get(0) ).get(1) ).doubleValue();
			double ref =  ( (Double)  ( (Vector) MET_model.getData().get(0) ).get(2) ).doubleValue();
			double temp = ( (Double)  ( (Vector) MET_model.getData().get(0) ).get(3) ).doubleValue();
			double pres = ( (Double)  ( (Vector) MET_model.getData().get(0) ).get(4) ).doubleValue();

		  output.println("A wind speed of " + ws + " m/s blowing from "
		               + wd + " degrees was measured at "
					+ ref + " m."
		               );
	       output.println("The estimated temperature was " + temp
		               + " K and the estimated pressure was " + pres + " atms. \n");

output.println("In the output that follows, all measurements with unlabeled units are in meters. \n");

		  // save receptor grid
		  output.println("\n-------------Receptor Information------------" );
		  output.println("---------------------------------------------" );
		  output.println( Receptor_model );

		  // save the link grid
		  output.println("\n-------------Link Information----------------" );
		  output.println("Traffic flowrate is in vehicles per hour. ");
		  output.println("Emission factors are in grams per mile per vehicle. ");
		  output.println("---------------------------------------------" );
		  output.println( Link_model );
		  // save the output grid
		  output.println("\n-------------Run Results---------------------" );
		  output.println("All concentrations are in PPM.");
		  output.println("---------------------------------------------" );
		  output.println( Run_Model );

		  // flush and close the stream and output file
		  output.flush();
		  output.close();
		  }
	    }
	    catch (Exception e)
	    {
		  System.out.println(e);
		  e.printStackTrace();
	    }
   }

   /**
    * Save output data to a text file (calls save_to_text_file).
    */

   void Save_to_text_file_actionPerformed(ActionEvent e) {
	   this.save_to_text_file();
   }


   /**
    * When a user types data into a table, the FE indicates that the
    * input data needs to be saved.
    */

   void this_keyTyped(KeyEvent e) {
	    Set_FE_status(UNSAVED_DATA);            // update the status display
   }

   /**
    * Let the user know if the status of the input and output data.
    * This routine updates the status bar which alerts the user with messages such as
    * the input data neds to be saved or that the model is currently running.
    */

   private void Set_FE_status(int new_status){

	 // assign the status to the FE object
	 FE_status = new_status;

	   switch (FE_status)
	   {
	    case  DEFAULT_DATA:
	          statusBar.setText("FE input data is the default data. Ready to run model.");
			run_model_button.setEnabled(true);       //allow the user to run based on good data
			break;

	    case  UNSAVED_DATA:
	          statusBar.setText("You cannot run the UCD 2001 model until input has been saved. Run output reset.");
			run_model_button.setEnabled(false);      //do allow user to run on unsaved data
			Initialize_Run();                 // reset the run data
			break;

	    case  SAVED:
	          statusBar.setText("FE input data has beened saved. Ready to run model.");
			run_model_button.setEnabled(true);       //allow the user to run based on good data
			break;

	    case  RUN_NOT_SAVED:
	          statusBar.setText("FE input data has beened saved, but latest run data has not.");
			run_model_button.setEnabled(false);       //allow the user to run based on good data
			break;

	    case RUNNING:
		    statusBar.setText("Running the model please wait.");
		    Initialize_Run();                 // reset the run data
		    break;

	    case BAD_RUN:
		    statusBar.setText("There was an error running the UCD 2001 model based on the input data.");
		    Initialize_Run();                 // reset the run data
		 break;
	    }

   }

   /**
    * This routine updates the run model data so that the headers and row labels
    * agree with the link and recepter data.  In addition, this routine sets all concentration
    * data to -99.9 to show that the ouput data is not valid.
    */

   private void Initialize_Run()
   {
   // reset the run data to -99.9s

	    	 String[] Run_Columns = InitializeRunHeadings();
	      Object[][] Run_Data = Initialize_Run_Data();
		 Run_Model.setColumnNames(Run_Columns);
		 Run_Model.setData(Run_Data);
		 Run_Model.fireTableStructureChanged();
		 Run_Model.fireTableDataChanged();       // let the jtable know that the structure and data has changed
   }


/**
 * Routine run when user selects the run model button.
 */
   void run_model_button_actionPerformed(ActionEvent e) {

	   try {

		Set_FE_status(RUNNING);

		// switch to run results pane
		jTabbedPane1.setSelectedIndex(4);

	      // run the ucd model on a seperate thread
		 model_thread mt = new model_thread();
		 mt.setDaemon(true);   // have the thread die if the FE ends
		 mt.start();    // start the model thread

	   }
	   catch (Exception excep)
	   {
	    System.out.println("There was an error running the UCD 2001 model.");
	    System.out.println("Debugging information printed below.");
	    System.out.println("-----------------------------------------------");
	    System.out.println(excep);
	    excep.printStackTrace();
	   }

   }

   /**
    * This routine loads in default link, met, and receptor data.  The receptor
    * and link geometry correspond to the GM roadway dispersion study conducted
    * in the mid 1970's.
    */

   private void load_default_data(){
	 // default FE data is from day *** of the GM experiment

	 // load in default table data for display in the Jtables
	 // the coodinates and receptor names used here are from the GM site configuration
	 String[] Recep_Columns  = {"Receptor Name", "X (m)", "Y (m)", "Z (m)"};
	 Object[][] Recep_Data  =
	 {
	    { "Recp_1_1", new Double(-42.7), new Double(-13.25), new Double(9.58) },
	    { "Recp_1_2", new Double(-42.7), new Double(-13.25), new Double(3.51) },
	    { "Recp_1_3", new Double(-42.7), new Double(-13.25), new Double(0.51) },
	    { "Recp_2_1", new Double(-14.6), new Double(-4.53),  new Double(9.5) },
	    { "Recp_2_2", new Double(-14.6), new Double(-4.53),  new Double(3.63) },
	    { "Recp_2_3", new Double(-14.6), new Double(-4.53),  new Double(0.56) },
	    { "Recp_3_1", new Double(0.0),   new Double(0.0),    new Double(9.58) },
	    { "Recp_3_2", new Double(0.0),   new Double(0.0),    new Double(3.05) },
	    { "Recp_3_3", new Double(0.0),   new Double(0.0),    new Double(0.56) },
	    { "Recp_4_1", new Double(16.5),  new Double(5.12),   new Double(9.63) },
	    { "Recp_4_2", new Double(16.5),  new Double(5.12),   new Double(3.61) },
	    { "Recp_4_3", new Double(16.5),  new Double(5.12),   new Double(0.51) },
	    { "Recp_5_1", new Double(27.7),  new Double(8.59),   new Double(9.5) },
	    { "Recp_5_2", new Double(27.7),  new Double(8.59),   new Double(3.48) },
	    { "Recp_5_3", new Double(27.7),  new Double(8.59),   new Double(0.56) },
	    { "Recp_6_1", new Double(42.7),  new Double(13.25),  new Double(9.6) },
	    { "Recp_6_2", new Double(42.7),  new Double(13.25),  new Double(3.84) },
	    { "Recp_6_3", new Double(42.7),  new Double(13.25),  new Double(0.58) },
	    { "Recp_7_3", new Double(62.7),  new Double(19.45),  new Double(0.56) },
	    { "Recp_8_3", new Double(112.7), new Double(34.96),  new Double(0.56) } };

	 Receptor_model = new FE_table_model( Recep_Columns, Recep_Data);
      Receptor_Table = new JTable(Receptor_model);
	 receptor_copy = new ExcelAdapter(Receptor_Table);  // This will allow cut and paste of the Jtables to excel


	 // link information is from the GM site description as well
	 // time period 274140958 is the default

        // list value in grams per mile per hour but this will be converted
	   // to grams per meter per second before running UCD 2001

        double GM_source_strength = 192.34;        // source strength of each GM link in g / mile / hour
	   double GM_flowrate = 5462/2.0 ;     // single gm link flowrate in vehicles / hour
								    // if you considered the roadway a single link you would have 5462 VPH
								    // since we consider 2 links the flowrate per link is 1/2 of 5462

	   // source strength = flowrate * vehicle emission factor
	   double single_car_ef = GM_source_strength / GM_flowrate ;


	 String[] Link_Columns = { "Link Name", "X1 (m)", "X2 (m)", "Y1 (m)", "Y2 (m)", "Width (m)", "VPH", "EF (g/mile/vehicle)"};
	 Object[][] Link_Data  =
	 {
	    {"GM East Link", new Double(9.3), new Double(9.3), new Double(-2500.0), new Double(2500.0),
					 new Double(6.8), new Double(GM_flowrate), new Double(single_car_ef)},
	    {"GM West Link", new Double(-9.3), new Double(-9.3), new Double(-2500.0), new Double(2500.0),
					 new Double(6.8), new Double(GM_flowrate), new Double(single_car_ef)}};

	 Link_model = new FE_table_model( Link_Columns, Link_Data);
      Link_Table = new JTable(Link_model);
	 link_copy = new ExcelAdapter(Link_Table);  // This will allow cut and paste of the Jtables to excel

	 String[] MET_Columns = { "Wind Speed (m/s)", "Wind Direction (degrees)", "Ref. Elevation (m)",
					      "Temperature (K)", "Pressure (atm)"};
	 Object[][] MET_Data  = { {new Double(2.87), new Double(291.0), new Double(4.5),
					       new Double(283.73),
						  new Double(convert_unit.mmHg_to_ATMS( 734.0))}};

	 MET_model = new FE_table_model( MET_Columns, MET_Data);
      MET_Table = new JTable(MET_model);
	 met_copy = new ExcelAdapter(MET_Table);  // This will allow cut and paste of the Jtables to excel

	 Run_Model = new FE_table_model();
	 this.Initialize_Run();
	 Run_Table = new JTable(Run_Model);
	 run_copy = new ExcelAdapter(Run_Table);  // This will allow cut and paste of the Jtables to excel
   }

   /**
    * The UCD 2001 model will be called with this inner member class.
    * The facilitates threading the execution so the user does not have
    * to wait for the model to complete running.
    */

   public class model_thread extends Thread{

   model_thread(){}          // default constructor does nothing

   /**
    * The run method will begin the execution of the UCD 2001 model on a seperate thread.
    */
   public void run(){
		this.run_UCD_2001();
   }
   /**
    * Run the UCD 2001 model based on the data from the FE input.
    */
    private void run_UCD_2001(){
	 try
	 {
	    // convert the input information to a UCD 2001 site object
	    site_description site = new site_description();

	    // initialize meteorological information
	    // take the vector data and convert it to doubles
	    double ws =   ( (Double)  ( (Vector) MET_model.getData().get(0) ).get(0) ).doubleValue();
	    double wd =   ( (Double)  ( (Vector) MET_model.getData().get(0) ).get(1) ).doubleValue();
	    double ref =  ( (Double)  ( (Vector) MET_model.getData().get(0) ).get(2) ).doubleValue();
	    double temp = ( (Double)  ( (Vector) MET_model.getData().get(0) ).get(3) ).doubleValue();
	    double pres = ( (Double)  ( (Vector) MET_model.getData().get(0) ).get(4) ).doubleValue();

	    site.set_site_temperature_in_K(temp);
	    site.set_site_pressure_in_ATMS(pres);
	    site.setDispersion(ws,wd,ref);

	    // set the pollutant information
	    pollutant_type pt = new pollutant_type( Pollutant_Description.getText(),
									    Double.parseDouble( Pollutant_MW.getText() )  );
	    site.set_pollutant(pt);

	    // set the receptors
		  Vector receptors = Receptor_model.getData(); // get the FE receptor data
		  // receptor object to facilitate saving receptor info to the site object
		  receptor recep;
		  String recep_name;
		  double x,y,z;

		  for (int i=0; i<receptors.size(); i++){
			recep_name =   (String) ( (Vector) Receptor_model.getData().get(i) ).get(0);
			x          = ( (Double) ( (Vector) Receptor_model.getData().get(i) ).get(1) ).doubleValue();
			y          = ( (Double) ( (Vector) Receptor_model.getData().get(i) ).get(2) ).doubleValue();
			z          = ( (Double) ( (Vector) Receptor_model.getData().get(i) ).get(3) ).doubleValue();
			recep = new receptor(recep_name, new coordinate(x,y,z));
			site.add_receptor(recep);
		  }

		  // set the links
		  Vector links = Link_model.getData(); // get the FE link data
		  // receptor object to facilitate saving receptor info to the site object
		  highway_link hwl;
		  String link_name;

		  double x1, x2, y1, y2, width, vph, vps, ef;
		  double elevation = 0.0;       // all links are at elevation 0.0 m

		  for (int i=0; i<links.size(); i++){
			link_name =   (String) ( (Vector) Link_model.getData().get(i) ).get(0);
			x1        = ( (Double) ( (Vector) Link_model.getData().get(i) ).get(1) ).doubleValue();
			x2        = ( (Double) ( (Vector) Link_model.getData().get(i) ).get(2) ).doubleValue();
			y1        = ( (Double) ( (Vector) Link_model.getData().get(i) ).get(3) ).doubleValue();
			y2        = ( (Double) ( (Vector) Link_model.getData().get(i) ).get(4) ).doubleValue();
			width     = ( (Double) ( (Vector) Link_model.getData().get(i) ).get(5) ).doubleValue();
			vph       = ( (Double) ( (Vector) Link_model.getData().get(i) ).get(6) ).doubleValue();
			vps = vph / 3600.0;    // convert from vehicles per hour to vehicles per second
			ef        = ( (Double) ( (Vector) Link_model.getData().get(i) ).get(7) ).doubleValue();

			// need to convert from g/mile/vehicle to g/m/vehicle
               double source_strength = ef/ constants.METERS_PER_MILE;

			hwl = new highway_link(new coordinate(x1,y1,elevation),
					             new coordinate(x2,y2,elevation),
							   width, source_strength, vps, link_name);
			site.add_link(hwl);
		  }

		  // run the model without saving information to a text file
		  site.run_simulation(false);

		  /** @todo see if there is a better way to do this */
		  // update the run_output tab based on run results
		  // first indicate the run results has bad data until proven otherwise
		  // this will also ensure that the rusults tab has the correct format and headers
		  Set_FE_status(UNSAVED_DATA);            // update the status display

		  double[][] conc = site.get_conc_matrix(2);    // get the concentration matrix in ppm

		  // convert the double results into the object[][] structure of the model data
		  int num_rows, num_columns;
		  num_rows = Run_Model.getRowCount();
		  num_columns = Run_Model.getColumnCount();
		  Object[][] new_data = new Object[num_rows][num_columns];


		  for (int i=0; i< num_rows; i++){
			for (int j=1; j<num_columns; j++) {
			    Run_Model.setValueAt( new Double( conc[i][j-1] ), i, j);     // conc data has an offset index
			}
		  }

		  // indicate that the run was completed and that it needs to be saved
		  Set_FE_status(RUN_NOT_SAVED);
	 }
	 catch (Exception e)
	 {
	    System.out.println("There was an error converting the input data for use with the UCD 2001 model.");
	    System.out.println("Check to see that all input is valid.  The stack trace will be printed to the console");
	    System.out.println(e);
	    e.printStackTrace();
	    Set_FE_status(BAD_RUN);
	 }
   }
   }

   /**
    * Display the about dialog box explaining the function of this code.
    */

   void About_button_actionPerformed(ActionEvent e) {
      FE_GUI_AboutBox dlg = new FE_GUI_AboutBox(this);
      Dimension dlgSize = dlg.getPreferredSize();
      Dimension frmSize = getSize();
      Point loc = getLocation();
      dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
      dlg.setModal(true);
      dlg.show();
   }
}



