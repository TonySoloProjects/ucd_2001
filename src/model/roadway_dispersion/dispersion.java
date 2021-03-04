package roadway_dispersion;

/**
 * <b>Overview</b><br>
 * Class <code>dispersion<code> stores all the variables used to evaluate
 * the lateral dispersion and the huang dispersion equation.
 * Saving all these variables in a single
 * object cleans up the point source code and makes the program more clear.
 *
 *
 * <br><br>
 * <b>Essential Formulas and Relationships </b>                                     <br>
 * Wind thought to follow power law form of u(z)=a z^p                              <br>
 * Vertical eddy diffusivity thought to follow power law K= b z^n                   <br>
 * Modified BNL lateral standard deviation function of form sig(y) = c + d * x^e    <br>
 * Members:
 * a - const in wind power law function                                             <br>
 * b - constant in vertical eddy diffusivity function                               <br>
 * c - constant in lateral standard deviation function (LSDF)                       <br>
 * d - constant in LSDF                                                             <br>
 * e - exponent in LSDF                                                             <br>
 * n - exponent in eddy diffusivity power law function                              <br>
 * p - exponent in wind power law function                                          <br>
 * wind_angle - direction that the wind is blowing from in radians (see special note about sign convention)
 *
 * <br><br>
 *
 * <b>--------------------Important Sign Convention--------------------</b><br>
 * The wind angle is defined in degrees from the standard geometrical angle definition.
 * The positive x axis is zero and angles are positive in the counter-clockwise direction.
 * For example the positive y axis is 90 degrees and the negative y direction is 270 degrees.
 * The wind angle is defined as the direction that wind is blowing FROM.
 * If the north is defined as the +y axis and the west is -x axis a wind from
 * the northwest would be between 90 and 180 degrees
 *
 * <br><br>
 *
 * <b>Notes:</b><br>
 * In this and all other model files all units in metric (MKS) unless otherwise stated.   <br>
 *
 * @author Tony Held
 * @version 1.0 (October 2001)
 */
public class dispersion
{

    /**
     * Creates a new <code>dispersion</code> instance where user specifies all of the dispersion parameters.
     *
     * @param a a <code>double</code> value representing a const in wind power law function.
     * @param b a <code>double</code> value representing the exponent in wind power law function.
     * @param c a <code>double</code> value representing a constant in lateral standard deviation function (LSDF).
     * @param d a <code>double</code> value representing a constant in lateral standard deviation function (LSDF).
     * @param e a <code>double</code> value representing a constant in lateral standard deviation function (LSDF).
     * @param p a <code>double</code> value representing the exponent in wind power law function
     * @param n a <code>double</code> value representing exponent in eddy diffusivity power law function
     * @param wind_angle a <code>double</code> value representing the wind angle measured from 0 to 2 pi radians
     */
    public dispersion(double a, double b, double c, double d, double e, double n, double p, double wind_angle)
    {
	this.a = a;
	this.b = b;
	this.c = c;
	this.d = d;
	this.e = e;
	this.n = n;
	this.p = p;
	this.wind_angle = wind_angle;
    }

    /**
     * Creates a new <code>dispersion</code> instance with members identical to the constructor argument.
     *
     * @param to_be_copied a <code>dispersion</code> value is the dispersion object that you want to make an identical copy of.
     */
    public dispersion(dispersion to_be_copied)
    {
	this.a = to_be_copied.getA();
	this.b = to_be_copied.getB();
	this.c = to_be_copied.getC();
	this.d = to_be_copied.getD();
	this.e = to_be_copied.getE();
	this.n = to_be_copied.getN();
	this.p = to_be_copied.getP();
	this.wind_angle = to_be_copied.getWind_angle();
    }

    /**
     * Creates a new <code>dispersion</code> instance with all parameters =0.0.
     *
     */
    public dispersion()
    {
	this.a = 0.0;
	this.b = 0.0;
	this.c = 0.0;
	this.d = 0.0;
	this.e = 0.0;
	this.n = 0.0;
	this.p = 0.0;
	this.wind_angle = 0.0;
    }

