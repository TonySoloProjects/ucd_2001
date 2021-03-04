/**
 * <b>Overview</b><br>
 * Thee point vector class provides a modular and flexible way to
 * generate point source objects for use in the huang/roadway dispersion model.
 * This class alows the creation of points with locations that have a piece
 * wise continuous functional form.  The highway link only uses linear point spacing.
 * However, one can use this object to create spacing routines with any functional form.
 * For instance, point sources can be exponentially spaced.
 *
 * <br>
 * ACTION AND REVISION LOG
 * Author         Date      Action
 * --------------------------------------------------------------------------
 * Tony Held(TH)  09-28-00  class skeleton created
 * TH             11-07-00  class debuged and complete
 * </tt></pre>
 */
package roadway_dispersion;

import java.util.*;    // access to vector class
import utilities.*;    // access to the debugin classes

/**
 * <code>point_vector</code> class provides a modular and flexible way to generate point source locations
 * for use in the huang/roadway dispersion model.  Point locations can be created by specifying the spacing
 * between two end points or by specifying a functional of point placement between end point allowing the
 * user to select the total number of points.
 *
 * @author Tony Held
 * @version 1.0
 * @since 1.0
 */
public class point_vector
{

    /**
     * static member used to indicate that the functional form of points added to the point_profile
     * vector is linear.
     */
    public final static int LINEAR = 0;
    private Vector	    point_profile;    // vector that will contain the point profile
    private double[]	    point_array;      // point profile will ultimately be returned as an array

    /**
     * Default constructor creates a new vector object to store point locations, the array that will
     * utimately return the contents of this vector is set to null.
     */
    public point_vector()
    {
	point_profile = new Vector(0, 10);    // vector will initially be of size 0 and increment by 10
	point_array = null;		      // reset the array that returns the vector contents
    }

    /**
     * <code>add_points</code> based on a lower bound, upper bound, & desired point spacing.
     * When spacing is specified the points will always be equidistant (i.e. linear algorithm used).
     * Points will be equally centered on the mid point of the range.
     *
     * @param lower_bound a <code>double</code> value representing the lowest point value to add
     * @param upper_bound a <code>double</code> value representing the upper point value to add
     * @param spacing an <code>double</code> value indicating the distance between equaly spaced points
     */
    public void add_points(double lower_bound, double upper_bound, double spacing)
    {
	// ensure that the lower bound is below the upper bound
	if (lower_bound > upper_bound)
	{
	    System.err.println("An attempt was made to add points to a profile where the upper bound "
			       + "\n was not equal to or greater than the lower bound. "
			       + "\n If you choose to continue this point addition will simply be ignored");
	    debugging.exception_handeler();    // let the user continue even if there is a problem
	}

	// ensure that at lease one point can fit between the bounds
	if (spacing > upper_bound - lower_bound)
	{
	    System.err.println("An attempt was made to add a points with a spacing larger than the bounds. "
			       + "\n If you choose to continue this point addition will simply be ignored");
	    debugging.exception_handeler();    // let the user continue even if there is a problem
	}

	// place the points equally centered on the mid point of the range
	// find how many complete intervals are available in the range using integer arithmetic
	double intervals = Math.floor((upper_bound - lower_bound) / spacing);
	double num_points = intervals + 1;    // there will be one more point then intervals
	// find the extra space that will not be covered by the points
	// calculation will allow the points to be easily placed centered on the range midpoint
	// basic concept here is to find the start point that will result in a centered series
	// and then add the points with the desired spacing offset
	double range_residual = (upper_bound - lower_bound) - intervals * spacing;
	double start_point = range_residual / 2 + lower_bound;

	for (int i = 0; i < num_points; i++)
	{
	    // need to use the double object for storage in a vector
	    Double value = new Double(start_point + i * spacing);

	    point_profile.add(value);
	}
    }

