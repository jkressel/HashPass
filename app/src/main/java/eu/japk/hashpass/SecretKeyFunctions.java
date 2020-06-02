package eu.japk.hashpass;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SecretKeyFunctions {

    private String alias = "SecretEncryptionKey";
    private final String PROVIDER_STRING = "AndroidKeyStore";
    private final String DERIVED_PROVIDER = "PBEWithHmacSHA256AndAES_128";
    private final int ITERATION_COUNT = 10;


    public SecretKey generateKey() throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER_STRING);
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();
        keyGenerator.init(keyGenParameterSpec);
        return keyGenerator.generateKey();

    }

    public SecretKey generateDerivedKey(String password, byte[] salt, int length) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, length);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DERIVED_PROVIDER);

        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        return secretKey;

    }

    public SecretKey getKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance(PROVIDER_STRING);
        keyStore.load(null);

        final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                .getEntry(alias, null);

        return secretKeyEntry.getSecretKey();
    }

    public boolean secretKeyExists() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance(PROVIDER_STRING);
        ks.load(null);
        return ks.containsAlias(alias);
    }


}
