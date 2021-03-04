package roadway_dispersion;

import java.util.*;	      // gain access to the vector object
import utilities.*;	      // access to simple utilites that make debugging/naming easier
import java.io.*;	      // basic file access
import com.braju.format.*;    // access to fprintf formating functions

/**
 * <b>Overview</b><br>
 * <code>link</code> is the superclass for all types of roadway links.
 * The link class creates arrays of point objects to model emissions from a link. The
 * <code>highway_link</code> is the only subclass of the <code>link</code> that was
 * included with the original UCD 2001 model.
 *
 * <br> <br>
 * <b>Notes</b>
 * All units in this file and the model are metric (MKS) unless otherwise stated.
 *
 * @author Tony Held
 * @version 1.0 (October 2001)
 * @see point
 * @see constants
*/

public abstract class link implements Cloneable
{

    /**
     * This definition allows for the clone method to be virtual.
     */
    abstract public Object clone() throws CloneNotSupportedException;

    /**
     * This routine removes the point array for a link and sets the receptor
	* concentration contribution to zero for the link.  This routine should be
	* used to release resources after a link is no longer needed.
     */
    public void clear_link_concentration()
    {
	total_concentration = 0.0;     // reset the concentration
	point_array = new point[0];    // reset the point array
    }

    /**
     * Routine to determine the concentration contribution
	* from each point source used to represent the link and to superposition all the concentration
	* contributions to the <code>total_concentration</code> data member.
     */
    public void calculate_link_concentration()
    {
	// create the point array for the link type
	create_point_array();

	int total_points = point_array.length;    // total number of points in the point array

	this.total_concentration = 0.0;    // reset the total concentration for this link

	// loop through each point and find the concentraiton associated with the common receptor
	// add each point's concentration to the total link concentraiton
	for (int i = 0; i < total_points; i++)
	{
	    point_array[i].calculate_contribution();

	    total_concentration += point_array[i].getConcentration();
	    // debug_output
	    // System.out.println("point " + i + " cumulative concentrtion = " + total_concentration+ point_array[i].getPoint_location().x_y_z() );
	    // System.out.println(i);
	}
    }

    /**
     * The member is the total concentration contribution ( grams / m^3 ) to a receptor
	* from this link object.
     */
    private double total_concentration;

    /**
     * Get total_concentration in ( grams / m^3 ).
     */
    public double getTotal_concentration()
    {
	return total_concentration;
    }

    /**
     * Create a point array representing the sub class link and store the point array in the
	* member variable <code>point_array</code>.
     */
    public abstract void create_point_array();

    /**
     * This routine returns the <code>point_array</code> contents
	* as a 2-d double matrix in a form that facilitates analysis in an external program
	* such as matlab.
     */
    public double[][] get_point_array_concentrations()
    {
	// dim return array based on the size of the point_array
	// point array will have the format
	// (x,y,z, concentration)
	double[][] return_array = new double[point_array.length][4];

	for (int i = 0; i < point_array.length; i++)
	{
	    return_array[i][0] = point_array[i].getPoint_location().getX();
	    return_array[i][1] = point_array[i].getPoint_location().getY();
	    return_array[i][2] = point_array[i].getPoint_location().getZ();
	    return_array[i][3] = point_array[i].getConcentration();
	}

	return return_array;
    }

    /**
     * This member facilitates default naming of link objects.
     */
    public default_naming naming;


    /**
     * The variable <code>start_location</code> is the centerpoint of the start of a roadway link.
     * Start and end locations should correspond with the direction of traffic flow.
     */
    coordinate		  start_location;

    /**
     * The variable <code>end_location</code> is the centerpoint of the end of a roadway link.
     * Start and end locations should correspond with the direction of traffic flow.
     */
    coordinate		  end_location;

    /**
     * The variable <code>width</code> is the width of the traveled way (meters).
     */
    double		  width;

    /**
     * The variable <code>receptor_location</code> is the coordinate of the receptor
	* that potentially receives emissions transported from a a roadway link.  The UCD 2001
	* model creates a copy of each link for each receptor so that the point source locations
	* for each link can be optomized for the link-receptor position.  The receptor location is
	* common to all links associated to a receptor object.  This explains why the receptor location
	* is static and public.
     */
    static coordinate     receptor_location;

