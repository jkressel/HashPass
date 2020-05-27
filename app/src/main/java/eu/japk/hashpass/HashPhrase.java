package eu.japk.hashpass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPhrase {

    public byte[] hashPhrase(byte[] phrase, Salt salt){
        MessageDigest md = getMDInstance();
        if(md != null){
            byte[] combined = (new String(phrase) + salt.getSalt()).getBytes();
            md.update(combined);
            byte[] digest = md.digest();
            return digest;
        }
        return null;
    }

    private MessageDigest getMDInstance(){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            return md;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
