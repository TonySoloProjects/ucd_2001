package roadway_dispersion;

import utilities.*;    // access to formating routines
import java.util.*;    // access to vector class

/**
 * <b>Overview</b><br>
 * This class facilitates the creatation of the point array that represents
 * the emissions from a highway_link object.  Point source density and emission factors are weighted
 * to speed up the model execution time.
 * A default spacing scheme is instantiated when this class is first used.  Static members can be
 * called to change the spacing during run time, however, altering the point spacing
 * is primarily used for debugging purposes during model developement.
 * <br>
 * To use the this object, a user must specify the coordinates of a highway link's endpoints,
 * link width, link emission factor (g/sec-meter) the coordinate of the receptor, and the wind angle.
 * The location of the receptor will be known because it is a static member for all link objects.
 *
 * The link will then be rotated and translated so that the link is cooincident with the y-axis and the
 * receptor is on the x-axis.  The point generating scheme defines density zones, with point desity much
 * greater near the x-axis because increased point resolution is desireable near the receptor.
 *
 * @author Tony Held
 * @version 1.0 (October 2001)
 * @see link
 * @see highway_link
 */
public class highway_point_spacing
{

/** @todo make sure I label this more after skimming the code */
// members store information about link geometry and emissions

    private coordinate		      link_start = null;
    private coordinate		      link_end = null;
    private coordinate		      recept_location = null;
    private double		      width = 0.0;
    // line strength in g/(second-meter) for the highway link
    private double		      line_strength = 0.0;
    // standard geometric angle of the link prior to rotation
    private double		      link_angle = 0.0;
    // region start and end points are stored in a 2-d arrays named region_extents
    private double[][]		      region_extents;
    // fetch spacing for each region_extents interval is stored in fetch_spacing
    private double[]		      fetch_spacing;
    // cross spacing for each region_extents interval is stored in cross_spacing
    private double[]		      cross_spacing;
    // used to translate coordinate system for point generation purposes
    private coordinate	 offset = null;
    // used to rotate coordinate system for point generation purposes
    private double	      rotation_angle_radians = -99.9;

    /**
     * The wind vector object stores wind direction and wind speed at a reference elevation.
	* Currently, the highway_point_spacing class does not use this member to determine point spacing.
     */
    private wind_vector		      wind_vec = null;

    /**
     * The <code>points</code> member is a vector used to store point objects that are created for each zone.
     */
    private Vector	      points = new Vector();

    /**
     * If a calculation for travel time over the roadway has to be made,
     * the wind speed at the representative_wind_elevation will be used
     * for that calculation.
     */
    private static double     representative_wind_elevation = 4.5;

    /**
     * A read only member indicating the total number of regions with unique point densities.
     */
    private static int	      num_regions;

    /**
     * The <code>zones</code> member specifies a region by its distance from the x-axis
	* for use in point density calculations. There is one more region than numbers
	* specified in zone array.  <code>zones</code> must be specified in increasing magnitude.<br>
     *
	* For example: if zones = [5,10,30]                 <br>
     * Region 0 is from y= -5 to +5                      <br>
     * Region 1 is from y= -10 to -5 and +5 to +10       <br>
     * Region 2 is from y= -30 to -10 and 10 to 30       <br>
     * Region 3 is from y= -inf to -30 and 30 to +inf    <br>
     */
    private static double[]   zones;

    /**
     * <code>fetch_density</code> indicates the spacing of points along the roadway centerline
	* for each region. <code>fetch_density</code> must be one member longer than zones.
     * Since the spacing of points is expected to increase as the distance between
	* the link and receptor increases, one would expect each <code>fetch_density</code>
	*  member specification to be greater than the last. <br>
     *
	* For example fetch_density = [.5,1,5,10]                                      <br>
     * region 0 has points located every 0.5 m along the roadway centerline         <br>
     * region 1 has points located every 1 m along the roadway centerline           <br>
     * region 2 has points located every 5 m along the roadway centerline           <br>
     * region 3 has points located every 10 m along the roadway centerline          <br>
     */
    private static double[]   fetch_density;

