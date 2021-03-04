package roadway_dispersion;

import utilities.*;    // access to simple utilites that make debugging/naming easier
import java.util.*;    // access to vector class

/**
 * <b>Overview</b><br>
 * The <code>receptor</code> class represents a person or target coordinate near a roadway.
 * This class stores the pollutant concentration contribution from each link.
 * Note - All units in this file and the model are metric (MKS) unless otherwise stated.
 *
 * <br><tt><pre>
 * ACTION AND REVISION LOG
 * Author              Date    Action
 * --------------------------------------------------------------------------
 * Tony Held(TH)     11-15-99  cpp file created
 * TH                11-20-99  cpp class tested, debuged, and documented
 * TH                08-16-00  receptor class ported to java
 * </tt></pre>
 * @author Tony Held
 * @version 1.0 (October 2001)
 */
public class receptor
{

    /**
     * This routine removes all the links from the calling object's link vector.
     */
    public void clear_links()
    {
	links.clear();
    }

    /**
     * This routine determines the contributing concentrations from all members in the link
     * vector to this receptor.
     * @param print_point_arrays_to_text_file determines if the point array
     * for each link will be printed to a text file.
     */
    public void calculate_concentration(boolean print_point_arrays_to_text_file)
    {
	// set the static receptor location coordinate for each link and point
	// = the coordinate of this receptor
	link.setReceptor_location(this.getLocation());
	point.setReceptor_location(this.getLocation());

	// easier to read code if you have a reference to the link from the vector
	link current_link = null;

	// loop through each link - calculate its contribution to the receptor
	for (int index = 0; index < links.size(); index++)
	{
	    // get a reference to the link in the vector
	    current_link = (link) links.elementAt(index);

	    // have the link create its point array and calculate the concentration contribution
	    current_link.calculate_link_concentration();
	    // save the link concentration contribution for this link to the
	    add_concentration(index, current_link.getTotal_concentration());

	    // if the parameter flag was set to true have the link print its point array to a text file
	    if (true == print_point_arrays_to_text_file)
	    {
		current_link.print_link_to_file();
	    }

	    // clear the point arrays from each link to free up memory
	    current_link.clear_points();
	}
    }

    /**
     * This member adds default naming capacity for the receptors.
	* It allows a sequenced name to be assigned to each receptor if the user does not specify a name.
     */
    public default_naming naming;

    /**
     * Each receptor object has a copy of the links that contribute concentration to the receptor.
     */
    private Vector	  links;

    /**
     * Each link has a name assoicated with it.
	* The vector order of the name coresponds to the links vector.
     */
    private Vector	  link_names;

    /**
     * Each receptor has an internal vector that stores a copy of all the links
     * That contribute pollutant concentrations to it.  Use this method to add a copy
     * of a link to the receptor vector.  adding a link to a receptor will increment
     * the concentration array and reinitialize all the links all the concentration
     * contributions to each link = 0.0
     * @param link_to_add
     */
    public void addLinkCopy(link link_to_add)
    {
	try
	{
	    // save the original name of the link
	    link_names.add(link_to_add.naming.getName());
	    // add a copy of the link to the receptor vector
	    links.add((link) link_to_add.clone());
	    // change the link name to show receptor ownership
	    // for instance if you add link named Link_0 to a receptor
	    // with name Receptor_2 you will get the name
	    // Receptor_2-Owned-Link_0
	    ((link) links.lastElement()).naming.setName(this.naming.getName() + "-Owned-"
	     + ((link) links.lastElement()).naming.getName());
	}
	catch (CloneNotSupportedException e)
	{
	    System.out.println("Link Cloning Error");
	    e.getMessage();
	    e.printStackTrace();
	}
	// reset the concentration arrays and total concentration contribution
	finally
	{
	    total_concentration = 0.0;			 // reset total concentration = 0
	    concentration = new double[links.size()];    // reset concentration contribution from each link =0
	}
    }

