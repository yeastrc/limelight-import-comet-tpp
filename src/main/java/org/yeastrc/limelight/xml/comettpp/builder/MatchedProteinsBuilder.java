package org.yeastrc.limelight.xml.comettpp.builder;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

import org.yeastrc.limelight.limelight_import.api.xml_dto.LimelightInput;
import org.yeastrc.limelight.limelight_import.api.xml_dto.MatchedProtein;
import org.yeastrc.limelight.limelight_import.api.xml_dto.MatchedProteinLabel;
import org.yeastrc.limelight.limelight_import.api.xml_dto.MatchedProteins;
import org.yeastrc.limelight.xml.comettpp.objects.TPPReportedPeptide;
import org.yeastrc.proteomics.fasta.*;



/**
 * Build the MatchedProteins section of the limelight XML docs. This is done by finding all proteins in the FASTA
 * file that contains any of the peptide sequences found in the experiment. 
 * 
 * This is generalized enough to be usable by any pipeline
 * 
 * @author mriffle
 *
 */
public class MatchedProteinsBuilder {

	public static MatchedProteinsBuilder getInstance() { return new MatchedProteinsBuilder(); }

	/**
	 * Given the protein names reported by Comet as matches for each peptide, use the FASTA file to build the
	 * MatchedProteins section of the XML document. Return a mapping of protein names (reported by Comet as
	 * matches to any peptide) mapped to the id of the MatchedProtein for that name in the MatchedProteins
	 * section of the XML document. This can be used to populate the matched protein id for reported peptides.
	 *
	 * @param limelightInputRoot
	 * @param fastaFile
	 * @param reportedPeptides
	 * @return
	 * @throws Exception
	 */
	public Map<String, Integer> buildMatchedProteins( LimelightInput limelightInputRoot, File fastaFile, Collection<TPPReportedPeptide> reportedPeptides, boolean importDecoys, String decoyPrefix, boolean importIndependentDecoys, String independentDecoyPrefix ) throws Exception {

		System.err.print( " Matching peptides to proteins..." );

		// all protein names matched by any peptide
		Set<String> proteinNames = getAllProteinsFromResults( reportedPeptides );

		// find the proteins matched by any of these peptides (map of sequence => fasta annotations
		Map<String, MatchedProteinInformation> proteins = getProteinsUsingProteinNames( proteinNames, fastaFile );

		{
			Collection<String> proteinNamesNotFoundInFasta = getProteinNamesNotFoundInFasta(proteinNames, proteins);
			if (proteinNamesNotFoundInFasta.size() > 0) {
				throw new Exception("The following protein names were not found in FASTA: " + String.join(", ", proteinNamesNotFoundInFasta));
			}
		}

		// map and validate protein names to protein sequence ids
		Map<String, Integer> proteinNameIdMap = getMatchedProteinIdsForProteinNames( proteins, proteinNames );

		// create the XML and add to root element
		buildAndAddMatchedProteinsToXML( limelightInputRoot, proteins, importDecoys, decoyPrefix, importIndependentDecoys, independentDecoyPrefix );

		return proteinNameIdMap;
	}

	/**
	 * Return a collection of distinct protein names reported by Comet as a match to any peptide.
	 *
	 * @param reportedPeptides
	 * @return
	 */
	private Set<String> getAllProteinsFromResults( Collection<TPPReportedPeptide> reportedPeptides ) {

		Set<String> proteinNames = new HashSet<>();

		for( TPPReportedPeptide reportedPeptide : reportedPeptides ) {
			proteinNames.addAll( reportedPeptide.getProteinNames() );
		}

		return proteinNames;
	}


	/* ***************** REST OF THIS CAN BE MOVED TO CENTRALIZED LIB **************************** */


	private Collection<String> getProteinNamesNotFoundInFasta(Collection<String> proteinNames, Map<String, MatchedProteinInformation> proteinFastaAnnotations) {
		// Create a set to store all unique protein names from FASTA annotations
		Set<String> fastaNames = new HashSet<>();

		// Populate the set with names from all FASTA annotations
		for (MatchedProteinInformation mpi : proteinFastaAnnotations.values()) {
			for (FastaProteinAnnotation anno : mpi.getFastaProteinAnnotations()) {
				fastaNames.add(anno.getName());
			}
		}

		// Create a collection to store proteins not found in FASTA annotations
		Collection<String> proteinsNotFound = new HashSet<>();

		// Check which protein names are not present in the FASTA names set
		for (String proteinName : proteinNames) {
			if (!fastaNames.contains(proteinName)) {
				proteinsNotFound.add(proteinName);
			}
		}

		return proteinsNotFound;
	}