    /**
     * <code>cross_density</code> indicates the spacing of points on a line normal
	* to the roadway centerline for each region. <code>cross_density</code>
     * must be one member longer than zones.
     * If a specified spacing is larger than the link width a single point
	* centered on the link will be used. <br>
	*
     * For example: cross_density = [.5,1,5,10]                                                    <br>
     * region 0 has points located every 0.5 m along a line normal to the roadway centerline       <br>
     * region 1 has points located every 1 m along a line normal to the roadway centerline         <br>
     * region 2 has points located every 5 m along a line normal to the roadway centerline         <br>
     * region 3 has points located every 10 m along a line normal to the roadway centerline        <br>
     */
    private static double[]   cross_density;

    /**
     * <code>vertical_density</code> specifies the location of points above the roadway.
     * The <code>vertical_density</code> array is a varying length multidimensional array
     * and is based on a collection of piecewise continuous point locations.
     * The size of the 1st index determines the number of vertical segments for each region.
     * The second index is a 3 member 1-d array organized as [start, stop, spacing]. <br>
     *
     * Example 1: vertical_density = new double[5][3]
	* All regions have 5 vertical spacing segments
     * Example 2: vertical_density[][] = {{ 0, 5, 1},{6,16,2}}
	* All regions have a two member peicewise continuous point placement.
     * The first spacing segment starts at z=0 and has points every meter until z=5.
     * The second spacing segment starts at z=6 and has points every two meters until z=16.
     */
    private static double[][] vertical_density;

    /**
	* <code>cross_component_overhang_UP</code>
	* representss the distance that point sources can extend laterally from the upwind
	* side of a highway link. Points typically extend 3m to each side of the traveled way.
	* This code was originally designed to allow for asymetrical point placements
	* that can be determined based on wind speed and direction and link orientation.
	*/
    private static double cross_component_overhang_UP=0.0;

    /**
	* <code>cross_component_overhang_Down</code>
	* representss the distance that point sources can extend laterally from the downwind
	* side of a highway link. Points typically extend 3m to each side of the traveled way.
	* This code was originally designed to allow for asymetrical point placements
	* that can be determined based on wind speed and direction and link orientation.
	*/
    private static double cross_component_overhang_Down=0.0;

    /**
	* Set the distance in meters that point can extend in the upwind lateral direction of a link.
	*/
    public static void set_cross_component_overhang_UP(double single_side_overhang)
    {
	 highway_point_spacing.cross_component_overhang_UP = single_side_overhang;
    }
    /**
	* Set the distance in meters that point can extend in the downwind lateral direction of a link.
	*/
    public static void set_cross_component_overhang_Down(double single_side_overhang)
    {
	 highway_point_spacing.cross_component_overhang_Down = single_side_overhang;
    }

    /**
     * Routine to set the zone distances - see the member <code>zones</codes> for
	* details on acceptable usage.
	*/
    public static void setZones(double[] new_zones)
    {
	// set the total number of regions variable.  There is always one more region than zones.
	num_regions = new_zones.length + 1;
	// redim the zones member to the new size
	zones = new double[new_zones.length];

	for (int i = 0; i < new_zones.length; i++)
	{
	    highway_point_spacing.zones[i] = new_zones[i];
	}
    }

    /**
     * Routine to set the the fetch density of highway points - see <code>fetch_density</code>
	* for acceptable usage.
     */
    public static void setFetch_density(double[] new_fetch_density)
    {
	// set the total number of regions variable
	num_regions = new_fetch_density.length;
	// redim the fetch_density member to the new size
	fetch_density = new double[new_fetch_density.length];

	for (int i = 0; i < new_fetch_density.length; i++)
	{
	    highway_point_spacing.fetch_density[i] = new_fetch_density[i];
	}
    }

