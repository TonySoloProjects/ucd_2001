package roadway_dispersion;

import java.util.*;    // access to vector class
import utilities.*;    // access to my meteorological routines

/**
 * <b>Overview</b><br>
 * This class is used as a container to store information about a sampling site.
 * The class stores information such as link and receptor geometry, emission factors
 * and meteorological information.  Once all site information is set, the concentration
 * contribution from each link to each receptor can be determined.  The concentration
 * data is stored in the member conc_matrix.
 *
 * <br><b>Notes on Unit specifications<b>
 * Special attention should be made to ensure that the porper units are used for this object.
 * All length specifications should be in meters.  All time specifications should be in seconds.
 * The wind direction should be specified in degrees using standard meteorological convention.
 * Emission factors for vehicles should be in grams per meter per second,
 * not the convensional grams per mile per hour.
 * <br>For clarities sake:
 * <br>Input coordinates in meters.
 * <br>Wind should be specified in m/s at a refernce height specified in m.
 * <br>Wind angles should be specified in degrees using standard meteorological convention.
 * <br>Emission factors should be in grams per meter per second.
 * <br>Concentrations stored in the conc_matrix have units of grams/meter^3
 * <br>Temperature should be stated in degrees Kelvin (default is 300K).
 * <br>Pressure should be stated in ATMs (default is 1 ATM)
 *
 * @author Tony Held
 * @version 1.0 (October 2001)
 */


public class site_description
{
/**
 * The receptors vector maintains references to receptors in the sampling site.
 * Each receptor object stores the name and the coordinates of a receptor.
 */
    private Vector     receptors;		 // holds receptor name and coodintes

/**
 * The links vector maintains references to links in the sampling site.
 * Each link object stores the name, coordinates, and emission information for a link.
 */
    private Vector     links;

 /**
  * The dispersion_parameter object stores information about wind speed and direction in addition
  * to mixing and dispersion parameters such as eddy diffusivity constants and spreading parameters.
  */
    private dispersion dispersion_parameters;    // holds model parameters

/**
 * The conc_matrix stores the the concentration contribution from each link to each
 * receptor in the site_description.
 * Form of the matrix:
 * conc_matrix[i][j] represents the concentration at receptor i from link j.
 * The first column of the array conc_matrix[i][0] is a totals column -
 * which sums up each link's contribution to a given receptor.
 * The index of the link in the concentration matrix will be one more than the
 * index in the link_vector because the first link is the totals column.
 * For instance, if there are 2 links then the link vector will have members
 * 0 and 1.  The concentration matrix will have a column 0 and then the two links
 * will be 1 and 2. The concentration matrix uses the units g/m^3.
 */
    private double[][] conc_matrix;


 /**
  * Array to store the wind speed, angle and the reference elevation that the
  * wind was measured at.  In practice one can set the dispersion objects values
  * without indicating what the reference elevation is.  The wind_info members
  * are initialized to -999.9 to indicate that the values have not be set
  * programmatically.
  */
   private double[]    wind_info = {-999.9, -999.9,-999.9} ;

   /**
    * The ambient temperature at the sampling site in degrees Kelvin.
    * If the user does not specify a temperature, a value of 300 K will be used.
    * 300 K coresponds to temperature of approximately 27 C or 80 F.
    */
   private double site_temperature_in_K = 300.0;

   /**
    * The ambient presure at the sampling site in atmospheres.
    * If the user does not specify a pressure, a value of 1.0 will be used.
    */
   private double site_pressure_in_ATMS = 1.0;

   /**
    * The pollutant member stores the name and molecular weight of the pollutant of interest.
    * The pollutant MW is required if concentration unit conversions are made.  It is assumed
    * that the pollutant is SF6 unless otherwise specified.
    */
   private pollutant_type pollutant;


    /**
     * Constructor to create a new site_description.  Use the set routines to update the
	* receptor, link, and dispersion information.
     */
    public site_description()
    {
	// initialize all the vector objects with size 0 and increment by 10
	receptors = new Vector(0, 10);
	links = new Vector(0, 10);
	dispersion_parameters = new dispersion();
	pollutant = new pollutant_type(1);   // Assume the pollutant is SF6
    }

