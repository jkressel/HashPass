package eu.japk.hashpass;

import java.security.SecureRandom;

public class Salt {

    private String salt = null;
    private static final int SALT_LENGTH = 64;

    public void generateSalt() {
        SecureRandom sr = new SecureRandom();
        char[] saltArr = new char[SALT_LENGTH];
        for (int i = 0; i < SALT_LENGTH; i++){
            saltArr[i] = (char)(sr.nextInt(94) + 33);
        }
        salt = new String(saltArr);
    }

    public String getSalt(){
        return salt;
    }

    public boolean setSalt(String salt){
        if (salt.length() == 64){
            this.salt = salt;
            return true;
        }
        return false;
    }
}
