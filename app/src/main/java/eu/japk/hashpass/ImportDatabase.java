package eu.japk.hashpass;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    BackupHelperFunctions backupHelperFunctions;

    public ImportDatabase(SecretKey appKey, String user){
        this.appKey = appKey;
        this.user = user;
        salt = new Salt();
        cf = new CryptoFunctions();
        backupHelperFunctions = new BackupHelperFunctions(cf);
        skf = new SecretKeyFunctions();
        password = new ArrayList<>();


    }

    public List<PasswordRecord> importDatabase(Context context, Uri fileName) throws Exception {
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

        InputStream is;
        BufferedReader reader;
        PasswordRecord pwr;
            try{
                is = context.getContentResolver().openInputStream(fileName);
            }catch (FileNotFoundException e){
                throw new FileNotFoundException();
            }

            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            if(line != null) this.salt.setSalt(line);
            SecretKey sk = skf.generateDerivedKey(this.user, this.salt.getSalt().getBytes(), 256);
            line = reader.readLine();
            try {
                while (line != null) {
                    name = line;
                    length = Integer.parseInt(reader.readLine());
                    chars = reader.readLine();
                    salt = reader.readLine();
                    encryptedSalt = backupHelperFunctions.encrypt(appKey, backupHelperFunctions.decrypt(backupHelperFunctions.decodeB64(salt), backupHelperFunctions.decodeB64(reader.readLine()), sk));
                    saltIV = cf.getIV();
                    line = reader.readLine();
                    encryptedUser = backupHelperFunctions.encrypt(appKey, backupHelperFunctions.decrypt(backupHelperFunctions.decodeB64(line), backupHelperFunctions.decodeB64(reader.readLine()), sk));
                    userIV = cf.getIV();
                    line = reader.readLine();
                    encryptedNotes = backupHelperFunctions.encrypt(appKey, backupHelperFunctions.decrypt(backupHelperFunctions.decodeB64(line), backupHelperFunctions.decodeB64(reader.readLine()), sk));
                    notesIV = cf.getIV();
                    line = reader.readLine();
                    pwr = new PasswordRecord(encryptedSalt, encryptedUser, encryptedNotes, name, length, chars, saltIV, userIV, notesIV);
                    password.add(pwr);


                }
            }catch (Exception e){
                throw new Exception();
            }
            return password;

    }


}