	/**
	 * Do the work of building the matched peptides element and adding to limelight xml root
	 *
	 * @param limelightInputRoot
	 * @param proteins
	 * @throws Exception
	 */
	private void buildAndAddMatchedProteinsToXML( LimelightInput limelightInputRoot,
												  Map<String, MatchedProteinInformation> proteins,
											 	  boolean importDecoys,
												  String decoyPrefix,
												  boolean importIndependentDecoys,
												  String independentDecoyPrefix) throws Exception {

		MatchedProteins xmlMatchedProteins = new MatchedProteins();
		limelightInputRoot.setMatchedProteins( xmlMatchedProteins );

		for( String sequence : proteins.keySet() ) {

			Collection<FastaProteinAnnotation> fastaAnnotations = proteins.get( sequence ).getFastaProteinAnnotations();

			if( fastaAnnotations.isEmpty() ) {
				throw new Exception( "Did not get any fasta annotations (ie, name or description) for sequence: " + sequence );
			}

			MatchedProtein xmlProtein = new MatchedProtein();
			xmlMatchedProteins.getMatchedProtein().add( xmlProtein );

			xmlProtein.setSequence( sequence );
			xmlProtein.setId( BigInteger.valueOf( proteins.get( sequence ).getId() ) );

			if(importDecoys) {
				if(isProteinDecoy(proteins.get( sequence ).getFastaProteinAnnotations(), decoyPrefix)) {
					xmlProtein.setIsDecoy(true);
				} else {
					xmlProtein.setIsDecoy(false);
				}
			}

			if(importIndependentDecoys) {
				if(isProteinIndependentDecoy(proteins.get( sequence ).getFastaProteinAnnotations(), independentDecoyPrefix)) {
					xmlProtein.setIsIndependentDecoy(true);
				} else {
					xmlProtein.setIsIndependentDecoy(false);
				}
			}

			for( FastaProteinAnnotation anno : fastaAnnotations ) {
				MatchedProteinLabel xmlMatchedProteinLabel = new MatchedProteinLabel();
				xmlProtein.getMatchedProteinLabel().add( xmlMatchedProteinLabel );

				xmlMatchedProteinLabel.setName( anno.getName() );

				if( anno.getDescription() != null )
					xmlMatchedProteinLabel.setDescription( anno.getDescription() );

				if( anno.getTaxonomId() != null )
					xmlMatchedProteinLabel.setNcbiTaxonomyId( new BigInteger( anno.getTaxonomId().toString() ) );
			}
		}
	}

