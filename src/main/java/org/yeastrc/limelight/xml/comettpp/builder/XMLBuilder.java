package org.yeastrc.limelight.xml.comettpp.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.yeastrc.limelight.limelight_import.api.xml_dto.*;
import org.yeastrc.limelight.limelight_import.api.xml_dto.ReportedPeptide.ReportedPeptideAnnotations;
import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.limelight.limelight_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;
import org.yeastrc.limelight.xml.comettpp.annotation.PSMAnnotationTypeSortOrder;
import org.yeastrc.limelight.xml.comettpp.annotation.PSMAnnotationTypes;
import org.yeastrc.limelight.xml.comettpp.annotation.PSMDefaultVisibleAnnotationTypes;
import org.yeastrc.limelight.xml.comettpp.constants.Constants;
import org.yeastrc.limelight.xml.comettpp.objects.CometParameters;
import org.yeastrc.limelight.xml.comettpp.objects.ConversionParameters;
import org.yeastrc.limelight.xml.comettpp.objects.TPPPSM;
import org.yeastrc.limelight.xml.comettpp.objects.TPPReportedPeptide;
import org.yeastrc.limelight.xml.comettpp.objects.TPPResults;
import org.yeastrc.limelight.xml.comettpp.reader.TPPErrorAnalysis;
import org.yeastrc.limelight.xml.comettpp.utils.CometParsingUtils;

public class XMLBuilder {

	public void buildAndSaveXML( ConversionParameters conversionParameters,
			                     TPPResults tppResults,
			                     CometParameters cometParameters,
			                     TPPErrorAnalysis ppErrorAnalysis,
			                     TPPErrorAnalysis ipErrorAnalysis )
    throws Exception {

		LimelightInput limelightInputRoot = new LimelightInput();

		limelightInputRoot.setFastaFilename( conversionParameters.getFastaFile().getName() );
		
		// add in the conversion program (this program) information
		ConversionProgramBuilder.createInstance().buildConversionProgramSection( limelightInputRoot, conversionParameters);
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		limelightInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );
		
