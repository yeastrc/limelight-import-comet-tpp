/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.limelight.xml.comettpp.reader;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.yeastrc.limelight.xml.comettpp.objects.CometParameters;
import org.yeastrc.limelight.xml.comettpp.objects.TPPPSM;
import org.yeastrc.limelight.xml.comettpp.objects.TPPReportedPeptide;
import org.yeastrc.limelight.xml.comettpp.objects.TPPResults;
import org.yeastrc.limelight.xml.comettpp.utils.ReportedPeptideUtils;
import org.yeastrc.limelight.xml.comettpp.utils.TPPParsingUtils;

import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;

/**
 * @author Michael Riffle
 * @date Feb 21, 2018
 *
 */
public class TPPResultsParser {

	public static TPPResults getTPPResults(File pepXMLFile, CometParameters cometParams ) throws Throwable {

		Map<TPPReportedPeptide,Map<Integer,TPPPSM>> resultMap = new HashMap<>();
				
		MsmsPipelineAnalysis msAnalysis = null;
		try {
			msAnalysis = TPPParsingUtils.getMSmsPipelineAnalysis( pepXMLFile );
		} catch( Throwable t ) {
			System.err.println( "Got an error parsing the pep XML file. Error: " + t.getMessage() );
			throw t;
		}
		
		
		TPPResults results = new TPPResults();
		results.setPeptidePSMMap( resultMap );
		
		results.setTppVersion( TPPParsingUtils.getTPPVersionFromXML( msAnalysis ) );
		results.setCometVersion( TPPParsingUtils.getCometVersionFromXML( msAnalysis ) );
		
		results.setHasIProphetResults( TPPParsingUtils.getHasIProphetData( msAnalysis ) );
		
		
		for( MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( SpectrumQuery spectrumQuery : runSummary.getSpectrumQuery() ) {
				
				int charge = TPPParsingUtils.getChargeFromSpectrumQuery( spectrumQuery );
				int scanNumber = TPPParsingUtils.getScanNumberFromSpectrumQuery( spectrumQuery );
				BigDecimal neutralMass = TPPParsingUtils.getNeutralMassFromSpectrumQuery( spectrumQuery );
				BigDecimal retentionTime = TPPParsingUtils.getRetentionTimeFromSpectrumQuery( spectrumQuery );
				
				for( SearchResult searchResult : spectrumQuery.getSearchResult() ) {
					for( SearchHit searchHit : searchResult.getSearchHit() ) {
						
						// do not include decoy hits
						if( TPPParsingUtils.searchHitIsDecoy( searchHit, cometParams ) ) {
							continue;
						}
						
						TPPPSM psm = null;
						
						try {
							
							psm = TPPParsingUtils.getPsmFromSearchHit( searchHit, charge, scanNumber, neutralMass, retentionTime, cometParams );
							
						} catch( Throwable t) {
							
							System.err.println( "Error reading PSM from pepXML. Error: " + t.getMessage() );
							throw t;
							
						}
						
						if( psm != null ) {

							if(psm.getDeltaCnStar() == null) {
								results.setDeltaCNStarPresent( false );
							}

							TPPReportedPeptide tppRp = ReportedPeptideUtils.getTPPReportedPeptideForTPPPSM( psm );
							
							if( !results.getPeptidePSMMap().containsKey( tppRp ) )
								results.getPeptidePSMMap().put( tppRp, new HashMap<>() );
							
							results.getPeptidePSMMap().get( tppRp ).put( psm.getScanNumber(), psm );
						}
					}
				}
			}
		}
		
		return results;
	}
	
}
