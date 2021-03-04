package roadway_dispersion;
import utilities.*;    // access to simple utilites that make debugging easier

/**
 * <b>Overview</b><br>
 * The <code>point</code> class creates a point source based on huangs theory of
 * dispersion in turbulent shear flow (Atmoshperic Environment 1979).  It allows
 * for the specification of a point source and its continuous source strengh.
 * Roadway links are internally represented as an array or vector of point sources.
 * With a static member representing the receptor location, in addition to a static object
 * which contains meteorlogical information, the Huang equation can be evaluated to
 * determine the concentration contribution from a single point to a downwind receptor.
 *
 * <br><br>
 * <b>The Huang Equation</b><br>
 * The huang point source pollutant dispersion equation is given below.  This equation was
 * broken into many lines so that it would appear correctly on most text editors.
 *
 * <tt><pre>
 *
 * X(x,y,z,zs,Q,p,n,v,a,b) =
 *
 *            Q                       (- y^2)         (z * zs) ^ (1 - n)/2
 * ----------------------- * exp[---------------] *  ----------------------
 *  sigma_y * sqrt(2 * PI)       (2 * sigma_y^2)          b * alpha * x
 *
 *          - a ( z^alpha + zs^alpha)                  2 * a (z * zs )^ (alpha / 2)
 * * exp[  ---------------------------  ]  *   I(-v) [ ----------------------------  ]
 *               b * alpha^2  * x                            b * alpha^2  * x
 *
 *  Where:
 *        X - concentraion of pollutant
 *        alpha = 2 + p - n
 *        v = (1-n) / alpha
 *        I[-v] = modified bessel function of the first kind of order -v
 *
 *
 * <b>ACTION AND REVISION LOG</b>
 * <tt>
 * Author          Date     Action
 * --------------------------------------------------------------------------
 * Tony Held(TH)   11-29-99  cpp file created
 * TH              01-05-99  debuging of the normalization scheme started
 * TH              11-20-99  class tested, debuged, and documented
 * TH              01-15-99  Concentration calculation checked against mathematica and results were near identical
 * TH              08-16-00  point class ported to java
 *
 * </tt></pre>
 * <br><br>
 *
 * <b>Notes:</b><br>
 * All units in metric (MKS) unless otherwise stated.   <br>
 *
 * @author Tony Held
 * @version 1.0 (October 2001)
 * @see link
 * @see highway_point_spacing
 */

public class point
{

    /**
     * Set the dispersion parameters that are common to all points.  Since meteorological conditions
	* are the same for all points the dispersion parameters object is common to all points.
     */
    public static void setDisp_param(dispersion disp)
    {
	disp_param = disp;
    }

    /**
     * <code>disp_param</code> holds all the model parameters (such as wind profile constants).
     */
    public static dispersion disp_param;

    /**
     * <code>receptor_location</code> coordinate location of the receptor - common to all points.
     */
    public static coordinate receptor_location;

    /**
     * set the receptor location that is common to all points equal to a receptor's coordinate
     */
    public static void setReceptor_location(coordinate loc)
    {
	receptor_location = new coordinate(loc);
    }

    /**
     * <code>point_location</code> coordinate of the point source.
     */
    private coordinate point_location;

    /**
     * return a reference to the point_location
     */
    public coordinate getPoint_location()
    {
	return point_location;
    }

    /**
     * set the point location to a copy of the coordinate of the
     * passed as an argument
     */
    public void setPoint_location(coordinate new_location)
    {
	this.point_location = new coordinate(new_location);
    }

    /**
     * <code>source_strength</code> in grams/sec for this point.
     */
    private double source_strength;

    /**
     * return the <code>source_strength</code> in grams/sec for this point.
     */
    public double getSource_strength()
    {
	return source_strength;
    }

    /**
      * set the <code>source_strength</code> in grams/sec for this point.
    */
    public void setSource_strength(double source_strength)
    {
	this.source_strength = source_strength;
    }

    /**
     * <code>huang_parameters</code> stores fetch & lateral distance,  receptor & emission elevation
     * for use in huang equation.
     */
    private huang  huang_parameters;