    /**
     * This is a routine to set the receptor location.
     */
    public static void setReceptor_location(coordinate v)
    {
	receptor_location = new coordinate(v);
    }

    /**
     * This routine returns a reference to the receptor location that is common to all the
	* links associated with the receptor.
     */
    public static coordinate getReceptor_location()
    {
	return receptor_location;
    }

    /**
     * <code>disp_param</code> holds all the model parameters (such as wind profile constants).
	* The dispersion parameters are not currently used by the link class in the UCD 2001 model.
     */
    private static dispersion disp_param;

    /**
     * Set the model parameters for all the links equal to a copy a dispersion object.
     */
    public static void setDisp_param(dispersion v)
    {
	// disp_param = new dispersion(v);      // make a copy of the dispersion parameters
	disp_param = v;    // use a common copy of the dispersion parameters
    }

    /**
     * Returns a reference to the receptor location common to all links.
     */
    public static dispersion getDisp_param()
    {
	return disp_param;
    }

    /**
     * This member stores the point sources used to represent
	* a link object.
     */
    private point[] point_array;

    /**
	* Returns the number of point sources representing the link.
	*/
    public int get_number_points()
    {
    return point_array.length;
    }

    /**
     * Represents the length of the link along the centerline based on the
	* <code>start_location</code> and <code>end_location</code> coordinates.
	* This member is private and can only be determined by the
	*  <code>determine_link_geometry</code> routine.
     */
    private double  link_length;

     /**
     * Represents the angle that the link makes with the x-axis based on the
	* <code>start_location</code> and <code>end_location</code> coordinates.
	* This member is private and can only be determined by the
	*  <code>determine_link_geometry</code> routine.
     */
     double	    link_angle;

    /**
     * Determines the link length and angle based on the
	* <code>start_location</code> and <code>end_location</code> coordinates.
     */
    public void determine_link_geometry()
    {
	coordinate temp_coord;    // temporary coordinate to make calculations clearer

	temp_coord = new coordinate(end_location);    // temp_coord is a copy of the end point location

	// offset the location so that start of link is at the origin
	temp_coord.subtract(start_location);

	link_length = temp_coord.horizontal_norm();    // determine length of link
	link_angle = temp_coord.horizontal_angle();    // find angle link makes in radians
    }

    /**
     * Returns the coordinates of the four corners of the roadway link.  This routine is
	* helpful if one desires to plot the link with an external program such as matlab.
     */
    public coordinate[] getCorners()
    {
	coordinate[] corners = new coordinate[4];
	// this coordinate will be added and subtracted from the start and end points to find the link corners
	coordinate   offset = new coordinate();
	// find angle that is perpendicular to the link centerline
	double       perp_angle = link_angle + 90. * constants.DEGREES_TO_RADIANS;

	offset.set_all(Math.cos(perp_angle), Math.sin(perp_angle), 0.0);    // make a unit vector
	offset.multiply(width / 2.0);					    // extend the unit vector half the width of the link

	corners[0] = new coordinate(start_location);    // initialize the corner points
	corners[1] = new coordinate(end_location);
	corners[2] = new coordinate(end_location);
	corners[3] = new coordinate(start_location);

	corners[0].add(offset);			     // add the offset to the begin and end points
	corners[1].add(offset);
	offset.horizontal_rotation(constants.PI);    // rotate the offset around 180 degrees
	corners[2].add(offset);			     // add the offset to the begin and end points
	corners[3].add(offset);

	return corners;
    }

    /**
     * Return a reference to the array of points that represents the link object.
     */
    public point[] getPoint_array()
    {
	return point_array;
    }

    /**
     * Set the link point array to a point array reference.
     * This does not make a copy of of the point array to speed up the calculations.
     */
    public void setPoint_array(point[] point_array)
    {
	this.point_array = point_array;
    }

    /**
     * Return the link length.
     */
    public double getLink_length()
    {
	return link_length;
    }

