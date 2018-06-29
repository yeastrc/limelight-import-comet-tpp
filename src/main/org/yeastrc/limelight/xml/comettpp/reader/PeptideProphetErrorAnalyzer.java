package org.yeastrc.limelight.xml.comettpp.reader;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.yeastrc.limelight.xml.comettpp.objects.TPPPSM;
import org.yeastrc.limelight.xml.comettpp.objects.TPPReportedPeptide;
import org.yeastrc.limelight.xml.comettpp.objects.TPPResults;

/**
 * PeptideProphet does not report a score with inherent meaning, at least towards the end of providing
 * a filter for its probability score that is applicable to every search performed. This utility class provides
 * a means for analyzing then determining the FDR associated with the probability score from a given search.
 * 
 * @author mriffle
 *
 */
public class PeptideProphetErrorAnalyzer {

	/**
	 * Analyze the target/decoy counts in the analysis
	 * 
	 * @throws Exception
	 */
	public static PeptideProphetErrorAnalysis performAnalysis( TPPResults results ) throws Exception {
		
		Map<BigDecimal, ProbabilitySumCounter> probabilitySums = new HashMap<BigDecimal, ProbabilitySumCounter>();
		
		/*
		 * First, compile a count for targets and decoys for each score reported for all PSMs
		 */
		
		for( TPPReportedPeptide tppRp : results.getPeptidePSMMap().keySet() ) {
			for( int scanNumber : results.getPeptidePSMMap().get( tppRp ).keySet() ) {
							
				TPPPSM psm = results.getPeptidePSMMap().get( tppRp ).get( scanNumber );
				
				BigDecimal score = psm.getPpProbability();
				ProbabilitySumCounter psc = null;
				
				if( probabilitySums.containsKey( score ) ) {
					psc = probabilitySums.get( score );
				} else {
					psc = new ProbabilitySumCounter();
					probabilitySums.put( score,  psc );
				}
				
				psc.setpCount( psc.getpCount() + score.doubleValue() );
				psc.setOneMinusPCount( psc.getOneMinusPCount() + ( 1.0 - score.doubleValue() ) );
			}
		}
		
		PeptideProphetErrorAnalysis ppa = new PeptideProphetErrorAnalysis();
		ppa.setProbabilitySums( probabilitySums );
		
		return ppa;
	}
}
