package eu.japk.hashpass;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordFunctions {

    private String keyPhrase = null;
    private Salt salt = null;
    private String wholePhrase = "";
    private NonSpecialCharacters nsc;
    private CustomCharacters cc;

    public PasswordFunctions(String kp, Salt salt){
        this.keyPhrase = kp;
        this.salt = salt;
        constructPhrase();
        nsc = new NonSpecialCharacters();
    }

    private void constructPhrase(){
        if(keyPhrase != null && salt.getSalt() != null)
            wholePhrase += keyPhrase + salt.getSalt();
    }

    public String getPassword(boolean specialCharacters, int length){
        MessageDigest md = getMDInstance();
        if(md != null){
            try {
                md.update(wholePhrase.getBytes());
                byte[] digest = md.digest();
                if(specialCharacters){
                    return chopToSize(length, pwWithSpecial(digest));
                }else{
                    return chopToSize(length, pwWithoutSpecial(digest));
                }
            }catch (Exception e){
                Log.e("Hashing", e.toString());
            }
        }
        return null;
    }

    public String getCustomPwd(String custom, int length){
        cc = new CustomCharacters(custom);
        MessageDigest md = getMDInstance();
        if(md != null) {
            try {
                md.update(wholePhrase.getBytes());
                byte[] digest = md.digest();
                return chopToSize(length, pwWithCustom(cc, digest));
            } catch (Exception e) {
                Log.e("Hashing", e.toString());
            }

        }
        return null;
    }

    private String pwWithCustom(CustomCharacters cc, byte[] digest){
        char[] str = new char[digest.length];
        for (int i = 0; i < digest.length; i++){
            int pos = ((Math.abs(digest[i] % cc.getNumberOfChars())));
            str[i] = (char)cc.getCharacter(pos);
        }
        return new String(str);
    }

    private String pwWithSpecial(byte[] digest){
        char[] str = new char[digest.length];
        for (int i = 0; i<digest.length; i++){
            str[i] = (char)((Math.abs(digest[i] % 93) + 33));
        }
        return new String(str);
    }

    private String pwWithoutSpecial(byte[] digest){
        char[] str = new char[digest.length];
        for (int i = 0; i<digest.length; i++){
            int pos = ((Math.abs(digest[i] % nsc.getNumberOfChars())));
            str[i] = (char)nsc.getCharacter(pos);
        }
        return new String(str);
    }

    private String chopToSize(int length, String pass){
        if(length >= pass.length()){
            return pass;
        }
        return pass.substring(0,length);
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
