package org.yeastrc.limelight.xml.comettpp.annotation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.limelight.limelight_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.limelight.limelight_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.limelight.xml.comettpp.constants.Constants;



public class PSMAnnotationTypes {

	// comet scores
	public static final String COMET_ANNOTATION_TYPE_XCORR = "XCorr";
	public static final String COMET_ANNOTATION_TYPE_DELTACN = "DeltaCN";
	public static final String COMET_ANNOTATION_TYPE_DELTACNSTAR = "DeltaCN*";
	public static final String COMET_ANNOTATION_TYPE_SPSCORE = "Sp Score";
	public static final String COMET_ANNOTATION_TYPE_SPRANK = "Sp Rank";
	public static final String COMET_ANNOTATION_TYPE_EXPECT = "E-Value";
	
	// PeptideProphet scores
	public static final String PPROPHET_ANNOTATION_TYPE_SCORE = "Probability Score";
	public static final String PPROPHET_ANNOTATION_TYPE_FDR = "Calculated FDR";
	
	
	
	public static List<FilterablePsmAnnotationType> getFilterablePsmAnnotationTypes( String programName ) {
		List<FilterablePsmAnnotationType> types = new ArrayList<FilterablePsmAnnotationType>();

		if( programName.equals( Constants.PROGRAM_NAME_COMET ) ) {
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( COMET_ANNOTATION_TYPE_XCORR );
				type.setDescription( "Comet cross-correlation coefficient" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
	
				types.add( type );
			}
			
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( COMET_ANNOTATION_TYPE_DELTACN );
				type.setDescription( "Difference between the XCorr of this PSM and the next best PSM (with a dissimilar peptide)" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				
				types.add( type );
			}
			
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( COMET_ANNOTATION_TYPE_DELTACNSTAR );
				type.setDescription( "Difference between the XCorr of this PSM and the next best PSM" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				
				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( COMET_ANNOTATION_TYPE_SPSCORE );
				type.setDescription( "Score indicating how well theoretical and actual peaks matched." );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				
				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( COMET_ANNOTATION_TYPE_SPRANK );
				type.setDescription( "The rank of this peptide match for this spectrum basedo n Sp Score" );
				type.setFilterDirection( FilterDirectionType.BELOW );
				
				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( COMET_ANNOTATION_TYPE_EXPECT );
				type.setDescription( "The e-value, or the estimation of the chance of observing a hit of this quality by chance." );
				type.setFilterDirection( FilterDirectionType.BELOW );
				
				types.add( type );
			}
			
		}

		else if( programName.equals( Constants.PROGRAM_NAME_PEPTIDEPROPHET ) ) {
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( PPROPHET_ANNOTATION_TYPE_SCORE );
				type.setDescription( "PeptideProphet Probability Score" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
	
				types.add( type );
			}
			
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( PPROPHET_ANNOTATION_TYPE_FDR );
				type.setDescription( "Calculated FDR from Probability Score" );
				type.setFilterDirection( FilterDirectionType.BELOW );
				type.setDefaultFilterValue( new BigDecimal( "0.01" ) );
	
				types.add( type );
			}
		}
		
		return types;
	}
	
	
}