    /**
     * Remove all of the links from the site's link vector.
     */
    public void clear_links()
    {
	links.clear();    // remove all links
    }

    /**
     * Remove all of the receptors from the site's receptor vector.
     */
    public void clear_receptors()
    {
	receptors.clear();    // remove all receptors
    }

    /**
     * Remove all of the links, receptors, dispersion parameter, and concentration information
     * from the the sampling_site object.
     */
    public void clear_all()
    {
	links.clear();	      // remove all links
	receptors.clear();    // remove all receptors
	conc_matrix = null;   // delete the concentration matrix
	dispersion_parameters = null;    // reset the dispersion parameter to null
    }

    /**
     * Add a single link to the links vector.
     */
    public void add_link(link added_link)
    {
	links.add(added_link);
    }

     /**
	* Add multiple link to the site_description based on a Vector of link information.
	* The Vector must be a Vector of Vectors.  Each sub vector represents a single link.
	* The form of each sub vector is
	* "Link Name", X1, X2, Y1, Y2, Width, VPS, EF
	*  were the first member is of type String and the remaining values are type Double.
	*  All length measurements are in meters.
	*  X1, Y1 are the coordinates of the centerline of the link beggining, and
	*  X2, Y2 are the coordinates of the centerline of the link end.
	*  Width is the width of the traveled way.
	*  VPS is the vehicles flowrate in vehicles per second on the link.
	*  EF is the emission factor in grams per meter per second on the link.
	*  The UCD 2001 model assumes that all links are at z=0 so the link coordinates
	*  will automatically set the elevation of each link coordinate to 0.
	*/
   public void add_links(Vector link_vec)
    {
	 Vector temp_vec;                           // temporary Vector to store a single link vector
	 highway_link temp_link;                    // link to add to sites links vector
	 coordinate start_coord = new coordinate();  // temporary coordiante to store link start point
	 coordinate end_coord = new coordinate();    // temporary coordiante to store link end point
	 String temp_name;                          // temporary name to store link name
	 double width, vps, ef;                     // temporary varibles to store width, flowrate, and emission factor info

	 for (int i=0; i < link_vec.size(); i++)
	 {
	    temp_vec = (Vector) link_vec.get(i);     // get the sub vector
	    temp_name = (String) temp_vec.get(0);     // receptor name
	    start_coord.setX( ((Double) temp_vec.get(1)).doubleValue() );   // x1 coord
	    end_coord.setX( ((Double) temp_vec.get(2)).doubleValue() );     // x2 coord
	    start_coord.setY( ((Double) temp_vec.get(3)).doubleValue() );   // y1 coord
	    end_coord.setY( ((Double) temp_vec.get(4)).doubleValue() );     // y2 coord

	    width = ((Double) temp_vec.get(5)).doubleValue();            // width
	    vps = ((Double) temp_vec.get(6)).doubleValue();              // flowrate
	    ef = ((Double) temp_vec.get(7)).doubleValue();               // emission factor

	    temp_link = new highway_link(start_coord,end_coord,width,ef,vps);        // create the new link

	    this.add_link(temp_link);    // add the link to the site links vector
	 }
    }

    /**
     * Add a single receptor to the vector list of receptors for this sampling site.
     */
    public void add_receptor(receptor added_receptor)
    {
	receptors.add(added_receptor);
    }