    /**
     * Each receptor has an internal vector that stores a copy of all the links
     * That contribute pollutant concentrations to it.  Use this method to add a copy
     * of a vector of links to the receptor vector.
     * @param link_to_add
     */
    public void addLinkCopy(Vector link_vector_to_add)
    {
	try
	{
	    // loop through each link in the vector and add it to the receptor link vector
	    for (int i = 0; i < link_vector_to_add.size(); i++)
	    {
		// save the original name of the link
		link_names.add(((link) link_vector_to_add.elementAt(i)).naming.getName());
		// adds a copy of each member in link_vector_to_add to the internal receptor link
		// vector
		links.add(((link) link_vector_to_add.elementAt(i)).clone());
		// for instance if you add link named Link_0 to a receptor
		// with name Receptor_2 you will get the name
		// Receptor_2-Owned-Link_0
		((link) links.lastElement()).naming.setName(this.naming.getName() + "-Owned-"
		 + ((link) links.lastElement()).naming.getName());
	    }
	}
	catch (CloneNotSupportedException e)
	{
	    System.out.println("Link Cloning Error");
	    e.getMessage();
	    e.printStackTrace();
	}
	// reset the concentration arrays and total concentration contribution
	finally
	{
	    total_concentration = 0.0;			 // reset total concentration = 0
	    concentration = new double[links.size()];    // reset concentration contribution from each link =0
	}
    }

    /**
     * Routine to return a reference to a link in the link vector.  The index indicates the
	* possition of the desired link.
     */
    public link getLink(int index)
    {
	return (link) links.elementAt(index);
    }

    /**
     * <code>concentration</code> is an array with each member representing a pollutant concentration (g/m^3)
     * from each link. Note: remeber that array's index are zero based
     */
    private double[]   concentration;

    /**
     * <code>location</code> is the coordinate representing the location of the receptor.
     */
    private coordinate location;

    /**
     * Get the value of location.
     * @return value of location.
     */
    public coordinate getLocation()
    {
	return location;
    }

    /**
     * Set the value of location = copy of the coordinate argument.
     * @param v  Value to assign to location.
     */
    public void setLocation(coordinate v)
    {
	this.location = new coordinate(v);
    }

    /**
     * <code>total_concentration</code> of pollutants from all contributing links for this receptor.
     */
    private double total_concentration;

    /**
     * <code>getTotal_concentration</code> method returns the pollutant concentration from all known links.
     *
     * @return a <code>double</code> value representing the pollutant concentration in g/m^3.
     */
    public double getTotal_concentration()
    {
	return total_concentration;
    }

    /**
     * Creates a new <code>receptor</code> instance with a default coordinate location.
     */
    public receptor()
    {
	location = new coordinate();      // receptor location
	concentration = new double[1];    // each receptor must have storage for at least one link
	naming = new default_naming();    // initialize naming object and get default name
	links = new Vector(0, 10);	  // initialize the links and link_names vectors
	link_names = new Vector(0, 10);
    }

    /**
     * Creates a new <code>receptor</code> instance with a location identical to the passed
	* coordinate object argument.
     * @param coord a <code>coordinate</code> value specifying the new receptor location.
     */
    public receptor(coordinate coord)
    {
	location = new coordinate(coord);
	concentration = new double[1];    // each receptor must have storage for at least one link
	naming = new default_naming();    // initialize naming object
	links = new Vector(0, 10);	  // initialize the links and link_names vectors
	link_names = new Vector(0, 10);
    }

    /**
     * Creates a new <code>receptor</code> instance with a location identical to the passed argument
     * with a specified name.
     * @param coord a <code>coordinate</code> value specifying the new receptor location.
     * @param receptor_name a <code>String</code> value specifying the new receptor name.
     */
    public receptor(String receptor_name, coordinate coord)
    {
	location = new coordinate(coord);
	concentration = new double[1];		       // each receptor must have storage for at least one link
	naming = new default_naming(receptor_name);    // initialize naming object & set the receptor name
	links = new Vector(0, 10);		       // initialize the links and link_names vectors
	link_names = new Vector(0, 10);
    }

