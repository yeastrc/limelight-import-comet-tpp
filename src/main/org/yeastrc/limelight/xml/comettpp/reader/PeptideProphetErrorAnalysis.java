package org.yeastrc.limelight.xml.comettpp.reader;

import java.math.BigDecimal;
import java.util.Map;

public class PeptideProphetErrorAnalysis {

	/**
	 * Get the error ( numincorr / ( numincorr + numcorr) ) associated with a given probability score, as calculated
	 * by the TPP
	 * 
	 * @param score
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getError( BigDecimal score ) throws Exception {
		
		if( this.probabilitySums == null )
			throw new Exception( "Must call performAnalysis() first." );
		
		if( !this.probabilitySums.containsKey( score ) )
			throw new Exception( "The score: " + score + " was not found in this search." );
		
		ProbabilitySumCounter psc = this.probabilitySums.get( score );
		double error = (double)psc.getOneMinusPCount() / ( psc.getpCount() + psc.getOneMinusPCount() );
		
		BigDecimal retValue = BigDecimal.valueOf( error );
		retValue = retValue.setScale(4, BigDecimal.ROUND_HALF_EVEN);
		
		return retValue;
	}
	
	/**
	 * @return the probabilitySums
	 */
	public Map<BigDecimal, ProbabilitySumCounter> getProbabilitySums() {
		return probabilitySums;
	}

	/**
	 * @param probabilitySums the probabilitySums to set
	 */
	protected void setProbabilitySums(Map<BigDecimal, ProbabilitySumCounter> probabilitySums) {
		this.probabilitySums = probabilitySums;
	}

	private Map<BigDecimal, ProbabilitySumCounter> probabilitySums;

}