    /**
     * Sett all of the dispersion members at once.
     *
     * @param a a <code>double</code> value representing a const in wind power law function.
     * @param b a <code>double</code> value representing the exponent in wind power law function.
     * @param c a <code>double</code> value representing a constant in lateral standard deviation function (LSDF).
     * @param d a <code>double</code> value representing a constant in lateral standard deviation function (LSDF).
     * @param e a <code>double</code> value representing a constant in lateral standard deviation function (LSDF).
     * @param p a <code>double</code> value representing the exponent in wind power law function
     * @param n a <code>double</code> value representing exponent in eddy diffusivity power law function
     * @param wind_angle a <code>double</code> value representing the wind angle measured from 0 to 2 pi degrees
     */
    public void set_all(double a, double b, double c, double d, double e,
    double n, double p, double wind_angle)
    {
	this.a = a;
	this.b = b;
	this.c = c;
	this.d = d;
	this.e = e;
	this.n = n;
	this.p = p;
	this.wind_angle = wind_angle;
    }

    /**
     * <code>a</code> is a constant in the wind power law function.
     */
    private double a;

    /**
     * Get the value of a (const in wind power law function).
     * @return value of a (const in wind power law function).
     */
    public double getA()
    {
	return a;
    }

    /**
     * Set the value of <code>a</code> (const in wind power law function).
     * @param v  Value to assign to a (const in wind power law function).
     */
    public void setA(double v)
    {
	this.a = v;
    }

    /**
     * <code>b</code> is a constant in vertical eddy diffusivity function.
     */
    private double b;

    /**
     * Get the value of <code>b</code>.
     * @return value of <code>b</code>.
     */
    public double getB()
    {
	return b;
    }

    /**
     * Set the value of <code>b</code>.
     * @param v  Value to assign to <code>b</code>.
     */
    public void setB(double v)
    {
	this.b = v;
    }

    /**
     * <code>c</code> is a constant in lateral standard deviation function (LSDF).
     */
    private double c;

    /**
     * Get the value of <code>c</code>.
     * @return value of <code>c</code>.
     */
    public double getC()
    {
	return c;
    }

    /**
     * Set the value of <code>c</code>.
     * @param v  Value to assign to <code>c</code>.
     */
    public void setC(double v)
    {
	this.c = v;
    }

    /**
     * <code>d</code> is a constant in LSDF
     */
    private double d;

    /**
     * Get the value of <code>d</code>.
     * @return value of <code>d</code>.
     */
    public double getD()
    {
	return d;
    }

    /**
     * Set the value of <code>d</code>.
     * @param v  Value to assign to <code>d</code>.
     */
    public void setD(double v)
    {
	this.d = v;
    }

    /**
     * <code>e</code> is an exponent in LSDF.
     */
    private double e;

    /**
     * Get the value of <code>e</code>.
     * @return value of <code>e</code>.
     */
    public double getE()
    {
	return e;
    }

    /**
     * Set the value of <code>e</code>.
     * @param v  Value to assign to <code>e</code>.
     */
    public void setE(double v)
    {
	this.e = v;
    }

    /**
     * <code>n</code> is an exponent in eddy diffusivity power law function.
     */
    private double n;

    /**
     * Get the value of <code>n</code>.
     * @return value of <code>n</code>.
     */
    public double getN()
    {
	return n;
    }

    /**
     * Set the value of <code>n</code>.
     * @param v  Value to assign to <code>n</code>.
     */
    public void setN(double v)
    {
	this.n = v;
    }

    /**
     * <code>p</code> is an exponent in wind power law function.
     */
    private double p;

    /**
     * Get the value of <code>p</code>.
     * @return value of <code>p</code>.
     */
    public double getP()
    {
	return p;
    }

    /**
     * Set the value of <code>p</code>.
     * @param v  Value to assign to <code>p</code>.
     */
    public void setP(double v)
    {
	this.p = v;
    }