	/**
	 * Return true if all fasta annotation protein names start with decoy prefix
	 *
	 * @param proteins
	 * @param decoyPrefix
	 * @return
	 */
	private boolean isProteinDecoy(Collection<FastaProteinAnnotation> proteins, String decoyPrefix) {

		if(decoyPrefix == null) { return false; }

		for(FastaProteinAnnotation anno : proteins) {
			if(!(anno.getName().startsWith(decoyPrefix))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return true if all fasta annotation protein names start with independent decoy prefix
	 *
	 * @param proteins
	 * @param independentDecoyPrefix
	 * @return
	 */
	private boolean isProteinIndependentDecoy(Collection<FastaProteinAnnotation> proteins, String independentDecoyPrefix) {

		if(independentDecoyPrefix == null) { return false; }

		for(FastaProteinAnnotation anno : proteins) {
			if(!(anno.getName().startsWith(independentDecoyPrefix))) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param proteinSequenceAnnotations
	 * @param proteinNames
	 * @return
	 * @throws Exception If more than one protein sequence is matched by any protein name or if no id can be found for a protein name
	 */
	private Map<String, Integer> getMatchedProteinIdsForProteinNames(Map<String, MatchedProteinInformation> proteinSequenceAnnotations, Set<String> proteinNames) throws Exception {
		Map<String, Integer> proteinNameIdMap = new HashMap<>();
		Map<String, Integer> nameToIdMap = new HashMap<>();

		// First pass to collect identifiers and detect duplicates
		for (Map.Entry<String, MatchedProteinInformation> entry : proteinSequenceAnnotations.entrySet()) {
			MatchedProteinInformation mpi = entry.getValue();
			for (FastaProteinAnnotation fpa : mpi.getFastaProteinAnnotations()) {
				String proteinName = fpa.getName();
				if (nameToIdMap.containsKey(proteinName)) {
					if (!nameToIdMap.get(proteinName).equals(mpi.getId())) {
						throw new Exception("Found more than one FASTA entry for protein name: " + proteinName);
					}
				} else {
					nameToIdMap.put(proteinName, mpi.getId());
				}
			}
		}

		// Second pass to validate all provided protein names are found and have a unique id
		for (String proteinName : proteinNames) {
			Integer id = nameToIdMap.get(proteinName);
			if (id == null) {
				throw new Exception("Could not find FASTA entry for protein name: " + proteinName);
			}
			proteinNameIdMap.put(proteinName, id);
		}

		return proteinNameIdMap;
	}


	private boolean proteinNamesContainFASTAEntry(Set<String> proteinNames, FASTAEntry fastaEntry) {
		for( FASTAHeader header : fastaEntry.getHeaders() ) {

			if(proteinNames.contains(header.getName())) {
				return true;
			}
		}//end iterating over fasta headers

		return false;
	}


	/**
	 * Get a mapping of protein sequence to the id to use for that sequence (in the MatchedProteins section) and
	 * the FASTA annotations to use for that protein sequence.
	 *
	 * @param proteinNames The collection of distinct protein names reported by Comet as a match for any peptide
	 * @param fastaFile The FASTA file that was searched.
	 * @return
	 * @throws Exception if there is a problem reading the FASTA file
	 */
	private Map<String, MatchedProteinInformation> getProteinsUsingProteinNames(Set<String> proteinNames, File fastaFile ) throws Exception {

		Map<String, MatchedProteinInformation> proteinAnnotations = new HashMap<>();

		try ( FASTAFileParser parser = FASTAFileParserFactory.getInstance().getFASTAFileParser( fastaFile ) ) {

			int count = 0;
			System.err.println( "" );

			for ( FASTAEntry entry = parser.getNextEntry(); entry != null; entry = parser.getNextEntry() ) {

				count++;

				System.err.print( "\tTested " + count + " FASTA entries...\r" );

				if(proteinNamesContainFASTAEntry(proteinNames, entry)) {

					String sequence = entry.getSequence().replace("*", "");

					MatchedProteinInformation mpi = null;
					Collection<FastaProteinAnnotation> fastaAnnotations = null;

					if( proteinAnnotations.containsKey( sequence ) ) {

						mpi = proteinAnnotations.get( sequence );
						fastaAnnotations = mpi.getFastaProteinAnnotations();
					} else {

						mpi = new MatchedProteinInformation();
						proteinAnnotations.put(sequence, mpi);

						mpi.setId(count);

						fastaAnnotations = new HashSet<>();
						mpi.setFastaProteinAnnotations( fastaAnnotations );
					}

					for( FASTAHeader header : entry.getHeaders() ) {

						FastaProteinAnnotation anno = new FastaProteinAnnotation();
						anno.setName( header.getName() );
						anno.setDescription( header.getDescription() );

						fastaAnnotations.add( anno );

					}//end iterating over fasta headers

				}

			}// end iterating over fasta entries

			System.err.print( "\n" );
		}

		return proteinAnnotations;
	}


	/**
	 * Information to include for a MatchedProtein (a distinct protein sequence)
	 */
	private class MatchedProteinInformation {

		private Collection<FastaProteinAnnotation> fastaProteinAnnotations;
		private Integer id;

		public Collection<FastaProteinAnnotation> getFastaProteinAnnotations() {
			return fastaProteinAnnotations;
		}

		public void setFastaProteinAnnotations(Collection<FastaProteinAnnotation> fastaProteinAnnotations) {
			this.fastaProteinAnnotations = fastaProteinAnnotations;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
	}


	/**
	 * An annotation for a protein in a Fasta file
	 *
	 * @author mriffle
	 *
	 */
	private class FastaProteinAnnotation {


		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((description == null) ? 0 : description.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((taxonomId == null) ? 0 : taxonomId.hashCode());
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof FastaProteinAnnotation))
				return false;
			FastaProteinAnnotation other = (FastaProteinAnnotation) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (!description.equals(other.description))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (taxonomId == null) {
				if (other.taxonomId != null)
					return false;
			} else if (!taxonomId.equals(other.taxonomId))
				return false;
			return true;
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Integer getTaxonomId() {
			return taxonomId;
		}
		public void setTaxonomId(Integer taxonomId) {
			this.taxonomId = taxonomId;
		}



		private String name;
		private String description;
		private Integer taxonomId;
		private MatchedProteinsBuilder getOuterType() {
			return MatchedProteinsBuilder.this;
		}


	}
	
}
