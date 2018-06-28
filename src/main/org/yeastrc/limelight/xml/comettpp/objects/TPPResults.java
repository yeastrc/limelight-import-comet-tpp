package org.yeastrc.limelight.xml.comettpp.objects;

import java.util.Map;

public class TPPResults {

	private String cometVersion;
	private String peptideProphetVersion;
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
	 * @return the peptideProphetVersion
	 */
	public String getPeptideProphetVersion() {
		return peptideProphetVersion;
	}
	/**
	 * @param peptideProphetVersion the peptideProphetVersion to set
	 */
	public void setPeptideProphetVersion(String peptideProphetVersion) {
		this.peptideProphetVersion = peptideProphetVersion;
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
	
}