    /**
     * <code>wind_angle</code> direction that the wind is blowing from in degrees
     * (see special note about sign convention).
     */
    private double wind_angle;

    /**
     * Get the value of <code>wind_angle</code>.
     * @return value of <code>wind_angle</code>.
     */
    public double getWind_angle()
    {
	return wind_angle;
    }

    /**
     * Set the value of <code>wind_angle</code>.
     * @param v  Value to assign to <code>wind_angle</code>.
     */
    public void setWind_angle(double v)
    {
	this.wind_angle = v;
    }

    /**
     * <code>toString</code> method outputs all the dispersion parameters.
     *
     * @return a <code>String</code> value
     */
    public String toString()
    {
	return "\nThe dispersion object has several parameters based on the following relationships\n"
	       + "\nWind thought to follow power law form of u(z)=a z^p"
	       + "\nVertical eddy diffusivity thought to follow power law K= b z^n"
	       + "\nModified BNL lateral standard deviation function of form sig(y) = c + d * x^e"
	       + "\n\nThe parameters for these functions & the wind direction are:" + "\na = " + a
	       + "\t- const in wind power law function" + "\np = " + p + "\t- exponent in wind power law function"
	       + "\nb = " + b + "\t- cont in vertical eddy diffusivity funcion" + "\nn = " + n
	       + "\t- exponent in eddy diffusivity power law function" + "\nc = " + c
	       + "\t- const in lateral standard deviation function (LSDF)" + "\nd = " + d + "\t- const in LSDF"
	       + "\ne = " + e + "\t- exponent in LSDF" + "\t- in m/s" + "\nwind angle = " + wind_angle
	       + "\t- direction that the wind is blowing from in radians (see special note about sign convention)";
    }

    /**
     * <code>main</code> method only used for debugging.
     *
     * @param argv a <code>String[]</code> value that is not used in debugging.
     */
    public static void main(String[] argv)
    {
	System.out.println("\nThis file will debug the known dispersion class functions and members"
			   + "\nNOTE: the dispersion class has not been completely formed and debugged."
			   + "\nThis file needs to be augmented when more functions have been added");

	// create a dispersion test object
	dispersion test1 = new dispersion();

	// output with default initialization values
	System.out.println(test1);
	// Set the dispersion parameters
	test1.setA(1.6);		// so that the 10 m wind speed is 4 m/s for a p=.4
	test1.setB(0.4);		// so that the 10 m K is 4 m for a n=1
	// NOTE: I don't have a real sound reason for this estimate
	// but i need to do something that seems ballpark reasonable for diagnostic purposes
	test1.setC(1.5);		// just guessing an initial dispersion of 1.5 meters to get things rolling
	test1.setD(0.36);		// based on BNL neutral conditions
	test1.setE(0.86);		// "
	test1.setN(1.);			// based on huang eqn 34 this is for nuetral conditions
	test1.setP(.4);			// based on huang figure 1 this seems reasonable for a 10 m ref
	test1.setWind_angle(3.1415);    // wind coming approximately from the west
	System.out.println(test1);      // Output the results
	System.out.println("\nDebuggin Complete\n\n");
    }

    /**
     * This method finds the value of <code>a</code> that satisfies the power wind law equation
	* u(z)=a z^p  [a = u z^(-p)]
     * assuming nuetral stability (p=0.25) given the wind speed u at reference elevation z.
     */
    public static double find_nuetral_a(double ws, double ref_elevation)
    {
	double p = 0.25;    // assume nuetral stability

	return ws * Math.pow(ref_elevation, -p);
    }

    /**
     * Based on the object member values for the power wind law constants
	* <code>a</code> and <code>p</code> the wind speed at a evelvation
	* <code>ref_elevation</code> is returned.
	*
	*/

    public double wind_speed(double ref_elevation)
    {
	return a * Math.pow(ref_elevation, p);
    }
}