    /**
     * <code>add_points</code> based on a lower bound, upper bound, function type, & total number of points desired
     *
     * @param lower_bound a <code>double</code> value representing the lowest point value to add
     * @param upper_bound a <code>double</code> value representing the upper point value to add
     * @param num_points an <code>int</code> value specifying the total number of points to be added
     * @param function_type an <code>int</code> value indicating the type of profile characteristics of
     * the points to be added.
     */
    public void add_points(double lower_bound, double upper_bound, int num_points, int function_type)
    {
	// ensure that the lower bound is below the upper bound
	if (lower_bound > upper_bound)
	{
	    System.err.println("An attempt was made to add points to a profile where the upper bound "
			       + "\n was not equal to or greater than the lower bound. "
			       + "\n If you choose to continue this point addition will simply be ignored");
	    debugging.exception_handeler();    // let the user continue even if there is a problem
	}

	// ensure that a positive number of points are added
	if (num_points < 1)
	{
	    System.err.println("An attempt was made to add a non positive number of points. "
			       + "\n If you choose to continue this point addition will simply be ignored");
	    debugging.exception_handeler();    // let the user continue even if there is a problem
	}

	// if there is only one point to be added - it can't really be placed in a linear function
	if (num_points == 1)
	{
	    System.err.println("An attempt was made to add only one point with a linear profile. "
			       + "\nYou need at least two points to make a linear profile"
			       + "\nUse the other add_points method if you only want to add one point."
			       + "\n If you choose to continue this point addition will simply be ignored");
	    debugging.exception_handeler();    // let the user continue even if there is a problem
	}

	switch (function_type)
	{

	case point_vector.LINEAR:	       // linear - place the points equally spaced out
	    // equal spacing between points
	    double spacing = (upper_bound - lower_bound) / (num_points - 1);

	    for (int i = 0; i < num_points; i++)
	    {
		// need to use the double object for storage in a vector
		Double value = new Double(lower_bound + i * spacing);

		point_profile.add(value);
	    }

	    break;			       // break from the switch statement

	default:
	    // an unknown function was selected
	    System.err.println("An attempt was made to add points based on an unknown function type "
			       + "\n The selected type was " + function_type
			       + "\n If you choose to continue this point addition will simply be ignored");
	    debugging.exception_handeler();    // let the user continue even if there is a problem
	}
    }

    /**
     * Add a single point to the vector.
     */
    public void add_points(double point_location)
    {
	// need to use the double object for storage in a vector
	Double value = new Double(point_location);

	point_profile.add(value);
    }

    /**
     * <code>getPoint_array</code> calls create_array() which will create a
     * type double array to return the contents of the point_profile vector.
     * @returns the double array that is created from the point_profile vector.
     */
    public double[] getPoint_array()
    {
	create_array();    // make the array from the point_profile vector

	return point_array;
    }

    /**
     * main class is only used for debugin purposes
     */
    public static void main(String[] argv)
    {
	// create an instance of the point_vector class
	point_vector test_vector = new point_vector();

	System.out.println(test_vector);
	// Adding points to the profile by specifying the number of points and the
	// functional form of the point positioning
	test_vector.add_points(0, 10.0, 5, point_vector.LINEAR);
	test_vector.create_array();
	System.out.println("There should be 5 points linearly spaced between 0 and 10");
	System.out.println(test_vector);

	// Additing points to the profile by specifying the spacing of the points
	test_vector = new point_vector();    // create an instance of the point_vector class

	test_vector.add_points(1.0, 100.0, 20.0);
	test_vector.create_array();
	System.out.println("There should be a spacing of 20.0 between 1 and 100");
	System.out.println(test_vector);

	// create a compound vector with multiple add_point calls
	test_vector = new point_vector();

	test_vector.add_points(0.0, 9.5, 0.5);      // bottom spacing
	test_vector.add_points(10.0, 20.0, 1.0);    // middle spacing
	test_vector.add_points(25.0, 50.0, 5.0);    // top spacing
	test_vector.add_points(1999.0);		    // single point specified
	test_vector.create_array();
	System.out.println("Points should be spaced 0.5 from 0 to 9.5 then " + "spaced 1.0 from 10 to 20 then "
			   + "spaced 5.0 from 25 to 50" + "the last point should be @ 1999");
	System.out.println(test_vector);
	// test error checking of bad calles
	System.out.println("Testing error checking");
	test_vector.add_points(20, 10, 5.0);
	test_vector.add_points(10, 29, 3, 4);
    }

    /**
     * toString method is used to output class information to facilitate debugging
     */
    public String toString()
    {
	String return_string;

	return_string = "\nDebugging the point generation scheme";
	return_string += "\nVector size (capacity) is " + point_profile.size() + "(" + point_profile.capacity()
			 + ")";

	if (point_array == null)
	{
	    return_string += "\nArray is null and has no members";
	}
	else
	{
	    return_string += "\nArray size is " + point_array.length + " members are below";

	    for (int i = 0; i < point_array.length; i++)
	    {
		return_string += "\nPoint " + i + " = " + point_array[i];
	    }
	}

	return return_string;
    }

    /**
     * Create an array of primative type double from the point_vector internal vector.
     * This will save space and speed calculations for subsequent manipulation.
     * This method will always be called prior to the getPoint_array method.
     */
    private void create_array()
    {
	// create an array of the wrapper class Double
	// and redim the primative double array to the correct size
	Double[] temp_array = new Double[point_profile.size()];

	point_array = new double[point_profile.size()];
	// copy the vector elements to the temp array
	temp_array = (Double[]) point_profile.toArray(temp_array);

	// populate the primative double array type with the elements of the Double wrapper class
	for (int i = 0; i < point_profile.size(); i++)
	{
	    point_array[i] = temp_array[i].doubleValue();
	}
    }
}

