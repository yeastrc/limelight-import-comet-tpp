package org.yeastrc.limelight.xml.comettpp.objects;

import java.util.Map;

public class TPPResults {

	private boolean hasIProphetResults;
	private String cometVersion;
	private String tppVersion;
	private Map<TPPReportedPeptide, Map<Integer, TPPPSM>> peptidePSMMap;
	
	/**
	 * @return the cometVersion
	 */
	public String getCometVersion() {
		return cometVersion;
	}
	/**
	 * @param cometVersion the cometVersion to set
	 */
	public void setCometVersion(String cometVersion) {
		this.cometVersion = cometVersion;
	}
	/**
	 * @return the peptidePSMMap
	 */
	public Map<TPPReportedPeptide, Map<Integer, TPPPSM>> getPeptidePSMMap() {
		return peptidePSMMap;
	}
	/**
	 * @param peptidePSMMap the peptidePSMMap to set
	 */
	public void setPeptidePSMMap(Map<TPPReportedPeptide, Map<Integer, TPPPSM>> peptidePSMMap) {
		this.peptidePSMMap = peptidePSMMap;
	}
	public String getTppVersion() {
		return tppVersion;
	}
	public void setTppVersion(String tppVersion) {
		this.tppVersion = tppVersion;
	}
	public boolean isHasIProphetResults() {
		return hasIProphetResults;
	}
	public void setHasIProphetResults(boolean hasIProphetResults) {
		this.hasIProphetResults = hasIProphetResults;
	}


	
}
