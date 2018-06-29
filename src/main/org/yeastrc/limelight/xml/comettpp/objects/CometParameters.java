package org.yeastrc.limelight.xml.comettpp.objects;

import java.util.Map;

public class CometParameters {

	@Override
	public String toString() {

		String str = "Comet Parameters: ";
		
		if( this.staticMods != null ) {
			str += " Static mods: [";
			for( char r : this.staticMods.keySet() ) {
				str += "(";
				str += r + "," + this.staticMods.get( r );
				str += ")";
			}
			str += "]";
		}

		return str;
		
	}

	/**
	 * @return the staticMods
	 */
	public Map<Character, Double> getStaticMods() {
		return staticMods;
	}
	/**
	 * @param staticMods the staticMods to set
	 */
	public void setStaticMods(Map<Character, Double> staticMods) {
		this.staticMods = staticMods;
	}
	
	private Map<Character, Double> staticMods;
	
}
