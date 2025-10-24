package com.tapinwallet.util;

import java.io.StringWriter;
import java.security.*;
import java.security.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

/**
 * Minimal mirror of backend Crypt.java for key generation + signing.
 * Do NOT modify logic to preserve compatibility.
 */
public final class CryptLite {

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        
        for (var p : Security.getProviders()) {
            System.out.println("XXX Provider: " + p.getName());
        }
    }

    private CryptLite() {}

    /** Generate an RSA keypair identical to backend Crypt.java */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
            SecureRandom secureRandom = generateSecureRandom();
            keyGen.initialize(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4), secureRandom);
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Key generation failed", e);
        }
    }

    /** Sign data using RSASSA-PSS (SHA256withRSA/PSS) exactly like backend Crypt.java */
    public static String sign(String data, PrivateKey privateKey) {
        try {
            Signature privateSignature = Signature.getInstance("SHA256withRSA/PSS", "BC");
            privateSignature.setParameter(
                new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1)
            );
            privateSignature.initSign(privateKey);
            privateSignature.update(data.getBytes());
            byte[] signature = privateSignature.sign();
            return new String(encodeBytes(signature));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Verify signature same as backend Crypt.java */
    public static boolean verify(String data, String signature, PublicKey publicKey) {
        try {
            Signature publicSignature = Signature.getInstance("SHA256withRSA/PSS", "BC");
            publicSignature.initVerify(publicKey);
            publicSignature.update(data.getBytes());
            return publicSignature.verify(Base64.decode(signature));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Convert bytes to base64 */
    public static byte[] encodeBytes(byte[] s) {
        return Base64.encode(s);
    }

    /** Convert public key to PEM for inspection (optional) */
    public static String convertPublicToPem(byte[] keyBytes) {
        return convertToPem("RSA PUBLIC KEY", keyBytes);
    }

    /** Convert private key to PEM for inspection (optional) */
    public static String convertPrivateToPem(byte[] keyBytes) {
        return convertToPem("RSA PRIVATE KEY", keyBytes);
    }

    private static String convertToPem(String pemType, byte[] keyBytes) {
        PemObject pemObject = new PemObject(pemType, keyBytes);
        StringWriter stringWriter = new StringWriter();
        try (PemWriter pemWriter = new PemWriter(stringWriter)) {
            pemWriter.writeObject(pemObject);
        } catch (Exception ignored) {}
        return stringWriter.toString();
    }

    private static SecureRandom generateSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG", "BC");
        } catch (Exception e) {
            return new SecureRandom();
        }
    }
}
