package roadway_dispersion;

/**
 * <b>Overview</b><br>
 * The coordinate class stores and accesses cartesian coordinates (x,y,z) and
 * provides member functions such as dot, horizontal_angle, and norm to perform
 * simple operations on the coordinates.
 * Note - All units in this file and the model are metric (MKS) unless otherwise stated.
 *
 * <br><br>
 *
 * <b>ACTION AND REVISION LOG</b><br>
 * <tt><pre>
 * Author         Date        Action
 * --------------------------------------------------------------------------
 * Tony Held(TH)  11-15-99  cpp file created
 * TH             11-20-99  cpp class tested, debuged, and documented
 * TH             12-04-99  operator overloading added, vector and angle functions added
 * TH             08-16-00  coordinate class ported to java
 * TH             10-23-00  class debugged & verified - no additions needed to class
 * </tt></pre>
 *
 * @author Tony Held
 * @version 1.0 (October 2001)
 */

public class coordinate implements Cloneable
{

    /**
     * Members <code>x,y,z</code> are the cartesian coordinates in standard geometrical format.
     */
    private double x;
    /**
     * Members <code>x,y,z</code> are the cartesian coordinates in standard geometrical format.
     */
    private double y;
    /**
     * Members <code>x,y,z</code> are the cartesian coordinates in standard geometrical format.
     */
    private double z;

    /**
     * Creates a new <code>coordinate</code> instance will all coordinates = 0.0
     *
     */
    public coordinate()
    {
	x = 0.0;
	y = 0.0;
	z = 0.0;
    }

    /**
     * Creates a new <code>coordinate</code> instance based on the members of another coordinate.
     *
     * @param to_be_copied a <code>coordinate</code> object whos values you want copied to a new
	* coordinate object.
     */
    public coordinate(coordinate to_be_copied)
    {
	this.x = to_be_copied.x;
	this.y = to_be_copied.y;
	this.z = to_be_copied.z;
    }