    /**
     * <code>concentration</code> is the concentration contribution from this point
	* to the static receptor.  Another routine will some the concentration contributions from all
	* points to determine the aggregate concentration contribution from the link.
     */
    private double concentration;

    /**
     * Creates a new <code>point</code> instance.  User suplies point location and emission factor
	* for the point. This constructor will intialize the huang parameters and concentration
	* contribution to 0.0.
     *
     * @param point_location a <code>coordinate</code> value
     * @param source_strength a <code>double</code> value
     */
    public point(coordinate point_location, double source_strength)
    {
	this.point_location = new coordinate(point_location);
	this.source_strength = source_strength;
	huang_parameters = new huang();
	concentration = 0.0;
    }

    /**
     * Creates a new <code>point</code> instance.  Default point location and emission factor
	* are assumed. This constructor will intialize the huang parameters and concentration
	* contribution to 0.0.
     *
     * @param point_location a <code>coordinate</code> value
     * @param source_strength a <code>double</code> value
     */
    public point()
    {
	point_location = new coordinate();
	source_strength = 0.0;
	huang_parameters = new huang();
	concentration = 0.0;
    }

    /**
     * <code>toString</code> method outputs debuging information about point,
	* receptor, and huang parameters.
     *
     * @return a <code>String</code> value with debugging information.
     */
    public String toString()
    {
	return "\nPoint souce debugging information.\n" + "The Receptor coordinate information\n"
	       + receptor_location + "\n" + "Point source true location\n" + point_location + "\n"
	       + "Huang parameters in the form:"
	       + "\ndownwind distance from source, lateral distance from source, receptor elevation, source elevation\n"
	       + huang_parameters + "\nThe wind vector is (radians,degrees) : (" + disp_param.getWind_angle()
	       + ", " + disp_param.getWind_angle() * constants.RADIANS_TO_DEGREES + ")\n"
	       + "The concentration contribution of this point is : " + concentration;
    }

    /**
     * <code>normalize</code> method uses the point_location, receptor_location, and wind_direction
	* to set the huang_parameters in the form
	* (downwind distance from source, lateral distance from source, receptor elevation).
	*
	 * <br>--------------------terminology and conceptual notes --------------------<br>
	 * To facilitate the use of the complex huang dispersion equation
	 * the huang class was created to store several of the physical parameters used in the huang equation
	 * the offset receptor is created as a convenience by subtracting the x and y coordinates from both
	 * the receptor and point coordinates.  effictively this puts the point source at the origin of your
	 * coordinate system. (note - the actual coordinates of the point and receptor are only changed locally
	 * and will not effect other subroutines or class level variables.

 	 * <br>-------------------- normilization detail--------------------<br>
	 * 1. The transformed point location is defined at x=0,y=0
	 * To acheive this the receptor must have the x & y coordinates subtraced from the receptor x & y
	 * 2. The wind vector and receptor location are conceptualized in polar coordinates
	 * 3. The polar coordinate system is rotated so that the wind vector point along +x.
	 * This is done by subtracting the wind vector angle from the ofset receptor angle
	 * (Note: Since the wind vector is the direction that the wind is blowing from
	 * this means that the wind will always be coming from the -x direction)
	 * 4. The polar coordinates are converted back to rectanglar.  If the sign of the x coordainte
	 * is negative, then the downwind fetch is positive.  The sign on the later distance does not matter.
    *
     */
    private void normalize()
    {


	double offset_angle = 0;       // angle between the offset receptor and the x axis in radians
	// coordinate system will be rotated so that the wind vector points at +x
	double receptor_length = 0;    // magnitude of the receptor vector

	// point source elevation is the huang release elevation
	// huang receptor elevation is the receptor elevation common to all points
	// receptor elevation is stored in the static receptor info
	// this data is actually redundant but will help out in clarity
	huang_parameters.setZs(point_location.getZ());
	huang_parameters.setZ(receptor_location.getZ());
	// offset the receptor so that the point source is at the horizontal origin
	huang_parameters.setX(receptor_location.getX() - point_location.getX());
	huang_parameters.setY(receptor_location.getY() - point_location.getY());

	// find the angle from the origin to the offset receptor in radians
	offset_angle = huang_parameters.horizontal_angle() - disp_param.getWind_angle();
	// find horizontal distance from the receptor to the point source
	receptor_length = huang_parameters.horizontal_norm();

	// determine along wind component ( fetch along wind vector )
	// negative because wind vector points to direction wind comes from
	huang_parameters.setX(-receptor_length * Math.cos(offset_angle));
	// lateral distance from wind vector centerline - always positive
	huang_parameters.setY(Math.abs(receptor_length * Math.sin(offset_angle)));
    }

