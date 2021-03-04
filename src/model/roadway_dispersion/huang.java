package roadway_dispersion;
/**
 * <b>Overview</b><br>
 * The huang class provides a convenient way to clarify the evaluation
 * of the huang dispersion equation by grouping fetch, latteral distance,
 * elevation of source, and receptor elevation information for of a point-receptor pair.
 * for use in the huang point source dispersion formula.
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


public class huang
{

    /**
     * variable <code>x</code> represents receptor distance downwind from the source (m).
     */
    double x;

    /**
     * variable <code>y</code> represents the latteral distance from the downwind centerline to the receptor (m).
     */
    double y;

    /**
     * variable <code>z</code> represents the the elevation of the receptor (m).
     */
    double z;

    /**
     * variable <code>zs</code> represents the elevation of the point source (m).
     */
    double zs;

    /**
     * <code>set_all</code> method sets all of the huang parameters at once.
     *
     * @param x a <code>double</code> value representing the receptor distance downwind from the source (m).
     * @param y a <code>double</code> value representing the latteral distance from the downwind centerline to the receptor (m).
     * @param z a <code>double</code> value representing the elevation of the receptor (m).
     * @param zs a <code>double</code> value representing the elevation of the point source (m).
     */
    void set_all(double x, double y, double z, double zs)
    {
	this.x = x;
	this.y = y;
	this.z = z;
	this.zs = zs;
    }

    /**
     * <code>toString</code> method outputs the huang parameters for debugging purposes.
     *
     * @return a <code>string</code> value with all the huang parameters.
     */
    public String toString()
    {
	return "Huang Parameters x: " + x + "\ty: " + y + "\tz: " + z + "\tzs: " + zs + "";
    }

    /**
     * <code>horizontal_angle</code> method finds the horizontal angle between the x-axis
     * (measured counterclockwise) in radians and the coordiante (x,y) - using standard
     * geometric orientations (positive x axis is 0 or 2pi).
     *
     * @return a <code>double</code> value representing the horizontal angle.
     */
    public double horizontal_angle()
    {
	double temp;

	temp = Math.atan2(y, x);

	// atan2 returns angle from - pi to +pi
	// the next if statement will convert angle range from 0 to 2 pi
	if (temp < 0)
	{
	    temp = 2 * Math.PI + temp;
	}

	return temp;
    }

    /**
     * <code>horizontal_norm</code> method calculates the norm (length) of the horizontal vector
     * (i.e. only considers (x,y) information by projecting onto z=0 surface).
     *
     * @return a <code>double</code> value representing the projected horizontal vector length
     */
    double horizontal_norm()
    {
	return Math.sqrt(x * x + y * y);
    }

    /* ------Constructors-------- */

    /**
     * Creates a new <code>huang</code> instance with default all members = 0.0 m.
     *
     */
    public huang()
    {
	this.x = 0.0;
	this.y = 0.0;
	this.z = 0.0;
	this.zs = 0.0;
    }

    /**
     * Creates a new <code>huang</code> instance with specified parameter values.
     *
     * @param x a <code>double</code> value representing the receptor distance downwind from the source (m).
     * @param y a <code>double</code> value representing the latteral distance from the downwind centerline to the receptor (m).
     * @param z a <code>double</code> value representing the elevation of the receptor (m).
     * @param zs a <code>double</code> value representing the elevation of the point source (m).
     */
    public huang(double x, double y, double z, double zs)
    {
	this.x = x;
	this.y = y;
	this.z = z;
	this.zs = zs;
    }

    /* ------Get-Set Pairs-------- */

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
     * Get the value of zs.
     * @return value of zs.
     */
    public double getZs()
    {
	return zs;
    }

    /**
     * Set the value of zs.
     * @param v  Value to assign to zs.
     */
    public void setZs(double v)
    {
	this.zs = v;
    }

    /**
     * <code>main</code> method is used for debugging purposes only.
     *
     */
    public static void main(String[] argv)
    {
	System.out.println("Testing the constructor with default arguments");

	huang test1 = new huang();
	huang test3 = new huang();

	System.out.println(test1);
	System.out.println("Testing the constructor with specified arguments ");

	huang test2 = new huang(10, 20, 30, 40);

	System.out.println(test2);
	System.out.println("Testing the get functions ");
	System.out.println("Get x is " + test2.getX());
	System.out.println("Get y is " + test2.getY());
	System.out.println("Get z is " + test2.getZ());
	System.out.println("Get zs is " + test2.getZs());
	System.out.println("Testing the set functions \n");
	test2.setX(100);
	test2.setY(200);
	test2.setZ(300);
	test2.setZs(400);
	System.out.println("After set functions");
	System.out.println(test2);
	System.out.println("Testing the set all function");
	test2.set_all(1000, 2000, 3000, 4000);
	System.out.println(test2);
	System.out.println("Testing the norm(length) functions");
	test1.set_all(3, -1, 0, 0);
	System.out.println("Vector u is <3,-1,0,0>");
	System.out.println("The actual horizontal norm is = 3.1623.  The calculated norm is "
			   + test1.horizontal_norm());
	System.out.println("Testing the horizontal angle function");
	System.out.println("Given the following first quadrant vectors \n"
			   + "\t u = <1,1,0,0> \t v= <sqr(3), 1, 0, 0>");
	test1.set_all(1, 1, 0, 0);
	test2.set_all(Math.sqrt(3), 1, 0, 0);
	System.out.println(" The u angle is 45 degrees.  It was calculated to be "
			   + test1.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println(" The v angle is 30 degrees.  It was calculated to be "
			   + test2.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("Given the following second quadrant vectors \n"
			   + "\t u = <-1,1,0,0> \t v= <-sqr(3), 1, 0,0>");
	test1.set_all(-1, 1, 0, 0);
	test2.set_all(-Math.sqrt(3), 1, 0, 0);
	System.out.println(" The u angle is 135 degrees.  It was calculated to be "
			   + test1.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println(" The v angle is 150 degrees.  It was calculated to be "
			   + test2.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("Given the following third quadrant vectors \n"
			   + "\t u = <-1,-1,0,0> \t v= <-sqr(3), -1, 0,0>");
	test1.set_all(-1, -1, 0, 0);
	test2.set_all(-Math.sqrt(3), -1, 0, 0);
	System.out.println(" The u angle is 225 degrees.  It was calculated to be "
			   + test1.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println(" The v angle is 210 degrees.  It was calculated to be "
			   + test2.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("Given the following fourth quadrant vectors \n"
			   + "\t u = <1,-1,0,0> \t v= <sqr(3), -1, 0,0>");
	test1.set_all(1, -1, 0, 0);
	test2.set_all(Math.sqrt(3), -1, 0, 0);
	System.out.println(" The u angle is 315 degrees.  It was calculated to be "
			   + test1.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println(" The v angle is 330 degrees.  It was calculated to be "
			   + test2.horizontal_angle() * constants.RADIANS_TO_DEGREES);
	System.out.println("Debugging complete!\n");
    }
}

