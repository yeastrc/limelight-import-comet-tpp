package org.yeastrc.limelight.xml.comettpp.builder;

import org.yeastrc.limelight.limelight_import.api.xml_dto.LimelightInput;
import org.yeastrc.limelight.xml.comettpp.utils.HashUtils;
import org.yeastrc.proteomics.fasta.FASTAEntry;
import org.yeastrc.proteomics.fasta.FASTAFileParser;
import org.yeastrc.proteomics.fasta.FASTAFileParserFactory;
import org.yeastrc.proteomics.fasta.FASTAHeader;

import java.io.File;
import java.math.BigInteger;

public class FastaFileStatisticsBuilder {

    public static FastaFileStatisticsBuilder getInstance() { return new FastaFileStatisticsBuilder(); }

    /**
     * Add all target proteins from the FASTA file that contain any of the peptides found in the experiment
     * to the limelight xml document in the matched proteins section.
     *
     * @param limelightInputRoot
     * @param fastaFile
     * @throws Exception
     */
    public void buildFastaFileStatistics(LimelightInput limelightInputRoot, File fastaFile, String decoyPrefix, String independentDecoyPrefix ) throws Exception {

        System.err.println( " Gathering FASTA file statistics..." );

        FastaFileStatistics fastaFileStatistics = getFastaFileStatistics(fastaFile, decoyPrefix, independentDecoyPrefix);

        System.err.println("    Found " + fastaFileStatistics.getTargetCount() + " targets");
        System.err.println("          " + fastaFileStatistics.getDecoyCount() + " decoys");
        if(independentDecoyPrefix != null) {
            System.err.println("          " + fastaFileStatistics.getIndependentDecoyCount() + " independent decoys");
        }

        org.yeastrc.limelight.limelight_import.api.xml_dto.FastaFileStatistics xmlFastaFileStatistics = new org.yeastrc.limelight.limelight_import.api.xml_dto.FastaFileStatistics();
        limelightInputRoot.setFastaFileStatistics(xmlFastaFileStatistics);

        xmlFastaFileStatistics.setSHA384(fastaFileStatistics.getSha384Hash());
        xmlFastaFileStatistics.setNumTargets(BigInteger.valueOf(fastaFileStatistics.getTargetCount()));
        xmlFastaFileStatistics.setNumDecoys(BigInteger.valueOf(fastaFileStatistics.getDecoyCount()));
        xmlFastaFileStatistics.setNumIndependentDecoys(BigInteger.valueOf(fastaFileStatistics.getIndependentDecoyCount()));

    }

    private FastaFileStatistics getFastaFileStatistics(File fastaFile, String decoyPrefix, String independentDecoyPrefix) throws Exception {

        int targetCount = 0;
        int decoyCount = 0;
        int independentDecoyCount = 0;

        try ( FASTAFileParser parser = FASTAFileParserFactory.getInstance().getFASTAFileParser( fastaFile ) ) {

            int count = 0;
            System.err.println("");

            for (FASTAEntry entry = parser.getNextEntry(); entry != null; entry = parser.getNextEntry()) {
                if(fastaEntryIsDecoy(entry, decoyPrefix)) {
                    decoyCount++;
                } else if(fastaEntryIsIndependentDecoy(entry, independentDecoyPrefix)) {
                    independentDecoyCount++;
                } else {
                    targetCount++;
                }
            }
        }

        byte[] fastaFileSha384Hash = HashUtils.compute_file_sha_384_hash(fastaFile);

        return new FastaFileStatistics(targetCount, decoyCount, independentDecoyCount, fastaFileSha384Hash);
    }

    /**
     * Return true if this FASTA sequence (entry) is a decoy. It is a decoy if all annotations for this
     * sequence in this FASTA file start with the decoy prefix. Will return false if there is no decoy
     * prefix.
     *
     * @param entry
     * @param decoyPrefix
     * @return
     */
    private boolean fastaEntryIsDecoy(FASTAEntry entry, String decoyPrefix) {
        if(decoyPrefix == null) { return false; }

        for(FASTAHeader header : entry.getHeaders()) {
            if(!(header.getName().startsWith(decoyPrefix))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return true if this FASTA sequence (entry) is an independent decoy. It is an independent decoy if all
     * annotations for this sequence in this FASTA file start with the independent decoy prefix. Will return
     * false if there is no independent decoy prefix.
     *
     * @param entry
     * @param independentDecoyPrefix
     * @return
     */
    private boolean fastaEntryIsIndependentDecoy(FASTAEntry entry, String independentDecoyPrefix) {
        if(independentDecoyPrefix == null) { return false; }

        for(FASTAHeader header : entry.getHeaders()) {
            if(!(header.getName().startsWith(independentDecoyPrefix))) {
                return false;
            }
        }

        return true;
    }


    private class FastaFileStatistics {
        private int targetCount;
        private int decoyCount;
        private int independentDecoyCount;
        byte[] sha384Hash;

        public FastaFileStatistics(int targetCount, int decoyCount, int independentDecoyCount, byte[] sha384Hash) {
            this.targetCount = targetCount;
            this.decoyCount = decoyCount;
            this.independentDecoyCount = independentDecoyCount;
            this.sha384Hash = sha384Hash;
        }

        public int getTargetCount() {
            return targetCount;
        }

        public int getDecoyCount() {
            return decoyCount;
        }

        public int getIndependentDecoyCount() {
            return independentDecoyCount;
        }

        public byte[] getSha384Hash() {
            return sha384Hash;
        }
    }


}
