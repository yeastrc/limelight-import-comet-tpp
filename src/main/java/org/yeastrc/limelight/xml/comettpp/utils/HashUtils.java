package org.yeastrc.limelight.xml.comettpp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class HashUtils {

    /**
     * Get SHA-384 hash for the given file
     *
     * @param inputFile
     * @return
     * @throws Exception
     */
    public static byte[] compute_file_sha_384_hash( File inputFile ) throws Exception {

        MessageDigest messageDigest = MessageDigest.getInstance( "SHA-384" );

        try (FileInputStream fis = new FileInputStream( inputFile ); ) {

            byte[] dataBytes = new byte[4096];
            int numberBytesRead = 0;

            while ( ( numberBytesRead = fis.read( dataBytes ) ) != -1 ) {

                messageDigest.update( dataBytes, 0, numberBytesRead );
            }
        }

        return messageDigest.digest();
    }

}
