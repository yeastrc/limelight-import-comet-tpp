/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.limelight.xml.comettpp.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.yeastrc.limelight.xml.comettpp.constants.Constants;
import org.yeastrc.limelight.xml.comettpp.objects.ConversionParameters;
import org.yeastrc.limelight.xml.comettpp.objects.ConversionProgramInfo;
import org.yeastrc.limelight.xml.comettpp.utils.Limelight_GetVersion_FromFile_SetInBuildFromEnvironmentVariable;

import picocli.CommandLine;

@CommandLine.Command(name = "java -jar " + Constants.CONVERSION_PROGRAM_NAME,
		mixinStandardHelpOptions = true,
		versionProvider = LimelightConverterVersionProvider.class,
		sortOptions = false,
		synopsisHeading = "%n",
		descriptionHeading = "%n@|bold,underline Description:|@%n%n",
		optionListHeading = "%n@|bold,underline Options:|@%n",
		description = "Convert the results of a Comet + TPP analysis to a Limelight XML file suitable for import into Limelight.\n\n" +
				"More info at: " + Constants.CONVERSION_PROGRAM_URI
)

/**
 * @author Michael Riffle
 * @date Feb 21, 2018
 *
 */
public class MainProgram implements Runnable {

	@CommandLine.Option(names = { "-c", "--comet-params" }, required = true, description = "Full path to the comet params file")
	private File cometParamsFile;

	@CommandLine.Option(names = { "-f", "--fasta-file" }, required = true, description = "Full path to FASTA file used in the experiment. E.g., /data/yeast.fa If not supplied, comet.params will be used to find FASTA file.")
	private File fastaFile;

	@CommandLine.Option(names = { "-p", "--pepxml-file" }, required = true, description = "Full path to pepXML file")
	private File pepXMLFile;

	@CommandLine.Option(names = { "-d", "--import-decoys" }, required = false, description = "If present, decoys will be included in the Limelight XML output. Note: PeptideProphet must be run with MINPROB=0 in order to output decoy hits.")
	private boolean importDecoys = false;

	@CommandLine.Option(names = { "-i", "--independent-decoy-prefix" }, required = false, description = "If present, any hits to proteins that begin with this string will be considered \"independent decoys,\" for the purpose of error estimation. See: https://pubmed.ncbi.nlm.nih.gov/21876204/")
	private String independentDecoyPrefix;

	@CommandLine.Option(names = { "-o", "--out-file" }, required = true, description = "Full path to use for the Limelight XML output file. E.g., /data/my_analysis/crux.limelight.xml")
	private File outFile;

	@CommandLine.Option(names = { "-v", "--verbose" }, required = false, description = "If this parameter is present, error messages will include a full stacktrace. Helpful for debugging.")
	private boolean verboseRequested = false;

	@CommandLine.Option(names = { "--open-mod" }, required = false, description = "If this parameter is present, the converter will run in open mod mode. Mass diffs on the PSMs will be treated as an unlocalized modification mass for the peptide.")
	private boolean isOpenMod = false;

	@CommandLine.Option(names = { "--add-comment" }, required = false, description = "Use this parameter to optionally add comments to the search. Can be used multiple times.")
	private String[] comments;

	private String[] args;

	public void run() {

		printRuntimeInfo();

		if( !cometParamsFile.exists() ) {
			System.err.println( "Could not find comet params file: " + cometParamsFile.getAbsolutePath() );
			System.exit( 1 );
		}

		if( !pepXMLFile.exists() ) {
			System.err.println( "Could not find pepXML file: " + pepXMLFile.getAbsolutePath() );
			System.exit( 1 );
		}

		if( !fastaFile.exists() ) {
			System.err.println( "Could not find fasta file: " + fastaFile.getAbsolutePath() );
			System.exit( 1 );
		}

		ConversionProgramInfo cpi = null;
		
		try {
			cpi = ConversionProgramInfo.createInstance( String.join( " ",  args ) );        
		} catch(Throwable t) {

			System.err.println("Error running conversion: " + t.getMessage());

			if(verboseRequested) {
				t.printStackTrace();
			}

			System.exit(1);
		}

		ConversionParameters cp = new ConversionParameters();
		cp.setConversionProgramInfo( cpi );
		cp.setFastaFile( fastaFile );
		cp.setCometParametersFile( cometParamsFile );
		cp.setPepXMLFile( pepXMLFile );
		cp.setLimelightXMLOutputFile( outFile );
		cp.setOpenMod(isOpenMod);
		cp.setImportDecoys(importDecoys);
		cp.setIndependentDecoyPrefix(independentDecoyPrefix);
		cp.setComments(comments);

		try {
			ConverterRunner.createInstance().convertCometTPPToLimelightXML(cp);
		} catch( Throwable t ) {
			System.err.println( "Error encountered: " + t.getMessage() );

			if(verboseRequested) {
				t.printStackTrace();
			}

			System.exit( 1 );
		}

		System.exit( 0 );
	}

	public static void main( String[] args ) {

		MainProgram mp = new MainProgram();
		mp.args = args;

		CommandLine.run(mp, args);
	}


	/**
	 * Print runtime info to STD ERR
	 * @throws Exception 
	 */
	public static void printRuntimeInfo() {

		try( BufferedReader br = new BufferedReader( new InputStreamReader( MainProgram.class.getResourceAsStream( "run.txt" ) ) ) ) {

			String line = null;
			while ( ( line = br.readLine() ) != null ) {

				line = line.replace( "{{URL}}", Constants.CONVERSION_PROGRAM_URI );
				line = line.replace( "{{VERSION}}", Limelight_GetVersion_FromFile_SetInBuildFromEnvironmentVariable.getVersion_FromFile_SetInBuildFromEnvironmentVariable() );

				System.err.println( line );
				
			}
			
			System.err.println( "" );

		} catch ( Exception e ) {
			System.out.println( "Error printing runtime information." );
		}
	}

}
