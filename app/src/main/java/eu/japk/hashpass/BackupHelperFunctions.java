package eu.japk.hashpass;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class BackupHelperFunctions {

    CryptoFunctions cf;

    public BackupHelperFunctions(CryptoFunctions cf){
        this.cf = cf;
    }

    public byte[] decodeB64(String data){
        return Base64.getDecoder().decode(data);
    }

    public String encodeB64(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }


    public byte[] decrypt(byte[] cipherText, byte[] IV, SecretKey sk) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return cf.decrypt(sk, cipherText, IV);
    }

    public byte[] encrypt(SecretKey sk, byte[] plain) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return cf.encrypt(sk, plain);
    }
}
