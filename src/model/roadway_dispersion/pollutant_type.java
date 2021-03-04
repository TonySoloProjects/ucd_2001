package roadway_dispersion;
import utilities.*;    // access to simple utilites that make debugging/naming easier

/**
 * <b>Overview</b><br>
 * The pollutant type class is a simple way to store information about pollutants. This class stores the
 * name, descrition, moecular weight of CO and SF6 and allows the user to store information about
 * other pollutants as well.
 *
 * @author Tony Held
 * @version 1.0 (October 2001)
 */


public class pollutant_type
{
    private String    name;		   // name of pollutant
    private String    description;	   // description of pollutant
    private double    molecular_weight;    // MW of pollutant
    static public int CO = 0;		   // stored pollutant types
    static public int SF6 = 1;
    static public int F13B1 = 2;

    /**
     * construct a pollutant type given a name, description, and molecular weight
     */
    public pollutant_type(String name, String description, double molecular_weight)
    {
	this.name = name;
	this.description = description;
	this.molecular_weight = molecular_weight;
    }

    /**
     * construct a pollutant type given a name, and molecular weight
     */
    public pollutant_type(String name, double molecular_weight)
    {
	this.name = name;
	this.description = "No description";
	this.molecular_weight = molecular_weight;
    }

    /**
     * construct a pollutant based on the static constant list in the pollurant class
     */
    public pollutant_type(int known_polutant_type)
    {
	switch (known_polutant_type)
	{

	case 0:     // CO is the pollutant type
	    this.name = "Carbon Monoxide (CO)";
	    this.description = "Colorless, oderless gas that results from incomplete combustion "
			       + "such as automobile exhaust";
	    this.molecular_weight = 28.01;

	    break;

	case 1:     // SF6 is the pollutant type
	    this.name = "Sulfur Hexafloride (SF6)";
	    this.description = "Inert gas used as a tracer gas in dispersion experiments";
	    this.molecular_weight = 146.0504;

	    break;

        case 2:     // F13B1 is the pollutant type
        // F13B1 stands for Trifluorobromomethane (CAS 75-63-8)
        // and is a refrigerant with formula CBrF3 (MW=148.91)
        // see http://www.cdc.gov/niosh/npg/npgd0634.html
	    this.name = "Trifluorobromomethane (F13B1 or CBrF3)";
	    this.description = "Inert gas used as a tracer gas in dispersion experiments";
	    this.molecular_weight = 148.91;

	    break;

	default:    // unknown pollutant type is not allowed - use other constructor
	    System.out.println("An unknown pollutant type has been selected");
	    debugging.exception_handeler();
	}
    }

    // ---------------- Get - Set Pairs ----------------------------------


    public void SetName(String name)
    {
	this.name = name;
    }


    public String GetName()
    {
	return name;
    }

    public void SetDescription(String description)
    {
	this.description = description;
    }


    public String GetDescription()
    {
	return description;
    }


    public void SetMolecular_weight(double molecular_weight)
    {
	this.molecular_weight = molecular_weight;
    }


    public double GetMolecular_weight()
    {
	return molecular_weight;
    }
}