    /**
     * This routine will determine the concentration contribution of each link
	* to each receptor.  This routine adds a copy of each link in the site to a single
	* receptor.  Each link is simulated as a 3-d point source array with point spacing
	* optomized based on wind direction and link and receptor coordinates.
     * @param print_point_arrays_to_text_file - set this parameter to true if you want
	* a text file listing point source locations and concentrations to be generated.
	* Note:  each link can be represented by ten's of thousands of points.  Care should be used
	* when setting the print flag to true.
     */
    public void run_simulation(boolean print_point_arrays_to_text_file)
    {
	// all links and points will share the same dispersion parameter values
	// next two lines makes sure that there is global access to dispersion parameters
	link.setDisp_param(this.dispersion_parameters);
	point.setDisp_param(this.dispersion_parameters);

	// add each of the links to each member of the receptor link vector
	// calculate the concentration contribution from each link to each receptor
	for (int i = 0; i < receptors.size(); i++)
	{
	    // get a reference to the current receptor
	    receptor current_receptor = (receptor) receptors.elementAt(i);

	    // add a copy of all the sampling site links to the receptor
	    // the name of the link will be changed to indicate ownership by the
	    // the current_receptor
	    current_receptor.addLinkCopy(links);
	    // create the point arrays for each link and find the concentration from each link
	    // the argument true indicates that point array locations and concentrations
	    // should be printed to a text file for processing with matlab
	    current_receptor.calculate_concentration(print_point_arrays_to_text_file);
	}

	// convert receptor concentration data into a concentration matrix to facilite
	// exporting of model results
	   create_conc_matrix();
    }

    /**
     * Set the dispersion parameters of the sampling site based on a copy of a
	* dispersion parameter object.
     */
    public void setDispersion(dispersion disp_param)
    {
	this.dispersion_parameters = disp_param;
    }

    /**
     * Return the dispersion parameters of the sampling site.
     */
    public dispersion getDispersion()
    {
	return this.dispersion_parameters;
    }

    /**
     *
     * main drives the creation of the Output text file with concentration estimations
     */
    // execution java roadway_dispersion.sampling_site arg0 arg1
    // user suplies the following arguments
    // arg0 - path to the input and output files (generally ~/phd)
    // arg1 - met period (usee -99 for all)

    public static void main(String[] args)
    {
    System.out.println("Test the UCD 2001 new interface");

    // create a site_description identical to the GM student
    site_description driver = new site_description();

    // create receptors based on receptor names and coordinates
    double[][]
	 gm_receptors_coordinates = {
							 { -42.7, -13.25, 9.58 },
							 { -42.7, -13.25, 3.51 },
							 { -42.7, -13.25, 0.51 },
							 { -14.6, -4.53, 9.5 },
							 { -14.6, -4.53, 3.63 },
							 { -14.6, -4.53, 0.56 },
							 { 0, 0, 9.58 },
							 { 0, 0, 3.05 },
							 { 0, 0, 0.56 },
							 { 16.5, 5.12, 9.63 },
							 { 16.5, 5.12, 3.61 },
							 { 16.5, 5.12, 0.51 },
							 { 27.7, 8.59, 9.5 },
							 { 27.7, 8.59, 3.48 },
							 { 27.7, 8.59, 0.56 },
							 { 42.7, 13.25, 9.6 },
							 { 42.7, 13.25, 3.84 },
							 { 42.7, 13.25, 0.58 },
							 { 62.7, 19.45, 0.56 },
							 { 112.7, 34.96, 0.56 } };

    String[]
      receptor_names = {
				   "Recp_1_1", "Recp_1_2", "Recp_1_3",
				    "Recp_2_1", "Recp_2_2", "Recp_2_3",
				    "Recp_3_1", "Recp_3_2", "Recp_3_3",
				    "Recp_4_1", "Recp_4_2", "Recp_4_3",
				    "Recp_5_1", "Recp_5_2", "Recp_5_3",
				    "Recp_6_1", "Recp_6_2", "Recp_6_3",
				    "Recp_7_3", "Recp_8_3"} ;

	 driver.add_receptors(gm_receptors_coordinates, receptor_names);

        // hard code the source strength of the link
        // need to convert from g/mile/hour to g/m/s
        double source_strength =
          convert_unit.grams_per_mile_per_hour_to_grams_per_meter_per_second(233.1);

	 // add the links
	 highway_link west_link, east_link;

      east_link = new highway_link( new coordinate( 9.3,-2500.0, 0.0),
				                new coordinate( 9.3, 2500.0, 0.0),
							 6.8, source_strength, "East_Link");

      west_link = new highway_link( new coordinate(-9.3,-2500.0, 0.0),
							 new coordinate(-9.3, 2500.0, 0.0),
	                               6.8, source_strength, "West_Link");

	 driver.add_link(east_link);
	 driver.add_link(west_link);


	 // set the met data
	 driver.setDispersion(1.76,17.0,4.5);     // wind speed, direction, and reference elevation

	 driver.set_site_temperature_in_K(274.15); // set temperature and pressure
	 driver.set_site_pressure_in_ATMS(convert_unit.mmHg_to_ATMS( 747.2 ) );

	 driver.run_simulation(false);
	 //driver.output_site_info();

         System.out.println( format_matrix.array_2_str( driver.get_conc_matrix(3),false ));
        }