    /**
     * <code>known_links</code> return number of known links to
     * make sure concentration contributions indicies are valid.
     *
     * @return an <code>int</code> value representing the number of links to be modeled.
     */
    public int known_links()
    {
	return links.size();
    }

    /**
     * <code>add_concentration</code> adds a concentration contribution from a link to a receptor.
     *
     * @param link_num an <code>int</code> value representing the link that contributed a pollutant concentration.
     * @param x a <code>double</code> value representing the concentration contriubted from link link_num.
     */
    private void add_concentration(int link_num, double x)
    {
	// remeber that array index's are zero based
	// can only add to a valid link
	if (link_num < 0 || link_num >= known_links())
	{
	    System.err.println("An atempt was made to add a concentration to an undefined link"
			       + "\nThe program must be halted and debuged");
	    debugging.exception_handeler();
	}

	// can only add a positive concentration
	if (x < 0)
	{
	    System.err.println("An atempt was made to add a negative concentration"
			       + "\nThe program must be halted and debuged");
	    debugging.exception_handeler();
	}

	// if link and concentration is valid add the concentration contribution
	concentration[link_num] += x;
	// update the total concentration as well
	total_concentration += x;
    }

    /**
     * <code>out</code> method is only used for debugging purposes.
     * It outputs various information about a receptor to the default output.
     *
     */
    public String toString()
    {
	String return_string = "";    // string that will contain all information about receptor

	return_string = "\nReceptor Information for " + naming.getName()
			+ "\n\tThe coordinates of this receptor are " + location;

	if (known_links() > 0)
	{
	    for (int index = 0; index < links.size(); index++)
	    {
		// get a reference to the link and orignial link name at given index position
		link   temp_link = ((link) links.elementAt(index));
		String original_name = ((String) link_names.elementAt(index));

		return_string += "\n\nLink " + index + " of " + (links.size() - 1) + " (" + links.size()
				 + " total links)" + " Information ---------";
		return_string += "\nOriginal Link Name: " + original_name;
		return_string += temp_link.toString();
	    }
	}
	else
	{
	    return_string += "\n\tThere are no known links for this receptor";
	}

	return return_string;
    }

    /**
     * main is only used for debugging
     */
    public static void main(String[] argv)
    {
	System.out.println("Testing the default constructor and cleanup.");

	receptor recep1 = new receptor();

	System.out.println(recep1);

	receptor recep2 = new receptor();

	System.out.println(recep2);
	System.out.println("Testing the constructor with initial coordinate");

	coordinate coord1 = new coordinate(10, 20, 30);    // create a cordinate
	receptor   recep3 = new receptor(coord1);	   // constructor with inital coordinate

	System.out.println(recep3);
	System.out.println("The number of named receptors should be 3");
	System.out.println("Testing get and set for locations and name");
	coord1.set_all(100, 200, 300);
	recep3.setLocation(coord1);
	recep3.getLocation();
	recep3.naming.setName("I am a new name");

	String temp_name;

	temp_name = recep3.naming.getName();

	System.out.println("The new receptor name is:" + temp_name);
	System.out.println(recep3);
	System.out.println("Testing the initialization of a 10 element concentration contribution array");
	System.out.println("There are " + recep3.known_links() + " known links");
	System.out.println("Reseting the concentration array length to 6");
	recep3.add_concentration(0, 12);
	recep3.add_concentration(1, 24);
	recep3.add_concentration(3, 36);
	recep3.add_concentration(5, 48);
	System.out.println("Output the receptor information");
	System.out.println(recep3);
	System.out.println("Attempting to add a negative concentration");
	recep3.add_concentration(2, -23);
	System.out.println("Attempting to add a concentration to an invalid link");
	recep3.add_concentration(100, 2);
	System.out.println("Attempting to add a concentration to an invalid link");
	recep3.add_concentration(-200, 10.0);
	System.out.println("Debugging complete");
    }

    /**
     * return the concentration matrix for the calling receptor object
     */
    public double[] getConcentration()
    {
	return this.concentration;
    }
}

