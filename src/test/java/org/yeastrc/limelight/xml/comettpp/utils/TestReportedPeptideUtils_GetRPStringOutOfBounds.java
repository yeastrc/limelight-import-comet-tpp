package org.yeastrc.limelight.xml.comettpp.utils;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestReportedPeptideUtils_GetRPStringOutOfBounds {

	private Map<Integer, BigDecimal> _MODS = null;
	private String _SEQUENCE = "PEPTIDE";
	
	@Before
	public void setUp() {

		_MODS = new HashMap<>();
		
		_MODS.put( 1, new BigDecimal( "17.33234" ) );
		_MODS.put( 9, new BigDecimal( "38.538222" ) );	//peptide is length 7
		
	}
	
	@Test
	public void test() {

		try {
			ReportedPeptideUtils.getReportedPeptideStringForSequenceAndMods( _SEQUENCE, _MODS );
			fail( "Should have gotten an exception..." );
			
		} catch ( Exception e ) {
			;
		}

	}
	
}
