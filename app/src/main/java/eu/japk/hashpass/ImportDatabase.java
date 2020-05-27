package eu.japk.hashpass;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import eu.japk.hashpass.db.PasswordRecord;

public class ImportDatabase {

    private SecretKey appKey;
    private String user;
    private ArrayList<PasswordRecord> password;
    private Salt salt;
    private CryptoFunctions cf;
    SecretKeyFunctions skf;

    public ImportDatabase(SecretKey appKey, String user){
        this.appKey = appKey;
        this.user = user;
        salt = new Salt();
        cf = new CryptoFunctions();
        skf = new SecretKeyFunctions();
        password = new ArrayList<PasswordRecord>();


    }

    public List<PasswordRecord> importDatabase(Context context, String fileName) throws IOException, NullPointerException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        String name;
        int length;
        String chars;
        String salt;
        byte[] encryptedSalt;
        byte[] saltIV;
        byte[] encryptedUser;
        byte[] userIV;
        byte[] encryptedNotes;
        byte[] notesIV;

        FileInputStream is;
        BufferedReader reader;
        final File file = new File(fileName);
        PasswordRecord pwr;
        if (file.exists()) {
            Log.i("EXISTS","YAYYYYYYYYYYYYYYYYYYYYYYYYYYYY");
            is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            if(line != null) this.salt.setSalt(line);
            SecretKey sk = skf.generateDerivedKey(this.user, this.salt.getSalt().getBytes(), 256);
            line = reader.readLine();
            while(line != null){
                name = line;
                length = Integer.parseInt(reader.readLine());
                chars = reader.readLine();
                salt = reader.readLine();
                encryptedSalt = encrypt(appKey, decrypt(decodeB64(salt), decodeB64(reader.readLine()), sk));
                saltIV = cf.getIV();
                line = reader.readLine();
                encryptedUser = encrypt(appKey, decrypt(decodeB64(line), decodeB64(reader.readLine()),sk));
                userIV = cf.getIV();
                line = reader.readLine();
                encryptedNotes = encrypt(appKey, decrypt(decodeB64(line), decodeB64(reader.readLine()), sk));
                notesIV = cf.getIV();
                line = reader.readLine();
                pwr = new PasswordRecord(encryptedSalt, encryptedUser, encryptedNotes, name, length, chars, saltIV, userIV, notesIV);
                password.add(pwr);




            }
            return password;
        }
        return null;

    }

    private byte[] decodeB64(String data){
        return Base64.getDecoder().decode(data);
    }

    private byte[] decrypt(byte[] cipherText, byte[] IV, SecretKey sk) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return cf.decrypt(sk, cipherText, IV);
    }

    private byte[] encrypt(SecretKey sk, byte[] plain) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return cf.encrypt(sk, plain);
    }
}
