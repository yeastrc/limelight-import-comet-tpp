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

package org.yeastrc.limelight.xml.comettpp.main;

import org.yeastrc.limelight.xml.comettpp.builder.XMLBuilder;
import org.yeastrc.limelight.xml.comettpp.objects.CometParameters;
import org.yeastrc.limelight.xml.comettpp.objects.ConversionParameters;
import org.yeastrc.limelight.xml.comettpp.objects.TPPResults;
import org.yeastrc.limelight.xml.comettpp.reader.CometParamsReader;
import org.yeastrc.limelight.xml.comettpp.reader.TPPErrorAnalysis;
import org.yeastrc.limelight.xml.comettpp.reader.TPPErrorAnalyzer;
import org.yeastrc.limelight.xml.comettpp.reader.TPPResultsParser;

public class ConverterRunner {

	// quickly get a new instance of this class
	public static ConverterRunner createInstance() { return new ConverterRunner(); }
	
	
	public void convertCometTPPToLimelightXML( ConversionParameters conversionParameters ) throws Throwable {
	
		System.err.print( "Reading comet params into memory..." );
		CometParameters cometParams = CometParamsReader.getCometParameters( conversionParameters.getCometParametersFile() );
		System.err.println( " Done." );
		
		System.err.print( "Reading pepXML data into memory..." );
		TPPResults tppResults = TPPResultsParser.getTPPResults( conversionParameters.getPepXMLFile() );
		System.err.println( " Done." );
		
		System.err.print( "Performing FDR analysis of PeptideProphet probability scores..." );
		TPPErrorAnalysis ppErrorAnalysis = TPPErrorAnalyzer.performPeptideProphetAnalysis( tppResults, TPPErrorAnalyzer.TYPE_PEPTIDE_PROPHET );
		System.err.println( " Done." );
		
		TPPErrorAnalysis ipErrorAnalysis = null;
		if( tppResults.isHasIProphetResults() ) {
			System.err.print( "Performing FDR analysis of InterProphet probability scores..." );
			ipErrorAnalysis = TPPErrorAnalyzer.performPeptideProphetAnalysis( tppResults, TPPErrorAnalyzer.TYPE_INTER_PROPHET );
			System.err.println( " Done." );
		}

		System.err.print( "Writing out XML..." );
		(new XMLBuilder()).buildAndSaveXML( conversionParameters, tppResults, cometParams, ppErrorAnalysis, ipErrorAnalysis );
		System.err.println( " Done." );
		
	}
}