    /**
     * Set the dispersion parameters for the site based on reference elevation
	* that wind measurements are made, wind speed, and wind direction.  Default values
	* for the other parameters are used based on the model validation with the GM database.
	* The arguments represent the wind speed in m/s measured at a given reference elevation in meters.
	* The wind direction uses standard meteorological convention for angle descriptions.
	* Wind directions represent the direction the wind is blowing too in degrees.
	* The meteorological coordinate system is zero to the north and angles are
	* measured in the clockwise direction.  For instance a wind direction of 90 represents a wind
	* from the east and a wind direction of 225 represents a wind from the southwest.  The meteorological
	* wind angle with be converted programatically to the radian equivalent in the standard
	* geometric angle notation for use in dispersion calculations.
	*
	* <br>
	* Essential Formulas and relationships:
     * <br>Wind thought to follow power law form of u(z)=a z^p
     * <br>Vertical eddy diffusivity thought to follow power law K= b z^n
     * <br>Modified BNL lateral standard deviation function of form sig(y) = c + d * x^e
	* <br> Based on the GM calibration, the following default dispersion parameters are used:
	* P = 0.25, N = 0.81, C = 3.0, D = 0.32, E = 0.78.
     */
    public void setDispersion(double wind_speed, double wind_direction, double wind_reference_elevation)
    {
	dispersion disp = new dispersion();    // create a new dispersion object

	// set the dispersion parameters based on model calibration with the GM dataset.
	disp.setP(0.25);   // nuetral wind profile exponent
	disp.setN(0.81);   // emperical eddy diffusivity power
	disp.setB(0.28);   // set emperical eddy diffusivity const
	disp.setC(3.0);    // BNL initial dispersion constant
	disp.setD(0.32);   // BNL constant
	disp.setE(0.78);   // BNL constant

	// set the wind direction based on the met_data
	disp.setWind_angle(met_processor.met_to_geometric_angle(wind_direction) * constants.DEGREES_TO_RADIANS);

	// find the wind profile constant 'a' based on the wind speed at a reference elevation
     // create wind vector with u determined at the elevation 4.5
	    wind_vector wv = new wind_vector(wind_speed,
							       wind_reference_elevation,
								  disp.getP(), disp.getWind_angle());

	    // find the (a) associated with this u and p at z
	    disp.setA(wv.getA());

	    this.dispersion_parameters = disp;  // set the site's dispersion parameters for this object

    }


   /**
     * Create the concentration matrix that lists the concentration contribution
	* of each link to each receptor.
     * Form of the matrix:
     * conc_matrix[i][j] represents the concentration at receptor i from link j.
     * The first column of the array conc_matrix[i][0] is a totals column -
     * which sums up each link's contribution to a given receptor.
     * The index of the link in the concentration matrix will be one more than the
     * index in the link_vector because the first link is the totals column.
     * For instance, if there are 2 links then the link vector will have members
     * 0 and 1.  The concentration matrix will have a column 0 and then the two links
     * will be 1 and 2.
     * The concentration matrix uses the units g/m^3.
	*/
    private void create_conc_matrix()
    {

	// counters
	int i,j;

	// redim the concentration matrix so that it can accomidate all the
	// receptors and links including a total column
	conc_matrix = new double[receptors.size()][links.size() + 1];

	// loop through each receptor and populate the concentraton array
	for (i = 0; i < receptors.size(); i++)
	{
	    // get a reference to the current receptor
	    receptor current_receptor = (receptor) receptors.elementAt(i);
	    double[] single_recept_conc = current_receptor.getConcentration();

	    // loop through each of the link concentrations and add that to the concentration array
	    for (j = 0; j < single_recept_conc.length; j++)
	    {
		conc_matrix[i][j + 1] = single_recept_conc[j];    // link total is member zero so j+1 is an offset
	    }

	    // find the total concentration for the receptor from all links
	    conc_matrix[i][0] = current_receptor.getTotal_concentration();
	}

    }


