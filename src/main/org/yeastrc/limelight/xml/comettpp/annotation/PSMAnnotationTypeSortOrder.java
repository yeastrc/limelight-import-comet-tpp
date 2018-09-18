package org.yeastrc.limelight.xml.comettpp.annotation;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.limelight.xml.comettpp.constants.Constants;

public class PSMAnnotationTypeSortOrder {

	public static List<SearchAnnotation> getPSMAnnotationTypeSortOrder( boolean haveIProphetData ) {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();
		
		if( haveIProphetData ) {
			
			{
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName( PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_FDR );
				annotation.setSearchProgram( Constants.PROGRAM_NAME_INTERPROPHET );
				annotations.add( annotation );
			}
			
			{
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName( PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_SCORE );
				annotation.setSearchProgram( Constants.PROGRAM_NAME_INTERPROPHET );
				annotations.add( annotation );
			}
			
		} else {
		
		
			{
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName( PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_FDR );
				annotation.setSearchProgram( Constants.PROGRAM_NAME_PEPTIDEPROPHET );
				annotations.add( annotation );
			}
			
			{
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName( PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_SCORE );
				annotation.setSearchProgram( Constants.PROGRAM_NAME_PEPTIDEPROPHET );
				annotations.add( annotation );
			}
			
		}
		
		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_EXPECT );
			annotation.setSearchProgram( Constants.PROGRAM_NAME_COMET );
			annotations.add( annotation );
		}

		
		return annotations;
	}
}
