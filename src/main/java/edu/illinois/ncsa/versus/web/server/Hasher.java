/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package edu.illinois.ncsa.versus.web.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author antoinev
 */
public class Hasher {

    private Hasher() {
    }

    public static String getHash(File file, String algorithm) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        return getHash(new FileInputStream(file), algorithm);
    }

    public static String getHash(InputStream inputStream, String algorithm) throws NoSuchAlgorithmException, IOException {
        DigestInputStream dis = null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            dis = new DigestInputStream(bis, md);
            while (dis.read() != -1);
            byte[] hash = md.digest();

            return byteArray2Hex(hash);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException ex) {
                    Logger.getLogger(Hasher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}