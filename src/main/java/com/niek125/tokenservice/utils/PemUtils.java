package com.niek125.tokenservice.utils;

import lombok.extern.log4j.Log4j2;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.File;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Log4j2
public class PemUtils {
    private PemUtils(){
        throw new IllegalStateException("Utility class");
    }

    private static byte[] parsePEMFile(File pemFile) {
        try (final PemReader reader = new PemReader(new FileReader(pemFile))) {
            final PemObject pemObject = reader.readPemObject();
            return pemObject.getContent();
        }catch (Exception e){
            log.error("The file {} doesn't exist.", pemFile.getAbsolutePath());
            return null;
        }
    }

    private static RSAPrivateKey getPrivateKey(byte[] keyBytes, String algorithm) {
        RSAPrivateKey privateKey = null;
        try {
            final KeyFactory kf = KeyFactory.getInstance(algorithm);
            final EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            privateKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error("Could not reconstruct the private key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            log.error("Could not reconstruct the private key");
        }
        return privateKey;
    }

    public static RSAPrivateKey readPrivateKeyFromFile(String filepath, String algorithm) {
        final byte[] bytes = PemUtils.parsePEMFile(new File(filepath));
        return PemUtils.getPrivateKey(bytes, algorithm);
    }

    private static PublicKey getPublicKey(byte[] keyBytes, String algorithm) {
        PublicKey publicKey = null;
        try {
            final KeyFactory kf = KeyFactory.getInstance(algorithm);
            final EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            publicKey = kf.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error("Could not reconstruct the public key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
            log.error("Could not reconstruct the public key");
        }

        return publicKey;
    }

    public static PublicKey readPublicKeyFromFile(String filepath, String algorithm) {
        final byte[] bytes = parsePEMFile(new File(filepath));
        return getPublicKey(bytes, algorithm);
    }
}

