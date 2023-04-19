Comet/TPP to limelight XML Converter
=======================================

Use this program to convert the results of a Comet/TPP analysis to
limelight XML suitable for import into the limelight web application.

Tested on Comet version 2017.01 rev. 3 and TPP v5.0.0

How To Run
-------------
1. Download the [latest release](https://github.com/yeastrc/limelight-import-comet-tpp/releases).
2. Run the program ``java -jar cometTPP2LimelightXML.jar`` with no arguments to see the possible parameters. Requires Java 8 or higher.

Command line documentation
---------------------------

```
java -jar cometTPP2LimelightXML.jar [-dhvV] [--open-mod] -c=<cometParamsFile>
                                    -f=<fastaFile>
                                    [-i=<independentDecoyPrefix>] -o=<outFile>
                                    -p=<pepXMLFile>

Description:

Convert the results of a Comet + TPP analysis to a Limelight XML file suitable
for import into Limelight.

More info at: https://github.com/yeastrc/limelight-import-comet-tpp

Options:
  -c, --comet-params=<cometParamsFile>
                             Full path to the comet params file
  -f, --fasta-file=<fastaFile>
                             Full path to FASTA file used in the experiment. E.g.,
                               /data/yeast.fa If not supplied, comet.params will be
                               used to find FASTA file.
  -p, --pepxml-file=<pepXMLFile>
                             Full path to pepXML file
  -d, --import-decoys        If present, decoys will be included in the Limelight
                               XML output.
  -i, --independent-decoy-prefix=<independentDecoyPrefix>
                             If present, any hits to proteins that begin with this
                               string will be considered "independent decoys," for
                               the purpose of error estimation. See: https://pubmed.
                               ncbi.nlm.nih.gov/21876204/
  -o, --out-file=<outFile>   Full path to use for the Limelight XML output file. E.
                               g., /data/my_analysis/crux.limelight.xml
  -v, --verbose              If this parameter is present, error messages will
                               include a full stacktrace. Helpful for debugging.
      --open-mod             If this parameter is present, the converter will run in
                               open mod mode. Mass diffs on the PSMs will be treated
                               as an unlocalized modification mass for the peptide.
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
```
