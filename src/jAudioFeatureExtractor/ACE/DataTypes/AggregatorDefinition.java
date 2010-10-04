/**
 * 
 */
package jAudioFeatureExtractor.ACE.DataTypes;

import java.io.Serializable;

/**
 * 
 * Provide basic metadata about an aggreagtor. This class does not hold any
 * data. Fields provided include name (which should be unique), description,
 * whether this aggregator is a general or specific aggregator, and the names of
 * each of the parameters this aggregator provides.
 * <p>
 * If the aggregator has no parameters, the list of parameters will be null.
 * 
 * @author Daniel McEnnis
 * 
 */
public class AggregatorDefinition implements Serializable {

	public String name;
	
	public String description;
	
	public boolean generic;
	
	public String[] parameters;
	
	public AggregatorDefinition(String name, String description, boolean generic,String[] parameters){
		this.name = name;
		this.description = description;
		this.generic = generic;
		this.parameters = parameters;
	}
}
