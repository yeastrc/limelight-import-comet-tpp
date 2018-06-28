package org.yeastrc.limelight.xml.comettpp.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CometParamsReader {

	public static Map<Character, Double> getStaticModsFromParamsFile( InputStream paramsInputStream ) throws IOException {
		
		Map<Character, Double> staticMods = new HashMap<>();
		
		Pattern p = Pattern.compile( "^add_([A-Z])_.+=\\s+([0-9]+(\\.[0-9]+)?).*$" );
		
	    try (BufferedReader br = new BufferedReader( new InputStreamReader( paramsInputStream ) ) ) {
	    	
			for ( String line = br.readLine(); line != null; line = br.readLine() ) {		
				

				// skip immediately if it's not a line we want
				if( !line.startsWith( "add_" ) )
						continue;
				
				Matcher m = p.matcher( line );
				if( m.matches() ) {
					double d = Double.valueOf( m.group( 2 ) );
					
					if( d >= 0.000001 )
						staticMods.put( m.group( 1 ).charAt( 0 ), Double.valueOf( m.group( 2 ) ) );
				}
			}
	    	
	    }
		
		return staticMods;
	}
	
}
