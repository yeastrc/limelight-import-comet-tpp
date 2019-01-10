package org.yeastrc.limelight.xml.comettpp.objects;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class TPPPSM {

	private BigDecimal xCorr;
	private BigDecimal deltaCn;
	private BigDecimal deltaCnStar;
	private BigDecimal spScore;
	private BigDecimal spRank;
	private BigDecimal eValue;
	
	private BigDecimal peptideProphetProbability;
	private BigDecimal interProphetProbability;
	
	private int hitRank;
	
	
	private int scanNumber;
	private BigDecimal precursorNeutralMass;
	private int charge;
	private BigDecimal retentionTime;
	
	private String peptideSequence;
	
	private Map<Integer,BigDecimal> modifications;
	
	private BigDecimal peptideProphetFDR;
	private BigDecimal interProphetFDR;

	private Collection proteinNames;

	public Collection getProteinNames() {
		return proteinNames;
	}

	public void setProteinNames(Collection proteinNames) {
		this.proteinNames = proteinNames;
	}

	/**
	 * @return the xCorr
	 */
	public BigDecimal getxCorr() {
		return xCorr;
	}
	/**
	 * @param xCorr the xCorr to set
	 */
	public void setxCorr(BigDecimal xCorr) {
		this.xCorr = xCorr;
	}
	/**
	 * @return the deltaCn
	 */
	public BigDecimal getDeltaCn() {
		return deltaCn;
	}
	/**
	 * @param deltaCn the deltaCn to set
	 */
	public void setDeltaCn(BigDecimal deltaCn) {
		this.deltaCn = deltaCn;
	}
	/**
	 * @return the deltaCnStar
	 */
	public BigDecimal getDeltaCnStar() {
		return deltaCnStar;
	}
	/**
	 * @param deltaCnStar the deltaCnStar to set
	 */
	public void setDeltaCnStar(BigDecimal deltaCnStar) {
		this.deltaCnStar = deltaCnStar;
	}
	/**
	 * @return the spScore
	 */
	public BigDecimal getSpScore() {
		return spScore;
	}
	/**
	 * @param spScore the spScore to set
	 */
	public void setSpScore(BigDecimal spScore) {
		this.spScore = spScore;
	}
	/**
	 * @return the spRank
	 */
	public BigDecimal getSpRank() {
		return spRank;
	}
	/**
	 * @param spRank the spRank to set
	 */
	public void setSpRank(BigDecimal spRank) {
		this.spRank = spRank;
	}
	/**
	 * @return the eValue
	 */
	public BigDecimal geteValue() {
		return eValue;
	}
	/**
	 * @param eValue the eValue to set
	 */
	public void seteValue(BigDecimal eValue) {
		this.eValue = eValue;
	}
	
	/**
	 * @return the hitRank
	 */
	public int getHitRank() {
		return hitRank;
	}
	/**
	 * @param hitRank the hitRank to set
	 */
	public void setHitRank(int hitRank) {
		this.hitRank = hitRank;
	}
	/**
	 * @return the scanNumber
	 */
	public int getScanNumber() {
		return scanNumber;
	}
	/**
	 * @param scanNumber the scanNumber to set
	 */
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	/**
	 * @return the precursorNeutralMass
	 */
	public BigDecimal getPrecursorNeutralMass() {
		return precursorNeutralMass;
	}
	/**
	 * @param precursorNeutralMass the precursorNeutralMass to set
	 */
	public void setPrecursorNeutralMass(BigDecimal precursorNeutralMass) {
		this.precursorNeutralMass = precursorNeutralMass;
	}
	/**
	 * @return the charge
	 */
	public int getCharge() {
		return charge;
	}
	/**
	 * @param charge the charge to set
	 */
	public void setCharge(int charge) {
		this.charge = charge;
	}
	/**
	 * @return the retentionTime
	 */
	public BigDecimal getRetentionTime() {
		return retentionTime;
	}
	/**
	 * @param retentionTime the retentionTime to set
	 */
	public void setRetentionTime(BigDecimal retentionTime) {
		this.retentionTime = retentionTime;
	}
	/**
	 * @return the peptideSequence
	 */
	public String getPeptideSequence() {
		return peptideSequence;
	}
	/**
	 * @param peptideSequence the peptideSequence to set
	 */
	public void setPeptideSequence(String peptideSequence) {
		this.peptideSequence = peptideSequence;
	}
	/**
	 * @return the modifications
	 */
	public Map<Integer, BigDecimal> getModifications() {
		return modifications;
	}
	/**
	 * @param modifications the modifications to set
	 */
	public void setModifications(Map<Integer, BigDecimal> modifications) {
		this.modifications = modifications;
	}
	public BigDecimal getPeptideProphetFDR() {
		return peptideProphetFDR;
	}
	public void setPeptideProphetFDR(BigDecimal peptideProphetFDR) {
		this.peptideProphetFDR = peptideProphetFDR;
	}
	public BigDecimal getInterProphetFDR() {
		return interProphetFDR;
	}
	public void setInterProphetFDR(BigDecimal interProphetFDR) {
		this.interProphetFDR = interProphetFDR;
	}
	public BigDecimal getPeptideProphetProbability() {
		return peptideProphetProbability;
	}
	public void setPeptideProphetProbability(BigDecimal peptideProphetProbability) {
		this.peptideProphetProbability = peptideProphetProbability;
	}
	public BigDecimal getInterProphetProbability() {
		return interProphetProbability;
	}
	public void setInterProphetProbability(BigDecimal interProphetProbability) {
		this.interProphetProbability = interProphetProbability;
	}

	
	
}
