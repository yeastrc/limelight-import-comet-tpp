package org.yeastrc.limelight.xml.comettpp.objects;

import java.util.Map;

public class TPPResults {

	private boolean hasIProphetResults;
	private String cometVersion;
	private String tppVersion;
	private Map<TPPReportedPeptide, Map<Integer, TPPPSM>> peptidePSMMap;
	private boolean deltaCNStarPresent = true;
	private boolean hasSubSearches = false;

	public boolean isHasSubSearches() {
		return hasSubSearches;
	}

	public void setHasSubSearches(boolean hasSubSearches) {
		this.hasSubSearches = hasSubSearches;
	}

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

	public boolean isDeltaCNStarPresent() {
		return deltaCNStarPresent;
	}

	public void setDeltaCNStarPresent(boolean deltaCNStarPresent) {
		this.deltaCNStarPresent = deltaCNStarPresent;
	}
}
