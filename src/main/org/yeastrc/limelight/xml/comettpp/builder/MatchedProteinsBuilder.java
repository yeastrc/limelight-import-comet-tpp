package org.yeastrc.limelight.xml.comettpp.builder;

import java.io.File;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.yeastrc.limelight.limelight_import.api.xml_dto.LimelightInput;
import org.yeastrc.limelight.limelight_import.api.xml_dto.MatchedProtein;
import org.yeastrc.limelight.limelight_import.api.xml_dto.MatchedProteinLabel;
import org.yeastrc.limelight.limelight_import.api.xml_dto.MatchedProteins;
import org.yeastrc.limelight.xml.comettpp.objects.TPPReportedPeptide;
import org.yeastrc.limelight.xml.comettpp.utils.ProteinInferenceUtils;
import org.yeastrc.fasta.FASTAEntry;
import org.yeastrc.fasta.FASTAHeader;
import org.yeastrc.fasta.FASTAReader;


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
	 * Add all target proteins from the FASTA file that contain any of the peptides found in the experiment
	 * to the limelight xml document in the matched proteins sectioni.
	 * 
	 * @param limelightInputRoot
	 * @param fastaFile
	 * @param decoyIdentifiers
	 * @throws Exception
	 */
	public void buildMatchedProteins( LimelightInput limelightInputRoot, File fastaFile, Collection<TPPReportedPeptide> reportedPeptides ) throws Exception {
		

		// the proteins we've found
		Map<String, Collection<FastaProteinAnnotation>> proteins = getProteins( reportedPeptides, fastaFile );
		
		// create the XML and add to root element
		buildAndAddMatchedProteinsToXML( limelightInputRoot, proteins );
		
	}
	
	/**
	 * Do the work of building the matched peptides element and adding to limelight xml root
	 * 
	 * @param limelightInputRoot
	 * @param proteins
	 * @throws Exception
	 */
	private void buildAndAddMatchedProteinsToXML( LimelightInput limelightInputRoot, Map<String, Collection<FastaProteinAnnotation>> proteins ) throws Exception {
		
		MatchedProteins xmlMatchedProteins = new MatchedProteins();
		limelightInputRoot.setMatchedProteins( xmlMatchedProteins );
		
		for( String sequence : proteins.keySet() ) {
			
			if( proteins.get( sequence ).isEmpty() ) continue;
			
			MatchedProtein xmlProtein = new MatchedProtein();
        	xmlMatchedProteins.getMatchedProtein().add( xmlProtein );
        	
        	xmlProtein.setSequence( sequence );
        	        	
        	for( FastaProteinAnnotation anno : proteins.get( sequence ) ) {
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
	 * Get a map of the distinct target protein sequences mapped to a collection of target annotations for that sequence
	 * from the given fasta file, where the sequence contains any of the supplied peptide sequences
	 * 
	 * @param allPetpideSequences
	 * @param fastaFile
	 * @param decoyIdentifiers
	 * @return
	 * @throws Exception
	 */
	private Map<String, Collection<FastaProteinAnnotation>> getProteins( Collection<TPPReportedPeptide> reportedPeptides, File fastaFile ) throws Exception {
		
		// get a unique set of naked peptide sequence
		Collection<String> nakedPeptideSequences = getNakedPeptideSequences( reportedPeptides );
		
		Map<String, Collection<FastaProteinAnnotation>> proteinAnnotations = new HashMap<>();
		
		FASTAReader fastaReader = null;
		
		try {
			
			fastaReader = FASTAReader.getInstance( fastaFile );
			
			for( FASTAEntry entry = fastaReader.readNext(); entry != null; entry = fastaReader.readNext() ) {

				for( String nakedSequence : nakedPeptideSequences ) {
					
					if( ProteinInferenceUtils.proteinContainsReportedPeptide( entry.getSequence(), nakedSequence ) ) {
						
						// this protein has a matching peptide
						
						for( FASTAHeader header : entry.getHeaders() ) {
							
							if( !proteinAnnotations.containsKey( entry.getSequence() ) )
								proteinAnnotations.put( entry.getSequence(), new HashSet<FastaProteinAnnotation>() );
							
							FastaProteinAnnotation anno = new FastaProteinAnnotation();
							anno.setName( header.getName() );
							anno.setDescription( header.getDescription() );
		            		
							proteinAnnotations.get( entry.getSequence() ).add( anno );

						}//end iterating over fasta headers
						
						break;// no need to check more peptides for this protein, we found one						

					} // end if statement for protein containing peptide

				} // end iterating over peptide sequences
				
			}// end iterating over fasta entries
			
			
		} finally {
			if( fastaReader != null ) {
				fastaReader.close();
				fastaReader = null;
			}
		}
		
		return proteinAnnotations;
	}
	
	

	private Collection<String> getNakedPeptideSequences( Collection< TPPReportedPeptide > percolatorPeptides ) {
		
		Collection<String> nakedSequences = new HashSet<>();
		
		for( TPPReportedPeptide reportedPeptide : percolatorPeptides ) {
			nakedSequences.add( reportedPeptide.getNakedPeptide() );
		}
		
		
		return nakedSequences;
	}

	
	
	/**
	 * An annotation for a protein in a Fasta file
	 * 
	 * @author mriffle
	 *
	 */
	private class FastaProteinAnnotation {
		
		public int hashCode() {
			
			String hashString = this.getName();
			
			if( this.getDescription() != null )
				hashString += this.getDescription();
			
			if( this.getTaxonomId() != null )
				hashString += this.getTaxonomId().intValue();
			
			return hashString.hashCode();
		}
		
		/**
		 * Return true if name, description and taxonomy are all the same, false otherwise
		 */
		public boolean equals( Object o ) {
			try {
				
				FastaProteinAnnotation otherAnno = (FastaProteinAnnotation)o;
				
				if( !this.getName().equals( otherAnno.getName() ) )
					return false;
				
				
				if( this.getDescription() == null ) {
					if( otherAnno.getDescription() != null )
						return false;
				} else {
					if( otherAnno.getDescription() == null )
						return false;
				}
				
				if( !this.getDescription().equals( otherAnno.getDescription() ) )
					return false;
				
				
				if( this.getTaxonomId() == null ) {
					if( otherAnno.getTaxonomId() != null )
						return false;
				} else {
					if( otherAnno.getTaxonomId() == null )
						return false;
				}
				
				if( !this.getTaxonomId().equals( otherAnno.getTaxonomId() ) )
					return false;
				
				
				return true;
				
			} catch( Exception e ) {
				return false;
			}
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
		
	}
	
}
