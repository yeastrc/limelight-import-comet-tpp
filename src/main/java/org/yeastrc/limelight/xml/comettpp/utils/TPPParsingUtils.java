package org.yeastrc.limelight.xml.comettpp.utils;

import static java.lang.Math.toIntExact;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.systemsbiology.regis_web.pepxml.*;
import org.yeastrc.limelight.xml.comettpp.objects.CometParameters;
import org.yeastrc.limelight.xml.comettpp.objects.ConversionParameters;
import org.yeastrc.limelight.xml.comettpp.objects.ConversionProgramInfo;
import org.yeastrc.limelight.xml.comettpp.objects.TPPPSM;

import net.systemsbiology.regis_web.pepxml.ModInfoDataType.ModAminoacidMass;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.AnalysisSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AnalysisResult;

public class TPPParsingUtils {

	/**
	 * Attempt to get the comet version from the pepXML file. Returns "Unknown" if not found.
	 * 
	 * @param msAnalysis
	 * @return
	 */
	public static String getCometVersionFromXML( MsmsPipelineAnalysis msAnalysis ) {
		
		for( MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( SearchSummary searchSummary : runSummary.getSearchSummary() ) {

				if( searchSummary.getSearchEngine().value().equals( "Comet" ) ) {
					return searchSummary.getSearchEngineVersion();
				}
			
			}
		}
		
		return "Unknown";
	}
	
	/**
	 * Attempt to get the TPP version from the pepXML. Returns "Unknown" if not found.
	 * 
	 * @param msAnalysis
	 * @return
	 */
	public static String getTPPVersionFromXML( MsmsPipelineAnalysis msAnalysis ) {
		
		for( AnalysisSummary analysisSummary : msAnalysis.getAnalysisSummary() ) {
			
			for( Object o : analysisSummary.getAny() ) {
			
				// if iProphet was run, get version from its summary
				try {
					InterprophetSummary pps = (InterprophetSummary)o;
					return pps.getVersion();

				} catch( Throwable t ) { ; }
				
				// if iProphet was not run, get version from peptideprophet summary
				try {
					PeptideprophetSummary pps = (PeptideprophetSummary)o;
					return pps.getVersion();

				} catch( Throwable t ) { ; }
				
			}
		}
		
		return "Unknown";
	}
	
	/**
	 * Return true if the results can be expected to have iProphet data, false otherwise.
	 * 
	 * @param msAnalysis
	 * @return
	 */
	public static boolean getHasIProphetData( MsmsPipelineAnalysis msAnalysis ) {
		
		for( AnalysisSummary analysisSummary : msAnalysis.getAnalysisSummary() ) {
			
			for( Object o : analysisSummary.getAny() ) {
			
				try {
					InterprophetSummary pps = (InterprophetSummary)o;
					return true;

				} catch( Throwable t ) { ; }				
				
			}
		}
		
		return false;
	}

	/**
	 * Return true if the results can be expected to have PTMProphet data, false otherwise.
	 *
	 * @param msAnalysis
	 * @return
	 */
	public static boolean getHasPTMProphetData( MsmsPipelineAnalysis msAnalysis ) {

		for( AnalysisSummary analysisSummary : msAnalysis.getAnalysisSummary() ) {

			for( Object o : analysisSummary.getAny() ) {

				try {
					PtmprophetSummary pps = (PtmprophetSummary)o;
					return true;

				} catch( Throwable t ) { ; }

			}
		}

		return false;
	}