		if( tppResults.isHasIProphetResults() ) {
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
				
			searchProgram.setName( Constants.PROGRAM_NAME_INTERPROPHET );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_INTERPROPHET );
			searchProgram.setVersion( tppResults.getTppVersion() );
			
			
			//
			// Define the annotation types present in iprophet data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
			
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
			
			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes( Constants.PROGRAM_NAME_INTERPROPHET, tppResults.isDeltaCNStarPresent() ) ) {
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
		}
		
		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
				
			searchProgram.setName( Constants.PROGRAM_NAME_PEPTIDEPROPHET );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_PEPTIDEPROPHET );
			searchProgram.setVersion( tppResults.getTppVersion() );
			
			
			//
			// Define the annotation types present in peptideprophet data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
			
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
			
			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes( Constants.PROGRAM_NAME_PEPTIDEPROPHET, tppResults.isDeltaCNStarPresent() ) ) {
				
				// disable default filter on ppro FDR if ipro data are available
				if( annoType.getName().equals( PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_FDR ) && tppResults.isHasIProphetResults() ) {
					annoType.setDefaultFilterValue( null );
				}
				
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}

		}
		
		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
				
			searchProgram.setName( Constants.PROGRAM_NAME_COMET );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_COMET );
			searchProgram.setVersion( tppResults.getCometVersion() );
			
			
			//
			// Define the annotation types present in percolator data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
			
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
			
			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes( Constants.PROGRAM_NAME_COMET, tppResults.isDeltaCNStarPresent() ) ) {
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
			
			//todo: add in non-filterable annotations
		}
		
		
		//
		// Define which annotation types are visible by default
		//
		DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( xmlDefaultVisibleAnnotations );
		
		VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
		xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations( xmlVisiblePsmAnnotations );

		for( SearchAnnotation sa : PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes( tppResults.isHasIProphetResults() ) ) {
			xmlVisiblePsmAnnotations.getSearchAnnotation().add( sa );
		}
		
		//
		// Define the default display order in proxl
		//
		AnnotationSortOrder xmlAnnotationSortOrder = new AnnotationSortOrder();
		searchProgramInfo.setAnnotationSortOrder( xmlAnnotationSortOrder );
		
		PsmAnnotationSortOrder xmlPsmAnnotationSortOrder = new PsmAnnotationSortOrder();
		xmlAnnotationSortOrder.setPsmAnnotationSortOrder( xmlPsmAnnotationSortOrder );
		
		for( SearchAnnotation xmlSearchAnnotation : PSMAnnotationTypeSortOrder.getPSMAnnotationTypeSortOrder( tppResults.isHasIProphetResults() ) ) {
			xmlPsmAnnotationSortOrder.getSearchAnnotation().add( xmlSearchAnnotation );
		}
		
		//
		// Define the static mods
		//
		if( cometParameters.getStaticMods() != null && cometParameters.getStaticMods().keySet().size() > 0 ) {
			StaticModifications smods = new StaticModifications();
			limelightInputRoot.setStaticModifications( smods );
			
			
			for( char residue : cometParameters.getStaticMods().keySet() ) {
				
				StaticModification xmlSmod = new StaticModification();
				xmlSmod.setAminoAcid( String.valueOf( residue ) );
				xmlSmod.setMassChange( BigDecimal.valueOf( cometParameters.getStaticMods().get( residue ) ) );
				
				smods.getStaticModification().add( xmlSmod );
			}
		}
		
		// cache of FDRs calculated for specific PSM probabilities
		Map<BigDecimal, BigDecimal> ppFDRCache = new HashMap<>();
		Map<BigDecimal, BigDecimal> ipFDRCache = new HashMap<>();


		//
		// Build MatchedProteins section and get map of protein names to MatchedProtein ids
		//
		Map<String, Integer> proteinNameIds = MatchedProteinsBuilder.getInstance().buildMatchedProteins(
				limelightInputRoot,
				conversionParameters.getFastaFile(),
				tppResults.getPeptidePSMMap().keySet()
		);

		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		limelightInputRoot.setReportedPeptides( reportedPeptides );
		
		// iterate over each distinct reported peptide
		for( TPPReportedPeptide tppReportedPeptide : tppResults.getPeptidePSMMap().keySet() ) {
						
			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add( xmlReportedPeptide );
			
			xmlReportedPeptide.setReportedPeptideString( tppReportedPeptide.getReportedPeptideString() );
			xmlReportedPeptide.setSequence( tppReportedPeptide.getNakedPeptide() );


			MatchedProteinsForPeptide xProteinsForPeptide = new MatchedProteinsForPeptide();
			xmlReportedPeptide.setMatchedProteinsForPeptide( xProteinsForPeptide );

			// add in protein inference info
			for( String proteinName : tppReportedPeptide.getProteinNames() ) {

				int matchedProteinId = proteinNameIds.get( proteinName );

				MatchedProteinForPeptide xProteinForPeptide = new MatchedProteinForPeptide();
				xProteinsForPeptide.getMatchedProteinForPeptide().add( xProteinForPeptide );

				xProteinForPeptide.setId( BigInteger.valueOf( matchedProteinId ) );
			}

			// add in the filterable peptide annotations (e.g., q-value)
			ReportedPeptideAnnotations xmlReportedPeptideAnnotations = new ReportedPeptideAnnotations();
			xmlReportedPeptide.setReportedPeptideAnnotations( xmlReportedPeptideAnnotations );

			// add in the mods for this peptide
			if( tppReportedPeptide.getMods() != null && tppReportedPeptide.getMods().keySet().size() > 0 ) {

				PeptideModifications xmlModifications = new PeptideModifications();
				xmlReportedPeptide.setPeptideModifications( xmlModifications );

				for( int position : tppReportedPeptide.getMods().keySet() ) {

					PeptideModification xmlModification = new PeptideModification();
					xmlModifications.getPeptideModification().add( xmlModification );

					xmlModification.setMass( tppReportedPeptide.getMods().get( position ) );

					if( CometParsingUtils.isNTerminalMod( tppReportedPeptide.getNakedPeptide(), position ) ) {

						xmlModification.setIsNTerminal( true );

					} else if( CometParsingUtils.isCTerminalMod( tppReportedPeptide.getNakedPeptide(), position ) ) {

						xmlModification.setIsCTerminal( true );

					} else {
						xmlModification.setPosition( BigInteger.valueOf( position ) );
					}
				}
			}

			
			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms( xmlPsms );

			// iterate over all PSMs for this reported peptide

			for( int scanNumber : tppResults.getPeptidePSMMap().get( tppReportedPeptide ).keySet() ) {

				TPPPSM psm = tppResults.getPeptidePSMMap().get( tppReportedPeptide ).get( scanNumber );

				Psm xmlPsm = new Psm();
				xmlPsms.getPsm().add( xmlPsm );

				xmlPsm.setScanNumber( new BigInteger( String.valueOf( scanNumber ) ) );
				xmlPsm.setPrecursorCharge( new BigInteger( String.valueOf( psm.getCharge() ) ) );

				// add in the filterable PSM annotations (e.g., score)
				FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
				xmlPsm.setFilterablePsmAnnotations( xmlFilterablePsmAnnotations );

				// handle comet scores
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_DELTACN );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET );
					xmlFilterablePsmAnnotation.setValue( psm.getDeltaCn() );
				}
				if(tppResults.isDeltaCNStarPresent()) {
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_DELTACNSTAR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET );
					xmlFilterablePsmAnnotation.setValue( psm.getDeltaCnStar() );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_EXPECT );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET );
					xmlFilterablePsmAnnotation.setValue( psm.geteValue() );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_SPRANK );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET );
					xmlFilterablePsmAnnotation.setValue( psm.getSpRank() );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_SPSCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET );
					xmlFilterablePsmAnnotation.setValue( psm.getSpScore() );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_XCORR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET );
					xmlFilterablePsmAnnotation.setValue( psm.getxCorr() );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_MASS_DIFF );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET );
					xmlFilterablePsmAnnotation.setValue( psm.getMassDiff() );
				}


				// handle peptide prophet scores
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_SCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PEPTIDEPROPHET );
					xmlFilterablePsmAnnotation.setValue( psm.getPeptideProphetProbability() );
				}
				
				
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_FDR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PEPTIDEPROPHET );
					
					BigDecimal error = null;
					if( ppFDRCache.containsKey( psm.getPeptideProphetProbability() ) ) {
						error = ppFDRCache.get( psm.getPeptideProphetProbability() );
					} else {
						error = ppErrorAnalysis.getError( psm.getPeptideProphetProbability() );
						ppFDRCache.put( psm.getPeptideProphetProbability(), error );
					}
					
					xmlFilterablePsmAnnotation.setValue( error );
				}
				
				// handle interprophet scores
				if( tppResults.isHasIProphetResults() ) {
					
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

						xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_SCORE );
						xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_INTERPROPHET );
						xmlFilterablePsmAnnotation.setValue( psm.getInterProphetProbability() );
					}
					
					
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

						xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_FDR );
						xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_INTERPROPHET );
						
						BigDecimal error = null;
						if( ipFDRCache.containsKey( psm.getInterProphetProbability() ) ) {
							error = ipFDRCache.get( psm.getInterProphetProbability() );
						} else {
							error = ipErrorAnalysis.getError( psm.getInterProphetProbability() );
							ipFDRCache.put( psm.getInterProphetProbability(), error );
						}
						
						xmlFilterablePsmAnnotation.setValue( error );
					}
					
				}

				if(conversionParameters.isOpenMod()) {
					PsmOpenModification xmlPsmOpenMod = new PsmOpenModification();
					xmlPsmOpenMod.setMass(psm.getMassDiff());
					xmlPsm.setPsmOpenModification(xmlPsmOpenMod);
				}
				
				
			}// end iterating over psms for a reported peptide
		
		}//end iterating over reported peptides

		
		// add in the config file(s)
		ConfigurationFiles xmlConfigurationFiles = new ConfigurationFiles();
		limelightInputRoot.setConfigurationFiles( xmlConfigurationFiles );
		
		ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
		xmlConfigurationFiles.getConfigurationFile().add( xmlConfigurationFile );
		
		xmlConfigurationFile.setSearchProgram( Constants.PROGRAM_NAME_COMET );
		xmlConfigurationFile.setFileName( conversionParameters.getCometParametersFile().getName() );
		xmlConfigurationFile.setFileContent( Files.readAllBytes( FileSystems.getDefault().getPath( conversionParameters.getCometParametersFile().getAbsolutePath() ) ) );
		
		
		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain( conversionParameters.getLimelightXMLOutputFile(), limelightInputRoot);
		
	}
	
	
}
