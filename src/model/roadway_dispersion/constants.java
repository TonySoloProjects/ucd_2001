package roadway_dispersion;
/**
 * <b>Overview</b><br>
 * The <code>constants</code> class stores all roadway dispersion constants into one class.
 * Note - All units in this file and the model are metric (MKS) unless otherwise stated.
 * This file reduces the likelyhood of magic numbers appearing in the code.
 *
 * <br><br>
 *
 * <br><b>ACTION AND REVISION LOG</b>
 * <tt><pre>
 * Author         Date        Action
 * --------------------------------------------------------------------------
 * Tony Held(TH)  12-02-99    cpp file created
 * TH             08-16-00    constants ported to java
 * TH             10-23-00    class debugged & verified - no additions needed to class
 * </tt></pre>
 *
 * @author Tony Held
 * @version 1.0 (October 2001)
 */


public class constants
{

/**
 * Conversion from hours to seconds = 3600.
 */

   public static final double SECONDS_PER_HOUR = 3600.0;

    /**
     * Conversion from miles to meters.
     */
    public static final double METERS_PER_MILE = 1609.344;

    /**
     * Conversion of pressure in mmHG to atmospheres (atms).
     */
    public static final double ATMS_PER_mmHG = 0.001315789;

    /**
     * Geometric constant <code>PI</code> (3.14 ...).
     */
    public static final double PI = Math.PI;

    /**
     * Unit conversion constant <code>RADIANS_TO_DEGREES</code> = 180/pi.
	* Allows conversion from radians to degrees.
     */
    public static final double RADIANS_TO_DEGREES = 57.2957795130931;

    /**
     * Unit conversion constant <code>DEGREES_TO_RADIANS</code> = pi/180.
	* Allows conversion from degrees to radians.
     */
    public static final double DEGREES_TO_RADIANS = 0.01745329251994;

    /**
     * Constant <code>DISTANCE_TOLERANCE</code> is the distance at which two object will be considered
     * coincident. This will prevent divide by 0 problems and will improve the accuracy of the bessel funciton calculations.
     */
    public static final double DISTANCE_TOLERANCE = 0.3;

    /**
     * Constant <code>LOOPING_EPSILON</code> is used to avoid rounding errors in for loops condition tests.
     */
    public static final double LOOPING_EPSILON = 0.01;

    /**
     * Constant <code>HUANG_1</code> is used in huang dispersion equation denomenator = sqrt(2 PI).
     * It is defined here as a constant to avoid it being calculated each time the bessel function is evaluated.
     */
    public static final double HUANG_1 = 2.506628274631;


}