    /**
     * <code>calculate_contribution</code> method uses huang dispersion equation
	* to find concentration contriubtion from a point source to a receptor.
     *
     */
    public void calculate_contribution()
    {
	// use the wind direction, receptor, and source information to determine
	// the huang object values
	normalize();

	// the receptor must be at least the distance tolerance from the point source in the downwind direction
	// otherwise the concentration contribution of this point to the static receptor is 0
	/** @todo check out what happens if i change this */
	if (huang_parameters.getX() < constants.DISTANCE_TOLERANCE)
	{
	    // if the point is upwind then its contribution is =0
	    concentration = 0.0;
	    // otherwise find the contribution from the huang equation (6) from the
	    // 1979 AE article titled theory of dispersion in shear flow
	}
	else
	{
	    // Form of the equation is given at the top of this text file
	    // store values need to compute X to reduce redundant function calls
	    double x = huang_parameters.getX();
	    double y = huang_parameters.getY();
	    double z = huang_parameters.getZ();
	    double zs = huang_parameters.getZs();
	    double q = source_strength;
	    double b = disp_param.getB();
	    double a = disp_param.getA();
	    double p = disp_param.getP();
	    double n = disp_param.getN();
	    double sigma = find_sigma();    // horizontal disperison
	    double alpha = 2 + p - n;
	    double v = (1 - n) / alpha;     // determines bessel function order

	    // make sure that zs and z are not exactly zero to avoid numerical stability problems
	    if (z == 0)
	    {
		z = 0.001;
	    }

	    if (zs == 0)
	    {
		zs = 0.001;
	    }

	    // to make the bessel function evaluation more clear and to simply the debugging
	    // the concentration will be calculated in the following way
	    // concentration = tmp1 * bessel_function(tmp2,-v,1)
	    double temp1 = (q / (constants.HUANG_1 * sigma))
			   * Math.exp(-Math.pow(y, 2.0) / (2.0 * Math.pow(sigma, 2.0)))
			   * Math.pow(z * zs, ((1.0 - n) / 2.0)) / (b * alpha * x)
			   * Math.exp(-a * (Math.pow(z, alpha) + Math.pow(zs, alpha))
				      / (b * Math.pow(alpha, 2.0) * x));
	    double temp2 = 2.0 * a * Math.pow(z * zs, (alpha / 2.0)) / (b * Math.pow(alpha, 2.0) * x);

	    if ( Math.abs(temp1) < 1E-200)     // equation blows up if temp1 is 0 and temp2 -> inf
	    {
	    concentration = 0.0;
	    }
	    else
	    {
	    concentration = temp1 * numerics.hyperbolics.bessel.bessel_function(temp2, -v, 1);
	    }
	}				    // end else

    }

    /**
     * <code>getConcentration</code> method returns the concentration contribution
	* from this point to the static receptor.
     *
     * @return a <code>double</code> value equal to the concentration contribution
	* from this point (assumed units g/m^3).
     */
    public double getConcentration()
    {
	return concentration;
    }

    /**
     * <code>find_sigma</code> method calculates the modified BNL lateral standard deviation
	* based on the huang parameters.
     * The BNL function is of the form sig(y) = c + d * x^e.
     *
     * @return a <code>double</code> value equal to the value sig(y)
	* from the BNL lateral standard deviation formula
     */
    private double find_sigma()
    {
	return disp_param.getC() + disp_param.getD() * Math.pow(huang_parameters.getX(), disp_param.getE());
    }

