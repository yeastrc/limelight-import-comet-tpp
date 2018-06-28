package org.yeastrc.limelight.xml.comettpp.utils;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestReportedPeptideUtils_GetRPStringNoMods {

	private Map<Integer, BigDecimal> _MODS = null;
	private String _SEQUENCE = "PEPTIDE";
	
	@Before
	public void setUp() {

		_MODS = new HashMap<>();
		
	}
	
	@Test
	public void test() throws Exception {

		assertEquals( "PEPTIDE", ReportedPeptideUtils.getReportedPeptideStringForSequenceAndMods( _SEQUENCE, _MODS ) );

	}
}