    /**
     * Creates a new <code>coordinate</code> instance with user specified coordinates.
     *
     * @param x1 a <code>double</code> value representing the x coordinate.
     * @param y1 a <code>double</code> value representing the y coordinate.
     * @param z1 a <code>double</code> value representing the z coordinate.
     */
    public coordinate(double x, double y, double z)
    {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    /**
     * This function allows you to set the horizontal parameters (x,y)
	* based on a the polar coordinate length and angle.
     */
    public void set_polar(double length, double angle_in_radians)
    {
	this.x = length * Math.cos(angle_in_radians);
	this.y = length * Math.sin(angle_in_radians);
    }

    /**
     * Get the value of x.
     * @return value of x.
     */
    public double getX()
    {
	return x;
    }

    /**
     * Set the value of x.
     * @param v  Value to assign to x.
     */
    public void setX(double v)
    {
	this.x = v;
    }

    /**
     * Get the value of y.
     * @return value of y.
     */
    public double getY()
    {
	return y;
    }

    /**
     * Set the value of y.
     * @param v  Value to assign to y.
     */
    public void setY(double v)
    {
	this.y = v;
    }

    /**
     * Get the value of z.
     * @return value of z.
     */
    public double getZ()
    {
	return z;
    }

    /**
     * Set the value of z.
     * @param v  Value to assign to z.
     */
    public void setZ(double v)
    {
	this.z = v;
    }

    /**
     * <code>set_all</code> sets all three coordinates (x,y,z) at once.
     *
     * @param x a <code>double</code> value representing the x coordinate.
     * @param y a <code>double</code> value representing the y coordinate.
     * @param z a <code>double</code> value representing the z coordinate.
     */
    public void set_all(double x, double y, double z)
    {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    /**
     * <code>norm</code> returns the 3-D norm (length) of a vector (assumes tail is at the origin).
     *
     * @return a <code>double</code> value representing the length of a 3-D vector.
     */
    public double norm()
    {
	return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * <code>horizontal_norm</code> returns the 2-D norm (length) of a vector (assumes tail is at the origin).
     * Only the <code>x,y</code> coordinates are considered; z is ignored.
     * @return a <code>double</code> value representing the length of a 2-D vector.
     */
    public double horizontal_norm()
    {
	return Math.sqrt(x * x + y * y);
    }

    /**
     * <code>dot</code> returns the dot product of the calling vector and the vector passed in the argument.
     *
     * @param coord1 a <code>coordinate</code> value to be dotted with the calling coordinate object.
     * @return a <code>double</code> value representing the 3-D dot product of the vectors.
     */
    public double dot(coordinate coord1)
    {
	return x * coord1.getX() + y * coord1.getY() + z * coord1.getZ();
    }

    /**
     * <code>horizontal_angle</code> returns the horizontal angle in radians of the (x,y) coordinates.
     * Result ranges from 0 to 2 pi and uses standard geometric orientations
     * (positive x axis is 0 or 2pi and angles measured counter-clockwise)
     *
     * @return a <code>double</code> value representing the angle between the (x,y) coordinates an the x-axis
     * in radians.
     */
    public double horizontal_angle()
    {
	double temp;

	// atan2 returns angle from - pi to +pi
	// so the return value has to be modified so that results are returned ranging from  0 to 2 pi
	temp = Math.atan2(y, x);

	if (temp < 0)
	{
	    temp = 2 * Math.PI + temp;
	}

	return temp;
    }

    /**
     * This routine will rotate the coordinate in the horizontal plane by a given angle in radians.
     * Standard geometric notations is used (i.e. x-axis is 0 or 2 pi and angles
     * are measured counter-clockwise)
     * the z component will not be affected - only then x & y will be modified
     *
     * @param radian_angle angle to rotate (in the counter clockwise direction)
     * the horizontal vector by.
     */
    public void horizontal_rotation(double radian_angle)
    {
	double length = horizontal_norm();    // length of the x,y vector
	double angle = horizontal_angle();    // angle that coordinate makes with the x-axis

	angle += radian_angle;		 // the new angle will be the old angle plus the CW rotation
	x = length * Math.cos(angle);    // move the x and y components to their new locations
	y = length * Math.sin(angle);
    }

    /**
     * Member <code>add</code> allows you to sum the coordinates of a coordinate object with another.
     * The coordinate values of the passed parameter will be added to the calling coordinate object.
     * @param coord2 a <code>coordinate</code> value to be added to the calling class
     */
    public void add(coordinate coord2)
    {
	x = x + coord2.getX();
	y = y + coord2.getY();
	z = z + coord2.getZ();
    }

    /**
     * Member <code>add</code> allows you to subtract the coordinates of a coordinate object from another.
     * The coordinate values of the passed parameter will be subtracted from the calling coordinate object.
     * @param coord2 a <code>coordinate</code> value to be subtracted from the calling class.
     */
    public void subtract(coordinate coord2)
    {
	x = x - coord2.getX();
	y = y - coord2.getY();
	z = z - coord2.getZ();
    }

    /**
     * Member <code>multiply</code> scales a coordinate object by multiplying all of its varibles by a scaling factor.
     *
     * @param multiplier a <code>double</code> value to multiply all of the coordinate varibles by.
     */
    public void multiply(double multiplier)
    {
	x = x * multiplier;
	y = y * multiplier;
	z = z * multiplier;
    }

    /**
     * <code>toString</code> outpus the x,y,z coordinate values of the coordinate object with angle and norm information.
     *
     * @return a <code>String</code> value of three coordinates.
     */
    public String toString()
    {
	return " Coordinates = (" + x + ", " + y + ", " + z + ")" + "\n Norm = " + norm() + " Horizontal Norm = "
	       + horizontal_norm() + "\n Horizontal Angle = " + horizontal_angle() + " radians = "
	       + horizontal_angle() * constants.RADIANS_TO_DEGREES + " degrees";
    }

    /**
     * <code>toString</code> outpus the x,y,z coordinate values of the coordinate object.
     *
     * @return a <code>String</code> value of three coordinates.
     */
    public String x_y_z()
    {
	return "\n Coordinates = (" + x + ", " + y + ", " + z + ")";
    }

    /**
     * <code>clone</code> method returns a coordinate object that has a copy of each
	* member of the calling object.
     *
     * @return an <code>Object</code> value
     * @exception CloneNotSupportedException if an error occurs
     */
    public Object clone() throws CloneNotSupportedException
    {
	coordinate copied_coordinate = new coordinate(this);

	return copied_coordinate;
    }

    /**
     * <code>main</code> method is only used for debugging.
     * Calling main will output the results of member function for certain test conditions
     */
    public static void main(String[] argv)
    {
	// default constructor test
	System.out.println("Testing the constructor with default arguments ");

	coordinate test1 = new coordinate();
	coordinate test2 = new coordinate();

	System.out.println(test1);
	System.out.println("Testing the constructor with specified arguments ");
	test2.set_all(10., 20., 30.);
	System.out.println(test2);
	System.out.println("Testing the get functions ");
	System.out.println("The current coordinate is \n" + test2);
	System.out.println("Get x is " + test2.getX());
	System.out.println("Get y is " + test2.getY());
	System.out.println("Get z is " + test2.getZ());
	System.out.println("Testing the set functions");
	System.out.println("Before set functions \n" + test2);
	test2.setX(100);
	test2.setY(200);
	test2.setZ(300);
	System.out.println("After set functions \n" + test2);
	System.out.println("Testing the set all function");
	test2.set_all(1000, 2000, 3000);
	System.out.println("After set_all functions \n" + test2);
	System.out.println("Testing the mathematical operators");
	test1.set_all(1, 2, 3);
	test2.set_all(10, 20, 30);

	coordinate test3 = new coordinate(0, 0, 0);

	System.out.println("Coord A is \n" + test1);
	System.out.println("Coord B is \n" + test2);
	test1.add(test2);
	System.out.println("A+B is \n" + test1);
	test1.set_all(1, 2, 3);
	test1.subtract(test2);
	System.out.println("A-B is \n" + test1);
	test2.set_all(10, 20, 30);
	test2.multiply(5);
	System.out.println("5*B is \n" + test2);
	System.out.println("Testing the norm(length) functions");
	test1.set_all(3, -1, 2);
	System.out.println("Vector u is <3,-1,2>");
	System.out.println("The actual norm is = 3.74166.  The calculated norm is " + test1.norm());
	System.out.println("The actual horizontal norm is = 3.1623.  The calculated norm is "
			   + test1.horizontal_norm());
	System.out.println("Testing the dot product function");
	test2.set_all(-4, 0, 2);
	System.out.println("Vector v is <-4,0,2>");
	System.out.println("u dot v is -8.  The calculate dot product is " + test1.dot(test2));
	System.out.println("Testing the horizontal angle function");
	System.out.println("Given the following first quadrant vectors \n"
			   + "\t u = <1,1,100> \t v= <sqr(3), 1, 20>");
	test1.set_all(1, 1, 100);
	test2.set_all(Math.sqrt(3), 1, 20);
	System.out.println("The u angle is 45 degrees.  It was calculated to be "
			   + test1.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println(" The v angle is 30 degrees.  It was calculated to be "
			   + test2.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("Given the following second quadrant vectors "
			   + "\t u = <-1,1,100> \t v= <-sqr(3), 1, 20>");
	test1.set_all(-1, 1, 100);
	test2.set_all(-Math.sqrt(3), 1, 20);
	System.out.println("The u angle is 135 degrees.  It was calculated to be "
			   + test1.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("The v angle is 150 degrees.  It was calculated to be "
			   + test2.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("Given the following third quadrant vectors "
			   + "\t u = <-1,-1,100> \t v= <-sqr(3), -1, 20>");
	test1.set_all(-1, -1, 100);
	test2.set_all(-Math.sqrt(3), -1, 20);
	System.out.println("The u angle is 225 degrees.  It was calculated to be "
			   + test1.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("The v angle is 210 degrees.  It was calculated to be "
			   + test2.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("Given the following fourth quadrant vectors "
			   + "\t u = <1,-1,100> \t v= <sqr(3), -1, 20>");
	test1.set_all(1, -1, 100);
	test2.set_all(Math.sqrt(3), -1, 20);
	System.out.println("The u angle is 315 degrees.  It was calculated to be "
			   + test1.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("The v angle is 330 degrees.  It was calculated to be "
			   + test2.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("\n Testing the cloning functions");

	coordinate test4 = new coordinate(test2);

	System.out.println("Next three coordinates should be equal \n");

	try
	{
	    coordinate test5 = (coordinate) test4.clone();

	    System.out.println(test2 + "\n" + test4 + "\n" + test5 + "\n");
	}
	catch (CloneNotSupportedException e)
	{
	    System.out.println("Clone error\n" + e);
	}

	// test the rotation method
	System.out.println("Testing the rotation methods");
	System.out.println("The vector u=<1,1,5> has an angle of 45 degrees");

	coordinate u = new coordinate(1, 1, 5);

	System.out.println(u);
	System.out.println("Rotate by 45 degrees");
	u.horizontal_rotation(45 * constants.DEGREES_TO_RADIANS);
	System.out.println(u);
	System.out.println("Rotate by -135 degrees");
	u.horizontal_rotation(-135 * constants.DEGREES_TO_RADIANS);
	System.out.println(u);
	System.out.println("Debugging complete!");
    }
}