    /**
     * <code>main</code> method is only used for debugging purposes.
     *
     * @param argv a <code>String[]</code> value (not used).
     */
    public static void main(String[] argv)
    {
	System.out.println("\nThis code has been designed to debug the point class normalization scheme\n"
			   + "and the huang concentration calculation formula\n\n");

	// need to initialize the static members
	coordinate test_receptor = new coordinate();    // coordinate to hold receptor info
	coordinate test_location = new coordinate();    // location of point source
	double     test_source_strength = 20;		// source strength for the sample point
	dispersion test_dispersion = new dispersion();    // various dispersion paramenters - most important is wind

	test_dispersion.setA(1.6);		      // so that the 10 m wind speed is 4 m/s for a p=.4
	// so that the 10 m K is 4 m for a n=1
	// NOTE: I don't have a real sound reason for this estimate
	// but i need to do something that seems ballpark reasonable for diagnostic purposes
	test_dispersion.setB(0.4);
	test_dispersion.setC(1.5);		      // just guessing an initial dispersion of 1.5 meters to get things rolling
	test_dispersion.setD(0.36);		      // based on BNL neutral conditions
	test_dispersion.setE(0.86);		      // "
	test_dispersion.setN(1.);		      // based on huang eqn 34 this is for nuetral conditions
	test_dispersion.setP(.4);		      // based on huang figure 1 this seems reasonable for a 10 m ref
	test_dispersion.setWind_angle(3.1415 / 2);    // wind coming approximately from the west

	// point object to test whether coordinate tranforms are working properly
	point test_point = new point(test_location, test_source_strength);

	test_point.disp_param = test_dispersion;	 // set point source dispersion characteristics
	test_point.receptor_location = test_receptor;    // location of receptor

	// results found graphically indicated by *** and are used for a double check
	// point source in quadrent I
	test_point.point_location.set_all(2, 2, 5);
	// receptor in quadrent I
	receptor_location.set_all(10, 10, 1);
	multiple_wind_angles(test_point);    // rotate the wind angle every 15 degrees
	// *** for wind angle of 30 deg fetch =~ -11	normal = ~3
	// *** for wind angle of 210 deg fetch =~ 11	normal = ~3
	// receptor in quadrent II
	receptor_location.set_all(-10, 10, 2);
	multiple_wind_angles(test_point);    // rotate the wind angle every 15 degrees
	// *** for wind angle of 135 deg fetch = ~-14.25 normal = ~2.75
	// *** for wind angle of 315 deg fetch = ~ 14.25 normal = ~2.75
	// receptor in quadrent III
	receptor_location.set_all(-10, -10, 3);
	multiple_wind_angles(test_point);    // rotate the wind angle every 15 degrees
	// *** for wind angle of 135 deg fetch = ~0 normal = ~17
	// *** for wind angle of 315 deg fetch = ~0 normal = ~17
	// receptor in quadrent IV
	receptor_location.set_all(10, -10, 4);
	multiple_wind_angles(test_point);    // rotate the wind angle every 15 degrees
	// *** for wind angle of 60 deg fetch =~ 6.25	normal = ~13.25
	// *** for wind angle of 240 deg fetch =~-6.25 	normal = ~13.25
	System.out.println("\n\nDebugging Complete\n\n");
    }

    /**
     *
     * The multiple_wind_angles routine is only used for debuggin purposes.
	* If facilitates the consideration of various wind angles to simulate coordinate rotations.
     */
    private static void multiple_wind_angles(point in_point)
    {
	for (double wind = 0; (2 * constants.PI + constants.LOOPING_EPSILON) > wind; wind += constants.PI / 12.0)
	{
	    disp_param.setWind_angle(wind);    // set the wind
	    // rotate the coordinate system based on point & receptor locations and wind vector
	    in_point.calculate_contribution();
	    System.out.println(in_point);
	}
    }
}

