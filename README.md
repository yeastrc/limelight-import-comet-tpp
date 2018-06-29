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

Usage: java -jar cometTPP2LimelightXML.jar.jar -c path -p path -f path -o path

Example: java -jar magnum2LimelightXML.jar -c /path/to/comet.params
                                       -o /path/to/output.limelight.xml
                                       -p /path/to/pepXML.xml
                                       -f /path/to/fasta.fa

Options:
```
        -c      [Required] Path to comet .params file
        -o      [Required] Path to use for the limelight XML output file
        -f      [Required] Path to FASTA file used in the experiment.
        -p      [Required] Path to pepXML file
```