    /**
     *
     * Output sampling site information to the console for debugging purposes.
     */
    public void output_site_info()
    {
	System.out.println("-----------Sampling site information----------");
	System.out.println("Dispersion parameter information");
	System.out.println(dispersion_parameters);
	System.out.println("\nLink Information");

	if (links.size() == 0)
	{
	    System.out.println("No links for this sampling site");
	}
	else
	{
	    System.out.println(links.size() + " Links for this site");

	    for (int i = 0; i < links.size(); i++)
	    {
		System.out.println((link) links.elementAt(i));
	    }
	}

	System.out.println("\nReceptor Information");

	if (receptors.size() == 0)
	{
	    System.out.println("No receptors for this sampling site");
	}
	else
	{
	    System.out.println(receptors.size() + " Receptors for this site");

	    for (int i = 0; i < receptors.size(); i++)
	    {
		System.out.println((receptor) receptors.elementAt(i));
	    }
	}
    }
    /**
	* Add multiple receptors to the site_description based on an 2-d array of receptor
	* coordinates and a 1-d array of receptor names.  The format of the 2-d matrix is
	* x-location = receptor[i][0] , y-location = receptor[i][1] , z-location = receptor[i][2] ,
	* where i refers to index of the receptor.  The name of each receptor is stored in the
	* 1-d receptor_name array with the same index [i].
	*/

    public void add_receptors(double[][] receptor_coordinates, String[] receptor_names)
    {
	coordinate coord;    // used to make receptor creation clearer

//	loop through each coordinate and name pair and add a reference to the receptor
//   to the receptor vector.

	for (int i = 0; i < receptor_coordinates.length; i++)
	{
	    coord = new coordinate(receptor_coordinates[i][0], receptor_coordinates[i][1],
				   receptor_coordinates[i][2]);

	    this.add_receptor(new receptor(receptor_names[i], coord));
	}
    }

    /**
	* Add multiple receptors to the site_description based on a Vector of receptor information.
	* The Vector must be a Vector of Vectors.  Each sub vector represents a single receptor.
	* The form of each sub vector is String Name, Double x, Double y, Double z.
	*/
   public void add_receptors(Vector receptors)
    {
	 coordinate temp_coord = new coordinate();  // temporary coordiante to store receptor info
	 String temp_name;                          // temporary name to store receptor name
	 Vector temp_vec;                           // temporary Vector to store a single receptor vector

	 for (int i=0; i < receptors.size(); i++)
	 {
	    temp_vec = (Vector) receptors.get(i);     // get the sub vector
	    temp_name = (String) temp_vec.get(0);     // receptor name
	    temp_coord.setX( ((Double) temp_vec.get(1)).doubleValue() );   // x coord
	    temp_coord.setY( ((Double) temp_vec.get(2)).doubleValue() );   // y coord
    	    temp_coord.setZ( ((Double) temp_vec.get(3)).doubleValue() );   // z coord

	    this.add_receptor(new receptor(temp_name, temp_coord));
	 }
    }
   /**
    * Set the pressure at the site in atmospheres.
    */
   public void set_site_pressure_in_ATMS(double pressure)
   {
	 site_pressure_in_ATMS = pressure;
   }

   /**
    * Get the pressure at the site in atmospheres.
    */
   public double get_site_pressure_in_ATMS()
   {
	 return site_pressure_in_ATMS;
   }