	/**
	 * Return true if this searchHit is a decoy. This means that it only matches
	 * decoy proteins.
	 *
	 * @param searchHit
	 * @return
	 */
	public static boolean searchHitIsDecoy( SearchHit searchHit, CometParameters cometParams ) {

		String protein = searchHit.getProtein();

		if( CometParsingUtils.isDecoyProtein( protein, cometParams ) ) {

			if( searchHit.getAlternativeProtein() != null ) {
				for( AltProteinDataType ap : searchHit.getAlternativeProtein() ) {

					if( !CometParsingUtils.isDecoyProtein( ap.getProtein(), cometParams ) ) {
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Return true if this searchHit is an independent decoy. This means that it only matches
	 * independent decoy proteins.
	 *
	 * @param searchHit
	 * @param independentDecoyPrefix
	 * @return
	 */
	public static boolean searchHitIsIndependentDecoy( SearchHit searchHit, String independentDecoyPrefix ) {

		if(independentDecoyPrefix == null) { return false; }

		String protein = searchHit.getProtein();
		if( protein.startsWith(independentDecoyPrefix ) ) {

			if( searchHit.getAlternativeProtein() != null ) {
				for( AltProteinDataType ap : searchHit.getAlternativeProtein() ) {
					if( !ap.getProtein().startsWith(independentDecoyPrefix ) ) {
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Return the top-most parent element of the pepXML file as a JAXB object.
	 * 
	 * @param file
	 * @return
	 * @throws Throwable
	 */
	public static MsmsPipelineAnalysis getMSmsPipelineAnalysis( File file ) throws Throwable {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(MsmsPipelineAnalysis.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		MsmsPipelineAnalysis msAnalysis = (MsmsPipelineAnalysis)jaxbUnmarshaller.unmarshal( file );
		
		return msAnalysis;
	}
	
	/**
	 * Get the retention time from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static BigDecimal getRetentionTimeFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return spectrumQuery.getRetentionTimeSec();
	}
	
	/**
	 * Get the neutral mass from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static BigDecimal getNeutralMassFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return spectrumQuery.getPrecursorNeutralMass();
	}
	
	/**
	 * Get the scan number from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static int getScanNumberFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return toIntExact( spectrumQuery.getStartScan() );
	}
	
	/**
	 * Get the charge from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static int getChargeFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return spectrumQuery.getAssumedCharge().intValue();
	}

	/**
	 * Get the scan filename from the spectrum query JAXB object.
	 *
	 * Parsed out of this string: Loo_2021_1108_RJ_48_0f1.00065.00065.3
	 * Where the correct answer is: Loo_2021_1108_RJ_48_0f1
	 * @param spectrumQuery
	 * @return
	 * @throws Exception if it cannot do this
	 */
	public static String getScanFilenameFromSpectrumQuery( SpectrumQuery spectrumQuery ) throws Exception {
		Pattern p = Pattern.compile("^(.+)\\.\\d+\\.\\d+\\.\\d+$");
		Matcher m = p.matcher(spectrumQuery.getSpectrum());

		if(m.matches()) {
			return m.group(1);
		}

		throw new Exception("Could not find name of spectral file in: " + spectrumQuery.getSpectrum());
	}

	/**
	 * Get a TPPPSM (psm object) from the supplied searchHit JAXB object.
	 *
	 * If the searchHit has no peptideprophet score, null is returned.
	 * @param searchHit
	 * @param charge
	 * @param scanNumber
	 * @param obsMass
	 * @param retentionTime
	 * @param cometParams
	 * @return
	 * @throws Throwable
	 */
	public static TPPPSM getPsmFromSearchHit(
			SearchHit searchHit,
			int charge,
			int scanNumber,
			BigDecimal obsMass,
			BigDecimal retentionTime,
			CometParameters cometParams,
			ConversionParameters conversionParameters) throws Throwable {
				
		TPPPSM psm = new TPPPSM();
		
		psm.setCharge( charge );
		psm.setScanNumber( scanNumber );
		psm.setPrecursorNeutralMass( obsMass );
		psm.setRetentionTime( retentionTime );
		
		psm.setPeptideSequence( searchHit.getPeptide() );
		psm.setMassDiff(searchHit.getMassdiff());
		psm.setxCorr( getScoreForType( searchHit, "xcorr" ) );
		psm.setDeltaCn( getScoreForType( searchHit, "deltacn" ) );

		// may not be present
		try {
			psm.setDeltaCnStar(getScoreForType(searchHit, "deltacnstar"));
		} catch(Exception e) { ; }

		psm.setSpScore( getScoreForType( searchHit, "spscore" ) );
		psm.setSpRank( getScoreForType( searchHit, "sprank" ) );
		psm.seteValue( getScoreForType( searchHit, "expect" ) );

		psm.setPeptideProphetProbability( getPeptideProphetProbabilityForSearchHit( searchHit ) );
		
		
		if( psm.getPeptideProphetProbability() == null ) {
			return null;
		}
		
		// this will set this to null if this was not an iProphet run
		psm.setInterProphetProbability( getInterProphetProbabilityForSearchHit( searchHit ) );

		try {
			psm.setProteinNames( getProteinNamesForSearchHit( searchHit, cometParams, conversionParameters ) );
		} catch( Throwable t ) {

			String error = "Error getting protein names for PSM.\n";
			error += "Psm: " + psm + "\n";
			error += "Error: " + t.getMessage();

			System.err.println( error );
			throw t;
		}
		
		try {
			psm.setModifications( getModificationsForSearchHit( searchHit ) );
		} catch( Throwable t ) {
			
			System.err.println( "Error getting mods for PSM. Error was: " + t.getMessage() );
			throw t;
		}
		
		return psm;
	}

	/**
	 * Get a PeptideProphet probability from the supplied searchHit JAXB object
	 *
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	public static BigDecimal getPeptideProphetProbabilityForSearchHit( SearchHit searchHit ) throws Exception {
		
		
		for( AnalysisResult ar : searchHit.getAnalysisResult() ) {
			if( ar.getAnalysis().equals( "peptideprophet" ) ) {
				
				for( Object o : ar.getAny() ) {
					
					try {
						
						PeptideprophetResult ppr = (PeptideprophetResult)o;
						return ppr.getProbability();
						
					} catch( Throwable t ) {
						
					}
					
				}
				
			}
		}
		
		return null;
	}

	/**
	 * Get a PeptideProphet probability from the supplied searchHit JAXB object
	 *
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	public static BigDecimal getInterProphetProbabilityForSearchHit( SearchHit searchHit ) throws Exception {
		
		
		for( AnalysisResult ar : searchHit.getAnalysisResult() ) {
			if( ar.getAnalysis().equals( "interprophet" ) ) {
				
				for( Object o : ar.getAny() ) {
					
					try {
						
						InterprophetResult ppr = (InterprophetResult)o;
						return ppr.getProbability();
						
					} catch( Throwable t ) {
						
					}
					
				}
				
			}
		}
		
		return null;
	}


	/**
	 * Get the requested score from the searchHit JAXB object
	 *
	 * @param searchHit
	 * @param type
	 * @return
	 * @throws Throwable
	 */
	public static BigDecimal getScoreForType( SearchHit searchHit, String type ) throws Throwable {
		
		for( NameValueType searchScore : searchHit.getSearchScore() ) {
			if( searchScore.getName().equals( type ) ) {
				
				return new BigDecimal( searchScore.getValueAttribute() );
			}
		}
		
		throw new Exception( "Could not find a score of name: " + type + " for PSM..." );		
	}

	/**
	 * Get the variable modifications from the supplied searchHit JAXB object
	 *
	 * @param searchHit
	 * @return
	 * @throws Throwable
	 */
	public static Map<Integer, BigDecimal> getModificationsForSearchHit( SearchHit searchHit ) throws Throwable {

		Map<Integer, BigDecimal> modMap = new HashMap<>();

		ModInfoDataType mofo = searchHit.getModificationInfo();
		if( mofo != null ) {
			for( ModAminoacidMass mod : mofo.getModAminoacidMass() ) {

				if( mod.getVariable() != null && mod.getVariable() != 0 ) {
					modMap.put( mod.getPosition().intValueExact(), BigDecimal.valueOf( mod.getVariable() ) );
				}
			}

			// set n-term mod at position 0
			if( mofo.getModNtermMass() != null && mofo.getModNtermMass() != 0 ) {
				modMap.put( 0, CometParsingUtils.getNTerminalModMass( BigDecimal.valueOf( mofo.getModNtermMass() ) ) );
			}

			// set c-term mod at peptide_length + 1
			if( mofo.getModCtermMass() != null && mofo.getModCtermMass() != 0 ) {
				modMap.put( searchHit.getPeptide().length() + 1, CometParsingUtils.getCTerminalModMass( BigDecimal.valueOf( mofo.getModCtermMass() ) ) );
			}
		}


		return modMap;
	}

	/**
	 * Get the protein names reported for this search hit
	 *
	 * @param searchHit
	 * @param cometParams
	 * @return
	 * @throws Throwable
	 */
	public static Collection<String> getProteinNamesForSearchHit(SearchHit searchHit, CometParameters cometParams, ConversionParameters conversionParameters) throws Throwable {

		Collection<String> proteins = new HashSet<>();

		if(searchHit.getProtein() != null) {
			if(conversionParameters.isImportDecoys() || (!conversionParameters.isImportDecoys() && !CometParsingUtils.isDecoyProtein( searchHit.getProtein(), cometParams ))) {
				proteins.add(searchHit.getProtein());
			}
		}

		if( searchHit.getAlternativeProtein() != null && searchHit.getAlternativeProtein().size() > 0 ) {

			for( AltProteinDataType apdt : searchHit.getAlternativeProtein() ) {
				if( !CometParsingUtils.isDecoyProtein( apdt.getProtein(), cometParams ) ) {
					proteins.add( apdt.getProtein() );
				}
			}

		}

		if( proteins.size() < 1 ) {
			throw new Exception( "Found zero target proteins for searchHit." );
		}

		return proteins;
	}
	
	
}
