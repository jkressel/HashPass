package eu.japk.hashpass;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class CryptoFunctions {

    private final String CIPHER_INSTANCE = "AES/GCM/NoPadding";
    private final int KEY_LENGTH = 128;

    private byte[] IV;

    public byte[] encrypt(SecretKey secretKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        IV = cipher.getIV();
        return cipher.doFinal(data);
    }

    public byte[] decrypt(SecretKey secretKey, byte[] ciphertext, byte[] eIV) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(KEY_LENGTH, eIV);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
        return cipher.doFinal(ciphertext);
    }

    public byte[] getIV(){
        return IV;
    }
}