    /**
     * Routine to set the the lateral density of highway points - see <code>cross_density</code>
	* for acceptable usage.
     */
    public static void setCross_density(double[] new_cross_density)
    {
	// set the total number of regions variable
	num_regions = new_cross_density.length;
	// redim the cross_density member to the new size
	cross_density = new double[new_cross_density.length];

	for (int i = 0; i < new_cross_density.length; i++)
	{
	    highway_point_spacing.cross_density[i] = new_cross_density[i];
	}
    }
    /**
     * Routine to set the the vertical density of highway points - see <code>vertical_density</code>
	* for acceptable usage.
     */
    public static void setVertical_density(double[][] new_vertical_density)
    {
	// redim the vertical_density member to the new size
	vertical_density = new double[new_vertical_density.length][3];

	for (int i = 0; i < new_vertical_density.length; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		highway_point_spacing.vertical_density[i][j] = new_vertical_density[i][j];
	    }
	}
    }

    /**
     * This static initilizer is used to set the default highway spacing members
     */
    static
    {
	set_default_spacing();    // use the default spacing scenario
    }

    /**
     * This routine sets the fetch, lateral and vertical spacing for each link zone.
	* In theory, one could use a very space point spacing for each orthogal direction for every
	* zone.  However, that would require significant processing time calculating concentration
	* contributions from parts of the link that are too far away from the receptor to merit extra
	* resolutions.  <br>
	* The default spacing specifies 4 zones with zones = [5.0, 20.0, 100.0.],
	* the fetch desnisty is [0.5, 1.0, 5.0], the cross_density is  100.0This coresponds to
	* zone A = 0 to +-5 m from
	* .  Zone a is sets highway point spacing to default values.  These will be used if the user does not
     * specify his or her own settings or if invalid settigns were attempted.
     *
     */
    public static void set_default_spacing()
    {
	// four regions total for this configuration
	double[]   zones ={ 5.0, 20.0, 100.0 };
	double[]   fetch_density ={ 0.5, 1.0, 5.0, 100.0 };
	double[]   cross_density ={ 0.5, 1.0, 3.0, 5.0 };    // only use a single point centered in the roadway at distant zones
	//
	double[][] vertical_density = {{0.0, 2.5, 0.5}};

	num_regions = zones.length + 1;    //always one more region than zones
	// set the object values to the matricies in this member function
	highway_point_spacing.zones = zones;
	highway_point_spacing.fetch_density = fetch_density;
	highway_point_spacing.cross_density = cross_density;
	highway_point_spacing.vertical_density = vertical_density;

	// set the distance that points extend past the traveled way in the cross wise direction
	// point extend 3.0 off the road in both the upwind and downwind direction
	highway_point_spacing.set_cross_component_overhang_UP(3.0);
	highway_point_spacing.set_cross_component_overhang_Down(3.0);
    }

    /**
     * This routine makes sure that the lengths of the user specified zone location and
	* point densities are valid.  If they are not the default scheme will be used.
     */
    private static void validate_zones()
    {
	if (num_regions != cross_density.length | num_regions != fetch_density.length
		| num_regions != (zones.length + 1))
	{
	    System.err.println("Warning: a invalid set of highway point density parameters were specified, a default setting will be used instead");
	    System.out.println("Invalid Settings are " + StatictoString());
	    set_default_spacing();    // set to default
	    System.out.println("Settings reset to the following " + StatictoString());
	}
    }

    /**
     * This routine returns spacing and zone information of non-static class
	* members as a string and is primarily used for debugging purposes.
     */
    public String toString()
    {
	String return_string = "";

	return_string += "Number of Regions = " + num_regions;
	return_string += "\nFetch Density = " + format_matrix.array_2_str(fetch_density, true, "%4.3g\t");
	return_string += "\nCross Density = " + format_matrix.array_2_str(cross_density, true, "%4.3g\t");
	return_string += "\nVertical Density = " + format_matrix.array_2_str(vertical_density, true, "%4.3g\t");
	return_string += "\nLink Width = " + width;
	return_string += "\nLine Strength = " + line_strength;
	return_string += "\nLink Start Point = " + link_start;
	return_string += "\nLink End Point = " + link_end;
	return_string += "\nReceptor Coordinate = " + recept_location;
	return_string += "\nWind Vector Info = " + wind_vec;
	return_string += "\nRegion Extents = " + format_matrix.array_2_str(region_extents, true);
	return_string += "\nRegion Fetch Spacing = " + format_matrix.array_2_str(fetch_spacing, true);
	return_string += "\nRegion cross Spacing = " + format_matrix.array_2_str(cross_spacing, true);

	return return_string;
    }

    /**
     * This routine returns spacing and zone information of static class
	* members as a string and is primarily used for debugging purposes.
     */
    public static String StatictoString()
    {
	String return_string = "";

	return_string += "Number of Regions = " + num_regions;
	return_string += "\nFetch Density = " + format_matrix.array_2_str(fetch_density, true, "%4.3g\t");
	return_string += "\nCross Density = " + format_matrix.array_2_str(cross_density, true, "%4.3g\t");
	return_string += "\nVertical Density = " + format_matrix.array_2_str(vertical_density, true, "%4.3g\t");

	return return_string;
    }

    /**
     * Constructor to create a new highway point spacing object based
	* on link and receptor locations and wind info.  Currently the MET information is not used
	* in the point placement routines.
     */
    public highway_point_spacing(coordinate link_start, coordinate link_end, double width, double line_strength,
				 coordinate recep, double power_law_wind_a, double p, double wind_angle_in_radians)
    {
	this.link_start = new coordinate(link_start);
	this.link_end = new coordinate(link_end);
	this.width = width;
	this.line_strength = line_strength;
	this.link_angle = link_angle;
	this.recept_location = new coordinate(recep);
	this.wind_vec = new wind_vector(power_law_wind_a, p, wind_angle_in_radians);
    }

    /**
	* The main method is only used for debugging purposes.
	*/

    public static void main(String[] args)
    {
	highway_point_spacing.debug1();
    }
    /**
	* This routine is only used for debugging purposes.
	* It tests the scheme used to generate point arrays for highway links/
	*/
    /** @todo consider removing this routine. */

    public static void debug1()
    {
	// set the point spacing so that it is the same as the highway_link example
	double[]   zone_array = new double[1];     // only one zone
	double[]   fetch_array = new double[2];    // really only one spacing the because their is only one zone
	double[]   cross_array = new double[2];    // really only one spacing the because their is only one zone
	double[][] vertical_array = new double[2][3];    // really only one spacing the because their is only one zone

	zone_array[0] = 10000.0;    // extra large zone so that there is only one spacing
	fetch_array[0] = fetch_array[1] = 10.0;
	cross_array[0] = cross_array[1] = 2.0;
	vertical_array[0][0] = 0.0;
	vertical_array[0][1] = 5.0;
	vertical_array[0][2] = 1.0;
	vertical_array[1][0] = 6.0;
	vertical_array[1][1] = 10.0;
	vertical_array[1][2] = 2.0;

	highway_point_spacing.setZones(zone_array);
	highway_point_spacing.setFetch_density(fetch_array);
	highway_point_spacing.setCross_density(cross_array);
	highway_point_spacing.setVertical_density(vertical_array);

	coordinate receptor_coordinate = new coordinate(50, 50, 1.8);
	double     link_width = 13.0;
	double     link_strength = 100.0;
	double     wind_direction = 3.14;    // blowing from the west
	double     wind_a = dispersion.find_nuetral_a(2.1, 4.5);
	double     wind_p = 0.25;
	// coordinates of the receptor and line begin and end points
	coordinate start;
	coordinate end;

	// Test Quadrent 1 - output the point locations before and after rotations
	start = new coordinate(0, 0, 0);
	end = new coordinate(100, 100, 0);

	highway_point_spacing hps = new highway_point_spacing(start, end, link_width, link_strength,
		receptor_coordinate, wind_a, wind_p, wind_direction);
	point[]		      test = hps.return_point_array();

	// System.out.println(hps);
	hps.print_points();
	// System.out.println(hps);
    }
    /**
	* This routine rotates and translates the coordinate system (CS) so that the link is parallel
	* to the y-axis and then the CS is translated so that the link centerline
	* is cooincident with the y-axis and translated once more so that the receptor is on the x-axis.
	* The object variable <code>offset</code> is used to store the distance that the CS is offset
	* and the rotation angle is stored as <code>rotation_angle_radians</code>.  These stored values
	* allow the points representing the link to be translated back to their original position once
	* created in the normalized coordinate system.
    */
    private void rotate_coordinate_system()
    {
	// find the link angle by putting the start coordinate on the origin
	// and finding the angle of the endpoint coordiante
	coordinate temp_coord = new coordinate(link_end);

	temp_coord.subtract(link_start);

	// angle that the coordinate system has to rotated so that the link will be coincident with the y-axis
	rotation_angle_radians = constants.PI / 2 - temp_coord.horizontal_angle();

	// rotate the end points and the wind vector
	link_end.horizontal_rotation(rotation_angle_radians);
	link_start.horizontal_rotation(rotation_angle_radians);
	recept_location.horizontal_rotation(rotation_angle_radians);
	wind_vec.rotate(rotation_angle_radians);

	// offset so that link end points are on the y-axis and receptor is on the x axis
	offset = new coordinate(link_start.getX(), recept_location.getY(), 0.0);

	link_end.subtract(offset);
	link_start.subtract(offset);
	recept_location.subtract(offset);
    }

    /**
     * This routine is used to find the zone end points based on the link length
	* and zone length specifications.  To ensure smooth transition at the
	* region boundaries the routine <code>optimize_fetch_spacing</code>
     * will be run at the end of this routine which will alter fetch spacing.
     */
    private void find_end_points()
    {
	// rotate the coordinate system so that the link is on the y-axis and the receptor is on the x-axis
	this.rotate_coordinate_system();

	// create the region start and end points as if there were no problems with end point locations
	// after the creation of the array the max and min end points will be adjusted so that they do
	// not exceed the link length
	double start_point = 0.0;    // region 0 radiates from the origin
	double end_point = 0.0;

	// loop through the specifications to find the region boundaries
	// start in the middle of the region specification matrix and radiate
	// outward so that the region specifications are in increasing magnitude
	region_extents = new double[num_regions * 2][2];

	for (int i = 0; i < zones.length; i++)
	{
	    end_point = zones[i];
	    region_extents[num_regions + i][0] = start_point;
	    region_extents[num_regions + i][1] = end_point;
	    region_extents[num_regions - 1 - i][0] = -end_point;
	    region_extents[num_regions - 1 - i][1] = -start_point;
	    // set the start point for the next region
	    start_point = end_point;
	}

	// make the end of the last region a verty large number that will be truncated later
	{
	    end_point = 1E12;
	    region_extents[num_regions + zones.length][0] = start_point;
	    region_extents[num_regions + zones.length][1] = end_point;
	    region_extents[0][0] = -end_point;
	    region_extents[0][1] = -start_point;
	}

	// set any value that is greater than the link extent = to the link extent
	// in a subsequent algorithm a region that start and ends on the same point
	// will have a length of zero and will not have points placed on it
	double negative_link_extent = link_start.getY();
	double positive_link_extent = link_end.getY();

	for (int i = 0; i < region_extents.length; i++)
	{
	    for (int j = 0; j < 2; j++)
	    {
		if (region_extents[i][j] > positive_link_extent)
		{
		    // if the region is greater than the link extent set the region end or start point = to the link extent
		    region_extents[i][j] = positive_link_extent;
		}

		if (region_extents[i][j] < negative_link_extent)
		{    // if the region is less negative than the link extent set the region end or start point = to the link extent
		    region_extents[i][j] = negative_link_extent;
		}
	    }
	}

	// initialized fetch and cross spacing matrix in the same format as the region_extents matrix
	cross_spacing = new double[num_regions * 2];
	fetch_spacing = new double[num_regions * 2];

	for (int i = 0; i < num_regions; i++)
	{
	    fetch_spacing[num_regions - 1 - i] = fetch_density[i];    // negative regions
	    cross_spacing[num_regions - 1 - i] = cross_density[i];
	    fetch_spacing[num_regions + i] = fetch_density[i];	      // positive regions
	    cross_spacing[num_regions + i] = cross_density[i];
	}
    }

    /**
     * This routine creates the points along the boundary between two regions.
     * These point must be dealt with differently than the interior
     * points because the number of cross sectional points and fetch spacing
     * generally changes from region to region.
     * Boundary points are created by averaging the fetch spacing.  The
     * cross spacing is taken as the lesser of the two intersecting regions.
     * There is one more boundary point than regions.
     * The First and last boundary points will have a fetch spacing of 1/2 the
	* terminal regions fetch spacing because the area contributing to those
	* end points is only 1/2 that of a boundary point between two regions.
     * Since the fetch spacing will determine a point's emission factor, boundary
	* and end points have differing EFs than interior points.
     */
    private void generate_boundary_points()
    {
	double location = 0.0;     // fetch location of the boundary point
	double f_spacing = 0.0;    // fetch spacing for the the boundary point
	double c_spacing = 0.0;    // cross spacing for the the boundary point

	// create the boundary points
	for (int i = 0; i < region_extents.length + 1; i++)
	{
	    if (i == 0)					 // first boundary point
	    {
		location = region_extents[i][0];	 // start point of first region
		f_spacing = fetch_spacing[i] / 2;
		c_spacing = cross_spacing[i];
	    }

	    if (i == region_extents.length)		 // last boundary point
	    {
		location = region_extents[i - 1][1];     // end point of last region
		f_spacing = fetch_spacing[i - 1] / 2;
		c_spacing = cross_spacing[i - 1];
	    }

	    if (i != 0 && i != region_extents.length)    // interior boundary point
	    {
		location = region_extents[i][0];	 // start point of interior boundary point
		// fetch spacing is the average of the two intersecting regions
		f_spacing = (fetch_spacing[i] + fetch_spacing[i - 1]) / 2;

		double left_spacing, right_spacing;

		left_spacing = cross_spacing[i - 1];
		right_spacing = cross_spacing[i];

		if (left_spacing > right_spacing)	 // take the lesser of the two regions spacing
		{
		    c_spacing = right_spacing;
		}
		else
		{
		    c_spacing = left_spacing;
		}
	    }

	    // add the boundary points to the point vector for this region
	    // the interior points will be handeled in a different routine
	    permute_points(location, location, f_spacing, c_spacing);
	}
    }

    /**
     * Create points interior to a region.  Boundary points are handeled differently, see
	* <code>generate_boundary_points</code>.
     */
    private void generate_interior_points()
    {
	// create the interior points
	for (int i = 0; i < region_extents.length; i++)
	{
	    // add the interior points to the point vector for this region
	    // the end points will be handeled in a different routine
	    permute_points(region_extents[i][0] + fetch_spacing[i], region_extents[i][1] - fetch_spacing[i],
			   fetch_spacing[i], cross_spacing[i]);
	}
    }

    /**
     * This routine is called from the <code>generate_interior_points</code> and
	* <code>generate_boundary_points</code> routines to add points the the
	* <code>highway_point_spacing</code> private member <points> with appropriately
	* weighted emission factors.  Each region has a a coresponding fetch and
	* cross spacing, but the vertical spacing is common to all regions.  The emission
	* factor for each point is based on the point density - the complexities of boundary and
	* termal points are handeled in the <code>generate_interior_points</code> and
	* <code>generate_boundary_points</code> routines by adjusting the <fetch_spacing>.
     */
    private void permute_points(double start, double end, double fetch_spacing, double cross_spacing)
    {
	point_vector vertical_vector = new point_vector();    // create a new vector to store the vertical points
	point_vector cross_vector = new point_vector();       // create a new vector to store the cross roadway points
	point_vector fetch_vector = new point_vector();       // create a new vector to store the fetch points
	double[]     vertical_profile = null;		      // arrays to hold point locations
	double[]     cross_profile = null;		      // relative to the rotated link
	double[]     fetch_profile = null;
	double       ef = 0;				      // emission factor for each point in this region

	// loop through each of the start stop, spacing rows of the vertical_density arrays
	for (int i = 0; i < vertical_density.length; i++)
	{
      // add poins from [i][0] to [i][1] with spacing [i][2]
	 // if vertical spacing is zero then skip to the next spacing vector in the matrix
	 if (vertical_density[i][2] != 0.0)
					{
	    vertical_vector.add_points(vertical_density[i][0], vertical_density[i][1], vertical_density[i][2]);

					}
					}

	vertical_profile = vertical_vector.getPoint_array();    // return the point array for vertical spacing

	// set the cross roadway spacing
	// if the spacing is greater than the width then have a single point on the centerline
	if (cross_spacing > width)
	{
	    cross_profile = new double[1];
	    cross_profile[0] = 0.0;
	}
	else
	{
	    // space the points across the roadway - first need to determine which side of the
	    // road is upwind.  if the x component of the wind is negative then the
	    // wind is from the left to right since the link is oriented along the y axis

	    if (wind_vec.get_x_component() >= 0 )
	    {
		  //  wind from the right to left
	    cross_vector.add_points( ( -width / 2.0 - cross_component_overhang_Down) ,
	    ( width / 2.0 + cross_component_overhang_UP), cross_spacing);
	    }
	    else
	    {
		  // wind from the right to left
  	    cross_vector.add_points( ( -width / 2.0 - cross_component_overhang_UP) ,
	    ( width / 2.0 + cross_component_overhang_Down), cross_spacing);
	    }

	    cross_profile = cross_vector.getPoint_array();
	}

	// space the points across the roadway symetric to the link centerline
	// if the start and end points are the same then a single
	// point will be placed on the start point
	// this will occur when you are placing a boundary point
	// the fetch spacing (and cross section # of points will also determine the per point emission factor
	if ( Math.abs(start - end) > 0.0001)    // interior point
	{
	    fetch_vector.add_points(start, end, fetch_spacing);
	}
	else		     // boundary point
	{
	    fetch_vector.add_points(start);
	}

	fetch_profile = fetch_vector.getPoint_array();
	// to find the emission factor for each of the  points
	// for each cross sectional area made perpendicular to the roadway centerline line
	// the emission if there were only a single point along the centerline would be determined
	// by the following relationship
	// single interior point source strength = fetch spacing * line source strength
	// [g/(point-sec)] = [m/point] * [g/(m-sec)]
	ef = fetch_spacing * line_strength;
	// since there will be more than one point per cross section, one has to devide by the
	// total number of cross sectional points to determine the per point source strength
	ef = ef / (vertical_profile.length * cross_profile.length);

	// temporary point to be added to the vector with all the sub-element point
	point temp_point;

	for (int i = 0; i < cross_profile.length; i++)
	{
	    for (int j = 0; j < fetch_profile.length; j++)
	    {
		for (int k = 0; k < vertical_profile.length; k++)
		{
		    // create the point and increment the index
		    temp_point = new point();

		    temp_point.getPoint_location().set_all(cross_profile[i], fetch_profile[j],
							   vertical_profile[k]);
		    temp_point.setSource_strength(ef);
		    // add the point to the points vector
		    points.add(temp_point);
		}
	    }
	}
    }

    /**
     * This routine to output the points locations and emission factors
	* to the console for debugging purposes.
     */
    public void print_points()
    {
	point point_ref;    // ref to point object

	// System.out.println("(x,y,z,ef)");
	// the first line is the x,y,z of the start coordinate with a zero pad
	// the second line is the x,y,z of the end coordinate with a zero pad
	// the third line is the width with three zero pads
	System.out.println(link_start.getX() + " " + link_start.getY() + " " + link_start.getZ() + " 0.0 ");
	System.out.println(link_end.getX() + " " + link_end.getY() + " " + link_end.getZ() + " 0.0 ");
	System.out.println(width + " 0.0 0.0 0.0");

	for (int i = 0; i < points.size(); i++)    // loop through all the points
	{
	    point_ref = (point) points.get(i);     // get a ref to a point so that its coordiante can be printed

	    System.out.println(point_ref.getPoint_location().getX() + "\t" + point_ref.getPoint_location().getY()
			       + "\t" + point_ref.getPoint_location().getZ() + "\t"
			       + point_ref.getSource_strength());
	}
    }

    /**
     * This routine is used in order to ensure a smooth transition of point placement and
	* emission factors between regions.  The fetch spacing may be changed to ensure
	* that the outer points are cooincident with the end points of the link.
	* This routine will not increase the size of the point spacing.
     * The optimum spacing is the maximum spacing that will result
     * in a whole and even number of points along the fetch.  This will happen if the
     * spacing evenly devides into the link length. If a region has a zero length
	* it will be dropped from the region_extents, cross_spacing,
     * and fetch_spacing matricies.
     */
    private void optimize_region_extents_and_fetch_spacing()
    {
	// loop through the region_extents to find if the region should be removed
	// or if the spacing need to be adjusted
	int good_regions = 0;    // number of regions that are non-zero in length

	for (int i = 0; i < region_extents.length; i++)
	{
	    double region_length = region_extents[i][1] - region_extents[i][0];    // find the length of the region
	    double original_spacing = fetch_spacing[i];				   // original spacing desired
	    // find if the spacing evenly divides into region length
	    double length_spacing_ratio = region_length / original_spacing;
	    double remainder = length_spacing_ratio - Math.floor(length_spacing_ratio);

	    if (remainder > 0.01)						   // need to find a better spacing
	    {
		// find the lowest whole number of points that will result
		// in a spacing <= originally specified spacing
		double points_per_link = Math.ceil(length_spacing_ratio);

		if (points_per_link < 5)					   // make sure there are at least 5 points per sub link
		{
		    points_per_link = 5;
		}

		fetch_spacing[i] = region_length / points_per_link;
	    }

	    // if the region has a zero length, set the spacing to -99
	    if (region_length < 0.1)
	    {
		fetch_spacing[i] = -99.9;
	    }
	    else								   // if the region is non zero in length increment the number of good regions
	    {
		good_regions++;
	    }
	}

	// drop the zero length regions
	double[][] temp_region_extents = new double[good_regions][2];
	double[]   temp_fetch_spacing = new double[good_regions];
	double[]   temp_cross_spacing = new double[good_regions];
	int	   index = 0;    // index to find out what the index of the good_regions are

	for (int i = 0; i < region_extents.length; i++)
	{
	    if (fetch_spacing[i] > -99.0)    // good regions have non-zero spacings
	    {
		temp_region_extents[index][0] = region_extents[i][0];
		temp_region_extents[index][1] = region_extents[i][1];
		temp_fetch_spacing[index] = fetch_spacing[i];
		temp_cross_spacing[index] = cross_spacing[i];
		index++;		     // increment the good regions index
	    }
	}

	// remove zero length regions
	region_extents = temp_region_extents;
	fetch_spacing = temp_fetch_spacing;
	cross_spacing = temp_cross_spacing;
	// System.out.println(format_matrix.array_2_str(temp_region_extents,true));
	// System.out.println(format_matrix.array_2_str(temp_fetch_spacing,true));
	// System.out.println(format_matrix.array_2_str(temp_cross_spacing,true));
    }

    /**
     * This routine is used to rotate and translate the points generated in the noralized
	* point generation coordinate system so that they are cooincident with the
     * actual link start and end points.  This routine is should be called only once, just before
	* the point array is returned.
	*/
    private void re_rotate_coordinate_system()
    {
	// rotate and translate the end points and the wind vector back to their original positions
	link_end.add(offset);
	link_start.add(offset);
	recept_location.add(offset);
	link_end.horizontal_rotation(-rotation_angle_radians);
	link_start.horizontal_rotation(-rotation_angle_radians);
	recept_location.horizontal_rotation(-rotation_angle_radians);
	wind_vec.rotate(-rotation_angle_radians);

	point curent_point = null;    // helps to have a reference to a single point

	for (int i = 0; i < points.size(); i++)
	{
	    curent_point = (point) points.get(i);

	    // translate the coordinate system origin to its original location
	    curent_point.getPoint_location().add(offset);
	    // rotate the points the oposite of how the coordinate system was originally rotated
	    curent_point.getPoint_location().horizontal_rotation(-rotation_angle_radians);
	}
    }

    /**
     * This routine returns the vector of points created with the
	* <code>highway_point_spacing</code> as an array of points.
     * This routine should only be used once in an objects lifetime, so the
	* underlying point vector is explicitly nulled in this routine
	* to save memory and avoid the temptation of using it twice.
     */
    public point[] return_point_array()
    {
	// generate the point vector for this link
	this.find_end_points();
	this.optimize_region_extents_and_fetch_spacing();
	this.generate_interior_points();
	this.generate_boundary_points();
	this.re_rotate_coordinate_system();

	point[] return_array = new point[points.size()];

	for (int i = 0; i < points.size(); i++)
	{
	    return_array[i] = (point) points.get(i);
	}

	// System.out.println(this);      // print for debuggin purposes
	points = null;    // clear the vector reference

	return return_array;
    }
}