   /**
    * Set the temperature at the site in degrees Kelvin.
    */
   public void set_site_temperature_in_K(double temp_in_K)
   {
	 site_temperature_in_K = temp_in_K;
   }

   /**
    * Get the temperature at the site in degrees Kelvin.
    */
   public double get_site_temperature_in_K()
   {
	 return site_temperature_in_K;
   }

    /**
    * Set the pollutant type.
    */

    public void set_pollutant( pollutant_type pt )
    {
	 this.pollutant = pt;
    }

    /**
	* Get the pollutant type.
	*/
    public pollutant_type get_pollutant( )
    {
	 return this.pollutant ;
    }

    /**
	* Return the concentration matrix representing the concentration contribution
	* from each link to each receptor.
	*  Form of the matrix:
     * conc_matrix[i][j] represents the concentration at receptor i from link j.
     * The first column of the array conc_matrix[i][0] is a totals column -
     * which sums up each link's contribution to a given receptor.
     * The index of the link in the concentration matrix will be one more than the
     * index in the link_vector because the first link is the totals column.
     * For instance, if there are 2 links then the link vector will have members
     * 0 and 1.  The concentration matrix will have a column 0 and then the two links
     * will be 1 and 2.
	* @param units indicates what units to return the concentration data in.
	* <br>Specify 1 for g/m^3.
	* <br>Specify 2 for parts per million by volume (PPMV).
	* <br>Specify 3 for parts per trillion by volume (PPTV).
	* To convert the concentration to mixing ratios, the estimated site temperature,
	* pressure, and pollutant MW are used.
	*/

	public double[][] get_conc_matrix(int units)
	{

	int i,j;     // counters use for looping
	double unit_conversion = 1.0;     // unit_conversion is used to convert concentrations
                                          // in g/m^3 to mixing ratios.  Initially this factor
					  // is 1.0 indicating that not conversion will be made.
	double[][] return_matrix;	  // concentrations in the specified units will be
			                  // returned with this variable
     double PPM_TO_PPT = 1E6;             // conversion factor from PPM to PPT

// determine what unit the user wants the concentration data to be returned in

	 switch (units)
	 {
	    case 1:  // units of g/m^3
		    unit_conversion = 1.0;   // no unit conversion is necessary
		    break;
	    case 2:  // units of PPMV
		    // using the ideal gas law, determine what a concentration in g/m^3
		    // whould need to be multiplied by to arrive a a mixing ratio in PPM
		    // this requires the temperature and pressure and MW of the pollutant
		    unit_conversion = convert_unit.G_PER_METER_CUBED_TO_PPM(
						  1.0, site_pressure_in_ATMS, site_temperature_in_K,
						  pollutant.GetMolecular_weight() );
			break;
	    case 3:  // units of PPTV
		        // similar to case 2, however, units will be reported in PPTV rather than PPMV
			unit_conversion = convert_unit.G_PER_METER_CUBED_TO_PPM(
				   1.0, site_pressure_in_ATMS, site_temperature_in_K,
				   pollutant.GetMolecular_weight() ) * PPM_TO_PPT;
			break;
	    case 4:  // units of ug/M^3
		        // scaled down version of case 1
			unit_conversion = 1.0E6;
			break;
	    default:
		  System.out.println("An unknown unit was specified to present concentration data. ");
		  System.out.println("A default unit of g/m^3 will be used however the data may be corrupted.");
		  break;
	 }


	// redim the return concentration matrix to same size as the concentration matrix
	return_matrix = new double[receptors.size()][links.size() + 1];

	// populate the return matrix with the concentration matrix
	// the routine will also convert the units to the desired format

	  for (i = 0; i < receptors.size(); i++)
	  {
	    for (j = 0; j < links.size() + 1 ; j++)
	    {
		  return_matrix[i][j] =  conc_matrix[i][j] * unit_conversion;
	    }
	  }

	  return return_matrix;     // send the concentration matrix back in the desired units

	}  // end method

}

