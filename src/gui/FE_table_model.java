package ucd_2001_fe;
import javax.swing.table.*;
import java.util.*;           // access to vectors


/**
 * <b>Overview</b><br>
 * The <code>FE_table_model</code> class is based on the abstract table model and is used
 * to store the data appearing in the jtables in the UCD 2001 front end.
 *
 * @author Tony Held
 * @version 1.0 (December 2001)
 */

public class FE_table_model extends AbstractTableModel {

/**
 * The model_modified member indicates if any data model has been changed.
 * This is useful in determining if a front end should be saved.
 * Currently these routines are not used
 */
// private static boolean model_modified = false;
//
// public static void Set_model_modified(boolean val){
//   model_modified = val;
// }
//
// public static boolean get_model_modified(){
//   return model_modified;
// }



/**
 * The DEBUG member is used only to output debuggining
 * information while developing this object.
 */
     private boolean DEBUG = false;     // used to determine if debugging output should be used

	/**
	 * Member to store the table column names.
	 */
     private String[] ColumnNames;

	/**
	 * Table Data is stored in the <code>data</code> member.
	 * The table data is stored in a vector to allow the addition and
	 * deletion of rows.  Each member of the data vector contains a
	 * 1-D object array with data for a single row.
	 */
	 private Vector Data;


	/**
	 * Constructor that assigns column names and data to the table model.
	 * The parameter arrays are cloned so that the data holds its own
	 * copy of the data.  The Data parameter must be a rectangular matrix.
	 * The number of columns in columnNames and Data must agree.
	 * The table_model will store its own copy of the data passed to the object
	 * based on the parameter data.
	 */
	public FE_table_model(String[] ColumnNames, Object[][] Data) {

		  setColumnNames(ColumnNames);         // set column names
		  setData(Data);                       // set data matrix
	}

	/**
	 * Blank Contructor.
	 */

	public FE_table_model() {}

	/**
	 * Get the column names of the table Data.
	 */
	public String[] getColumnNames() {
	 return ColumnNames;
	}

	/**
	 * Get the underlying table Data as a Vector of Vectors.
	 */
	public Vector getData() {
	 return Data;
	}

	/**
	 * Set the column names of the table data to a copy of the passed names array.
	 */
	public void setColumnNames(String[] names) {
	   // redim the object's column name size
	   this.ColumnNames = new String[names.length];

	   // populate the Column names with a copy of the string column parameter
	   for (int j=0; j<names.length; j++)
	   {
		 this.ColumnNames[j] = names[j].toString();
	   }
	}

	/**
	 * Set the underlying table Data from a copy of an object matrix.
	 */
     public void setData(Object[][] new_data) {
	   // initialize the data members to receive new values
	   this.Data = new Vector();
	   // vector to store a single row of data
	   Vector row_of_data;

	   // loop through the data object and convert the rows into vector items.
	   for(int i=0; i<new_data.length; i++)
	   {
	       row_of_data = new Vector();   // create new vector to store a row of data

		  // populate the vector with the data from the Data parameter Object
		  for (int j=0; j<new_data[0].length; j++)
		  {
			row_of_data.add(new_data[i][j]);
		  }

		  this.Data.add(row_of_data);   // add the row of data to the object data vector
	   }
	}

	 /**
	 * Set the underlying table Data from a copy of a vector of vectors.
	 */
	public void setData(Vector new_data) {
	 Data = (Vector) new_data.clone();
	}

	/**
	 * Set a single row of the Data Vector to a copy of a vector parameter.
	 */
	 public void addDataRow(Vector new_row) {
	    Data.add(new_row);
	 }

	 /**
	  * Remove a row of data based on a row index.
	  */
	  public void removeData_Row(int row_index){

	    try {
		    this.Data.removeElementAt(row_index);
	    }
	    catch (ArrayIndexOutOfBoundsException e)
	    {
		  System.out.println("An error occured while trying to remove a receptor row");
		  e.printStackTrace();
	    }

	  }

	 /**
	  * Add a new row to an existing data structure.  This member assumes that the
	  * first member of of a data row is a descriptive name that is passed as a parameter
	  * and that the remaining members of the row are of type double.
	  */

	  public void add_named_row(String row_name) {
		    // find the size of a data row
		    int size = ( (Vector) Data.get(0)).size();

		    // temporary vector to add to data vector
		    Vector new_row = new Vector();

		    new_row.add(row_name.toString()); // to string method ensures that you add a copy

		    for (int i=0; i<size; i++)
		    {
			new_row.add(new Double(0.0));
		    }

		    Data.add(new_row);                // add the newly constructed row to the vector

	  }


	/**
	 * Determine the number of columns in the underlying table Data.
	 */
     public int getColumnCount() {
         return ColumnNames.length;
     }

	/**
	 * Determine the number of rows in the underlying table Data.
	 */
     public int getRowCount() {
         return Data.size();
     }

	/**
	 * Determine the columns header name of the underlying table Data.
	 */
     public String getColumnName(int col) {
         return ColumnNames[col];
     }

	/**
	 * Find the Data member associated with a row and column index.
	 */
     public Object getValueAt(int row, int col) {
	    return ((Vector) Data.get(row)).get(col);
	 }

     /*
      * JTable uses this method to determine the default renderer/
      * editor for each cell.
      */
     public Class getColumnClass(int c) {
         return getValueAt(0, c).getClass();
     }

    /*
      * Use this method to determine if a cell content can change.
      */
     public boolean isCellEditable(int row, int col) {

         return true;   // default is that you can edit everything
     }

	/**
	 * This routine prints debugging information about the table
	 * Data to the console.
	 */

	private void printDebugData() {
         int numRows = getRowCount();
         int numCols = getColumnCount();

         for (int i=0; i < numRows; i++) {
             System.out.print("    row " + i + ":");
             for (int j=0; j < numCols; j++) {
                 System.out.print("  " + ( (Vector) Data.get(i) ).get(j) );
             }
             System.out.println();
         }
         System.out.println("--------------------------");
     }

	/**
      * Update the table based on a programatic or user change in the
	 * underlying Data.  This routine will be called automatically by the Jtable
	 * if the user edits the Jtable.  If DEBUG=true debugging information will
	 * be output to the console.
      */
     public void setValueAt(Object value, int row, int col) {
         if (DEBUG) {
             System.out.println("Setting value at " + row + "," + col
                                + " to " + value
                                + " (an instance of "
                                + value.getClass() + ")");
         }

	  // indicate that model data has been changed
	  // Set_model_modified(true);

	    // set the underlying Data and update Jtable of change.
	    ( (Vector) Data.get(row) ).set(col,value);
          // Data[row][col] = value;
          fireTableCellUpdated(row, col);

         if (DEBUG) {
             System.out.println("New value of Data:");
             printDebugData();
         }
     }

	/**
	 * Output the model contents to a string.
	 */

	public String toString() {
	 String return_string = ""; // string to return model
	 int num_columns, num_rows;

	 num_columns = getColumnCount();
	 num_rows = getRowCount();

	 String[] col_names = getColumnNames();

	 // save the header info
	 for (int j=0; j<num_columns; j++){
	    return_string += col_names[j] + "\t";
	 }

	 return_string += "\n";

	 // save the data
	 for (int i=0; i<num_rows; i++){
		for (int j=0; j<num_columns; j++){
		  return_string += getValueAt(i,j) + "\t";
		}
		return_string += "\n";
	 }
	 return return_string;
	}


}