    /**
     * Return the link angle in radians.
     */
    public double getLink_angle()
    {
	return link_angle;
    }
    /* -----------------------------Get-Set Pairs--------------------------------- */

    /**
     * Get the value of start_location.
     * @return value of start_location.
     */
    public coordinate getStart_location()
    {
	return start_location;
    }

    /**
     * Set the value of start_location - to a copy of the coordinate passed to this function
     * @param v  Value to assign to start_location.
     */
    public void setStart_location(coordinate v)
    {
	this.start_location = new coordinate(v);    // make a copy of the coordinate
    }

    /**
     * Get the value of end_location.
     * @return value of end_location.
     */
    public coordinate getEnd_location()
    {
	return end_location;
    }

    /**
     * Set the value of end_location - to a copy of the coordinate passed to this function.
     * @param v  Value to assign to end_location.
     */
    public void setEnd_location(coordinate v)
    {
	this.end_location = new coordinate(v);
    }

    /**
     * Get the value of width.
     * @return value of width.
     */
    public double getWidth()
    {
	return width;
    }

    /**
     * Set the value of width.
     * @param v  Value to assign to width.
     */
    public void setWidth(double v)
    {
	this.width = v;
    }

    /**
     * remove all the point objects from the link to save memmory
     */
    public void clear_points()
    {
	point_array = null;
    }

    /**
     * Allows the link to be printed to a file with a file name based on the link
	* name and receptor ownership.
     */
    public void print_link_to_file()
    {
	String default_name = this.naming.getName() + ".txt";

	print_link_to_file(default_name);
    }

    /**
     *
     * This method will output the contents of the link to a file
     * with named [link_name].txt so that the link can be evaluated in an external program.
	* Each row of the file has 4 members seperated by commas.  Not all lines contain 4 pieces
	* of information and require the use of zero pads a formating place holders.
     * The first line of the text file is the wind speed and direction with two zero pads ending the line.
     * The second line is the receptor location with a single zero pad.
     * The next 4 lines of the file will be the four corners
     * of the highway link in the format (x,y,z,0).
     * The remainder of the lines will be the point coordinate and
     * concentration contribution of all the points in the link.
     * Format for those lines is (x,y,z, concentration)
     * @param name of file that will receive output from the link
     *
     */
    public void print_link_to_file(String file_name)
    {
	// get a reference to the point array
	point[]      point_info = getPoint_array();
	// find out the corners of the link
	coordinate[] corners = new coordinate[4];

	corners = getCorners();

	// open up the output file named out_file
	try
	{
	    // use printf to output to files
	    Parameters  p = null;
	    PrintWriter out_file = new PrintWriter(new FileOutputStream((file_name)), true);

	    // output the wind speed and direction with a 2 zero pads at the end
	    p = new Parameters();

	    Format.fprintf(out_file, "%.1f %.3f 0.0 0.0\n",
			   p.add(disp_param.wind_speed(4.5)).add(met_processor.geometric_to_met_angle(disp_param.getWind_angle()
						       * constants.RADIANS_TO_DEGREES)));

	    // output the receptor location with a zero pad at the end
	    p = new Parameters();

	    Format.fprintf(out_file, "%.1f %.1f %.1f 0.0\n",
			   p.add(getReceptor_location().getX()).add(getReceptor_location().getY()).add(getReceptor_location().getZ()));

	    // output the corners with a zero pad at the end
	    for (int i = 0; i < 4; i++)
	    {
		p = new Parameters();

		Format.fprintf(out_file, "%.1f %.1f %.1f 0.0\n",
			       p.add(corners[i].getX()).add(corners[i].getY()).add(corners[i].getZ()));
	    }

	    // output the point coordinates and concentration contribution
	    for (int i = 0; i < point_info.length; i++)
	    {
		Format.fprintf(out_file, "%.1f %.1f %.1f %g\n",
			       p.add(point_info[i].getPoint_location().getX()).add(point_info[i].getPoint_location().getY()).add(point_info[i].getPoint_location().getZ()).add(point_info[i].getConcentration()));
	    }
	}
	catch (Exception e)
	{
	    System.out.println("Error outputing link info to file");
	}
    }
}

