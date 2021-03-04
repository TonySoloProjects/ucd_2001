package roadway_dispersion;
import utilities.*;  // used to access naming routines

/**
 * <b>Overview</b><br>
 * <code>highway_link</code> is a link type developed specifically to emulate the release characteristics
 * of the GM sulfate dispersion experiment.  In theory, this link type can be used to simulate any free-flowing
 * roadway link such as a highway or major arterial roadway link.
 * Internally, the highway_link represents the vehicular emissions eminating from a free flowing roadway as
 * a three dimensional array of point sources.  Conceptually, this approach emulates a volume source type rather
 * that the typical line-source approach used by other roadway dispersion models.
 * The highway_link class is a subclass of the abstract link class.
 * The point source array simulates uniform emissions from a control volume extending 2.5 m above the road
 * and 3 m to lateral of each traveled way.  The point densities are optimized so that there are a greater
 * number of points sources nearby receptors.
 * The highway_point_spacing class is used to determine the location and the weighting of each point source.
 *
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

public class highway_link extends link implements Cloneable
{
    /**
     * variable <code>line_strength</code> representing the line source strength of the highway link -
     * units are assumed to be grams of pollutant per meter per second [(grams of pollutant) / (m-s)].
     */
    private double		  line_strength;

    /**
     * The variable <code>traffic_flowrate</code> is the vehicle flowrate on the link in vehicles/second.
 	* Note:  usually traffic flowrates are in vehicles per hour but they must be specified in vehicles per
	* second so that everything stays metric.

     */
    private double		  traffic_flowrate;

    /**
     * variable <code>vehicle_EF</code> is the single vehicle aggregate emission factor in
     * (grams of pollutant)/(vehicle-m)  note: that is per meter not mile!
     */
    private double		  vehicle_EF;
    /**
     * This is the default constructor and is only used in the cloning process.
     */
    private highway_link() {}

    /**
     * Creates a new <code>highway_link</code> instance with a default name by specifying the geometry and
     * line strength of a highway link.
     *
     * @param start_point a <code>coordinate</code> value representing the begining of the link (based on traffic flow direction).
     * @param end_point a <code>coordinate</code> value representing the end of the link (based on traffic flow direction).
     * @param link_width a <code>double</code> value representing the width of the traveled way
     * @param line_strength a <code>double</code> value  representing the line source strength of the highway link -
     * units are assumed to be (grams) / (m-s)
     */
    public highway_link(coordinate start_point, coordinate end_point, double link_width, double line_strength)
    {
	setStart_location(start_point);     // set the begin and end points of the link
	setEnd_location(end_point);
	setWidth(link_width);		    // set link width
	setLine_strength(line_strength);    // set line strength
	determine_link_geometry();	    // find link length and angle

	naming = new default_naming();    // get a default name for the link
    }

    /**
     * Creates a new <code>highway_link</code> instance of a given name by specifying the geometry and
     * line strength of a highway link.
     *
     * @param start_point a <code>coordinate</code> value representing the begining of the link (based on traffic flow direction).
     * @param end_point a <code>coordinate</code> value representing the end of the link (based on traffic flow direction).
     * @param link_width a <code>double</code> value representing the width of the traveled way
     * @param line_strength a <code>double</code> value  representing the line source strength of the highway link -
     * units are assumed to be (grams) / (m-s)
     * @param link_name - name of the link
     */
    public highway_link(coordinate start_point, coordinate end_point, double link_width, double line_strength,
			String link_name)
    {
	setStart_location(start_point);     // set the begin and end points of the link
	setEnd_location(end_point);
	setWidth(link_width);		    // set link width
	setLine_strength(line_strength);    // set line strength
	determine_link_geometry();	    // find link length and angle

	naming = new default_naming(link_name);    // set the name for the link
    }

    /**
     * Creates a new <code>highway_link</code> instance with a default name
	* by specifying the geometry, traffic flowrate,and the aggregate per-vehicle emission factor.
	* The overall source strength of the link will be calculated from these values.
     *
     * @param start_point a <code>coordinate</code> value representing the begining of the link (based on traffic flow direction).
     * @param end_point a <code>coordinate</code> value representing the end of the link (based on traffic flow direction).
     * @param link_width a <code>double</code> value representing the width of the traveled way
     * @param vehicle_EF a <code>double</code> value representing the single vehicle aggregate emission factor in
     * (grams of pollutant)/(vehicle-m)  note: that is per meter not mile!
     * @param traffic_flowrate a <code>double</code> is the vehicle flowrate on the link in vehicles/second.
	* Note:  usually traffic flowrates are in vehicles per hour but they must be specified in vehicles per
	* second so that everything stays metric.
     */
    public highway_link(coordinate start_point, coordinate end_point, double link_width, double vehicle_EF,
			double traffic_flowrate)
    {
	setStart_location(start_point);    // set the begin and end points of the link
	setEnd_location(end_point);
	setWidth(link_width);		   // set link width

	this.vehicle_EF = vehicle_EF;    // set the traffic flowrate and vehicle EF
	this.traffic_flowrate = traffic_flowrate;

	// find the source strength based on traffic flowrate and vehicle EF
	setLine_strength(traffic_flowrate * vehicle_EF);
	determine_link_geometry();    // find link length and angle

	naming = new default_naming();    // get a default name for the link
    }

        /**
     * Creates a new <code>highway_link</code> instance with a default name
	* by specifying the geometry, traffic flowrate,and the aggregate per-vehicle emission factor.
	* The overall source strength of the link will be calculated from these values.
     *
     * @param start_point a <code>coordinate</code> value representing the begining of the link (based on traffic flow direction).
     * @param end_point a <code>coordinate</code> value representing the end of the link (based on traffic flow direction).
     * @param link_width a <code>double</code> value representing the width of the traveled way
     * @param vehicle_EF a <code>double</code> value representing the single vehicle aggregate emission factor in
     * (grams of pollutant)/(vehicle-m)  note: that is per meter not mile!
     * @param traffic_flowrate a <code>double</code> is the vehicle flowrate on the link in vehicles/second.
	* @param link_name is the user assigned name for the link
	* Note:  usually traffic flowrates are in vehicles per hour but they must be specified in vehicles per
	* second so that everything stays metric.
     */
    public highway_link(coordinate start_point, coordinate end_point, double link_width, double vehicle_EF,
			double traffic_flowrate, String link_name)
    {
	setStart_location(start_point);    // set the begin and end points of the link
	setEnd_location(end_point);
	setWidth(link_width);		   // set link width

	this.vehicle_EF = vehicle_EF;    // set the traffic flowrate and vehicle EF
	this.traffic_flowrate = traffic_flowrate;

	// find the source strength based on traffic flowrate and vehicle EF
	setLine_strength(traffic_flowrate * vehicle_EF);
	determine_link_geometry();    // find link length and angle

	naming = new default_naming(link_name);    // set the name for the link
    }



    /**
     * This method returns a clone of a called link object with a perfect copy of all
	* the called object's physical characteristics. Certain members of the called link
	* were intentially not copied to the cloned link to prevent confusion
     * as to whether the link holds old or new information.
     * The super-class link information that is not copied by this clone include the
	* point array and concentration members.  The highway_link members not copied are
	* the cross, line, & vertical profiles.
     *
     */
    public Object clone() throws CloneNotSupportedException
    {
	highway_link return_link = new highway_link();    // create a default highway link

	// set certain non-static base link properties of the return link = this link
	return_link.naming = new default_naming(this.naming.getName());    // get name

	return_link.setStart_location(this.getStart_location());	// start_location
	return_link.setEnd_location(this.getEnd_location());		// end_location
	return_link.setWidth(this.getWidth());				// width
	return_link.determine_link_geometry();				// link_angle and  link_length
	// do not set these base class properties
	// point_array
	// total_concentration
	// set certain highway link specific properties of the return link = this link
	return_link.setLine_strength(this.getLine_strength());		// line_strength
	return_link.setTraffic_flowrate(this.getTraffic_flowrate());    // traffic_flowrate
	return_link.setVehicle_EF(this.getVehicle_EF());		// vehicle_EF

	// do not set these base class properties
	// cross_profile
	// fetch_profile
	// vertical_profile
	return return_link;
    }

    /**
	* The <code>create_point_array</code> subroutine uses a highway_point_spacing object
	* to determine the point source array location and emission factors that are used
	* to represent the roadway link.  The points sources are then stored in the base class member
	* point_array.
	*/

    public void create_point_array()
    {
	highway_point_spacing hps = null;    // highway spacing object to assist in point generation

	this.getStart_location();
	this.getEnd_location();
	this.getWidth();
	this.getLine_strength();
	this.getReceptor_location();
	this.getDisp_param().getA();            // the MET data is not currently used to determine point spacing
	this.getDisp_param().getP();
	this.getDisp_param().getWind_angle();

	hps = new highway_point_spacing(this.getStart_location(), this.getEnd_location(), this.getWidth(),
					this.getLine_strength(), this.getReceptor_location(),
					this.getDisp_param().getA(), this.getDisp_param().getP(),
					this.getDisp_param().getWind_angle());

	this.setPoint_array(hps.return_point_array());    // get the point array from the hps object
	//System.out.println("points created = " + this.get_number_points());
    }


    /**
     * Get the value of line_strength in
	* grams of pollutant per meter per second [(grams of pollutant) / (m-s)]
     * @return value of line_strength.
     */
    public double getLine_strength()
    {
	return line_strength;
    }

    /**
     * Get the value of traffic_flowrate in vehicles/second.
 	* Note:  usually traffic flowrates are in vehicles per hour but they must be specified in vehicles per
	* second so that everything stays metric.

     * @return value of traffic_flowrate.
     */
    public double getTraffic_flowrate()
    {
	return traffic_flowrate;
    }

    /**
     * Get the value of vehicle_EF in
	* (grams of pollutant)/(vehicle-m)  note: that is per meter not mile!
     * @return value of vehicle_EF.
     */
    public double getVehicle_EF()
    {
	return vehicle_EF;
    }

    /**
     * Set the value of line_strength in
	* grams of pollutant per meter per second [(grams of pollutant) / (m-s)]
     * @param v  Value to assign to line_strength.
     */
    public void setLine_strength(double v)
    {
	this.line_strength = v;
    }

    /**
     * Set the value of traffic_flowrate in vehicles/second.
 	* Note:  usually traffic flowrates are in vehicles per hour but they must be specified in vehicles per
	* second so that everything stays metric.

     * @param v  Value to assign to traffic_flowrate.
     */
    public void setTraffic_flowrate(double v)
    {
	this.traffic_flowrate = v;
    }

    /**
     * Set the value of vehicle_EF in
	* (grams of pollutant)/(vehicle-m)  note: that is per meter not mile!
     * @param v  Value to assign to vehicle_EF.
     */
    public void setVehicle_EF(double v)
    {
	this.vehicle_EF = v;
    }

    /**
     * This routine is only used for debuggin purposes.  It returns link information
	* as a string.
     */
    public String toString()
    {
	String return_string = "";

	return_string += "\nLink name = " + this.naming.getName() + "\nLink Type = " + this.getClass().getName()
			 + "\nBegin Point " + this.getStart_location() + "\nEnd Point " + this.getEnd_location()
			 + "\nLink Angle " + this.getLink_angle() + "\nLink Length " + this.getLink_length()
			 + "\nLine Strength " + this.line_strength + "\nTraffic Flowrate " + this.traffic_flowrate
			 + "\nEmission Factor " + this.vehicle_EF + "\nConcentration contribution "
			 + this.getTotal_concentration() + "\nNumber of Points = ";

	if (this.getPoint_array() == null)
	{
	    return_string += "0";
	}
	else
	{
	    return_string += this.getPoint_array().length;
	}

	return return_string;
    }

}

