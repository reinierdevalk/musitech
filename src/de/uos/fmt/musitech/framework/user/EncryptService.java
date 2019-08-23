/**********************************************

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see
<http://www.gnu.org/licenses/>.
In addition to the rights granted to the GNU General Public License,
you opt to use this program as specified in the following:

MUSITECH LINKING EXCEPTION

Linking this library statically or dynamically with other modules is making
a combined work based on this library. Thus, the terms and conditions of the
GNU General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you permission
to link this library with independent modules to produce an executable, regardless
of the license terms of these independent modules, and to copy and distribute the
resulting executable under terms of your choice, provided that you also meet,
for each linked independent module, the terms and conditions of the license of
that module. An independent module is a module which is not derived from or based
on this library.

For the MUSITECH library, this exceptional permission described in the paragraph
above is subject to the following three conditions:
- If you modify this library, you must extend the GNU General Public License and
       this exception including these conditions to your version of the MUSITECH library.
- If you distribute a combined work with this library, you have to mention the
       MUSITECH project and link to its web site www.musitech.org in a location
       easily accessible to the users of the combined work (typically in the "About"
       section of the "Help" menu) and in any advertising material for the combined
       software.
- If you distribute a combined work with the MUSITECH library, you allow the MUSITECH
               project to use mention your combined work for promoting the MUSITECH project.
       For the purpose of this licence, 'distribution' includes the provision of software
       services (e.g. over the World Wide Web).

**********************************************/
/*
 * File EncryptService.java
 * Created on 26.05.2004
 */

package de.uos.fmt.musitech.framework.user;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

/**
 * This is an encrypter for passwords using the Java Cryptography Architecture
 * (JCA) as explained in an <a
 * HREF="http://www.devarticles.com/c/a/Java/Password-Encryption%3A-Rationale-and-Java-Example">article
 * </A> by James Shvarts.
 * 
 * @author Tillman Weyde
 */

public final class EncryptService {

    private static EncryptService instance;

    private EncryptService() {
    }

    /**
     * Encrypts a string using the SHA-1 algorithm as implemented in the JCA.
     * 
     * @param plaintext the text to encrypt
     * @return the encrypted text.
     * @throws SystemUnavailableException when there is a problem preventing the
     *             encryption.
     */
    public synchronized String encrypt(String plaintext) throws SystemUnavailableException {
        MessageDigest md = null;
        // get a digest object
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new SystemUnavailableException(e.getMessage());
        }
        // feed the text in
        try {
            md.update(plaintext.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new SystemUnavailableException(e.getMessage());
        }
        // digest it
        byte raw[] = md.digest();
        // make a string
        String hash = (new BASE64Encoder()).encode(raw);
        // and return the result
        return hash;
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return The instance.
     */
    public static synchronized EncryptService getInstance() {
        if (instance == null) {
            return new EncryptService();
        } else {
            return instance;
        }
    }
    
    /**
     * Command line application for encrypting strings. 
     * 
     * @param args The first string passed will be encrypted.
     * @throws SystemUnavailableException If encryption system is not available.
     */
    public static void main(String[] args) throws SystemUnavailableException  {
        if(args.length < 1){
            System.out.println("USAGE: java EncryptService string_to_encrypt");
            return;
        }
        if(args.length >1){
            System.out.println("USAGE: java EncryptService string_to_encrypt");
            System.out.println("additional arguments will be ignored.");
        }
        EncryptService encServ = EncryptService.getInstance();
        System.out.println(encServ.encrypt(args[0]));
    }
}