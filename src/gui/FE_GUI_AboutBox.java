package ucd_2001_fe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.borland.jbcl.layout.*;

/**
 * <b>Overview</b><br>
 * The <code>FE_GUI_AboutBox</code> class provides a dialog box which
 * dexcribes the UCD 2001 Front End.
 *
 * @author Tony Held
 * @version 1.0 (December 2001)
 */


public class FE_GUI_AboutBox extends JDialog implements ActionListener {

   JPanel about = new JPanel();
   JPanel about_south = new JPanel();
   JButton about_ok = new JButton();
   BorderLayout about_layout_1 = new BorderLayout();
   JPanel about_center = new JPanel();
   FlowLayout about_layout_2 = new FlowLayout();
   BoxLayout2 about_layout_3 = new BoxLayout2();
   JLabel Model_Name = new JLabel();
   JLabel Model_Version = new JLabel();
   JLabel Model_Copyright = new JLabel();
   JPanel about_west = new JPanel();
   JPanel about_north = new JPanel();
   JPanel about_east = new JPanel();
   JTextPane Model_Description = new JTextPane();
   JLabel Description_Label = new JLabel();
   Component v_strut_1;
   Component v_strut_2;
   Component v_strut_3;
   public FE_GUI_AboutBox(Frame parent) {
      super(parent);
      enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
      pack();
   }
   /**Component initialization*/
   private void jbInit() throws Exception  {
      //imageLabel.setIcon(new ImageIcon(FE_GUI_AboutBox.class.getResource("[Your Image]")));
      v_strut_1 = Box.createVerticalStrut(15);
      v_strut_3 = Box.createVerticalStrut(15);
      v_strut_2 = Box.createVerticalStrut(15);
	 this.setTitle("About the UCD 2001 Front End");
      setResizable(false);
      about.setLayout(about_layout_1);
      about_ok.setText("Ok");
      about_ok.addActionListener(this);
      about_south.setLayout(about_layout_2);
      about_center.setLayout(about_layout_3);
      Model_Name.setText("UCD_2001 Java Interface");
      Model_Version.setText("Version 1.0 (December 2001)");
      Model_Copyright.setText("Copyright (c) 2001");
      about_layout_3.setAxis(BoxLayout.Y_AXIS);
      Model_Description.setPreferredSize(new Dimension(300, 125));
      Model_Description.setBorder(null);
      Model_Description.setEditable(false);
      Model_Description.setFont(new java.awt.Font("Dialog", 0, 12));
      Model_Description.setText("A simple graphical front end to allow users to determine the concentration " +
    "of pollutants near roadways based on meteorological information, " +
    "roadway geometry and usage, and receptor location.");
      Model_Description.setBackground(SystemColor.menu);
      Description_Label.setText("Description");
      this.getContentPane().add(about, null);
      about_south.add(about_ok, null);
      about.add(about_east, BorderLayout.EAST);
      about.add(about_north, BorderLayout.NORTH);
      about.add(about_west, BorderLayout.WEST);
      about.add(about_center, BorderLayout.CENTER);
      about_center.add(v_strut_1, null);
      about_center.add(Model_Name, null);
      about_center.add(Model_Version, null);
      about_center.add(Model_Copyright, null);
      about_center.add(v_strut_2, null);
      about_center.add(Description_Label, null);
      about_center.add(v_strut_3, null);
      about_center.add(Model_Description, null);
      about.add(about_south, BorderLayout.SOUTH);
   }
   /**Overridden so we can exit when window is closed*/
   protected void processWindowEvent(WindowEvent e) {
      if (e.getID() == WindowEvent.WINDOW_CLOSING) {
         cancel();
      }
      super.processWindowEvent(e);
   }
   /**Close the dialog*/
   void cancel() {
      dispose();
   }
   /**Close the dialog on a button event*/
   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == about_ok) {
         cancel();
      }
   }
}