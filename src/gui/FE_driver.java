package ucd_2001_fe;

import javax.swing.UIManager;
import java.awt.*;

/**
 * <b>Overview</b><br>
 * The <code>FE_driver</code> class launches the graphical front end to the UCD 2001 roadway dispersion
 * model.  The front end is a simple GUI to allow users to determine the concentration of
 * pollutants near roadways based on meteorological information,
 * roadway geometry and usage, and receptor location.
 *
 *
 * @author Tony Held
 * @version 1.0 (December 2001)
 */

public class FE_driver {
   boolean packFrame = false;

   /**Construct the application*/
   public FE_driver() {
      FE_GUI frame = new FE_GUI();
      //Validate frames that have preset sizes
      //Pack frames that have useful preferred size info, e.g. from their layout
      if (packFrame) {
         frame.pack();
      }
      else {
         frame.validate();
      }
      //Center the window
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = frame.getSize();
      if (frameSize.height > screenSize.height) {
         frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width) {
         frameSize.width = screenSize.width;
      }
      frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
      frame.setVisible(true);
   }
   /**Main method*/
   public static void main(String[] args) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e) {
         e.printStackTrace();
      }
      new FE_driver();
   }
}