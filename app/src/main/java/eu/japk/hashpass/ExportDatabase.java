package eu.japk.hashpass;

import android.content.Context;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import eu.japk.hashpass.db.PasswordRecord;

public class ExportDatabase {
    private Salt salt;
    private SecretKey sk;
    private CryptoFunctions cf;
    private SecretKey appkey;
    private BackupHelperFunctions backupHelperFunctions;

    public ExportDatabase(Salt salt, SecretKey sk, SecretKey appKey){
        this.salt = salt;
        this.sk = sk;
        this.appkey = appKey;
        cf = new CryptoFunctions();
        backupHelperFunctions = new BackupHelperFunctions(cf);
    }

    public void export(Context context, String sFileName, List<PasswordRecord> records) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
            StringBuilder sb = new StringBuilder();
            sb.append(salt.getSalt());
            sb.append("\n");
            File file = new File(getFile(context), sFileName);
            FileWriter writer = new FileWriter(file);
            for(PasswordRecord pr: records){
                sb.append(pr.name);
                sb.append("\n");
                sb.append(pr.length);
                sb.append("\n");
                sb.append(pr.charsAllowed);
                sb.append("\n");
                sb.append(backupHelperFunctions.encodeB64(backupHelperFunctions.encrypt(sk, backupHelperFunctions.decrypt(pr.salt, pr.saltIV, appkey))));
                sb.append("\n");
                sb.append(backupHelperFunctions.encodeB64(cf.getIV()));
                sb.append("\n");
                sb.append(backupHelperFunctions.encodeB64(backupHelperFunctions.encrypt(sk, backupHelperFunctions.decrypt(pr.user, pr.userIV, appkey))));
                sb.append("\n");
                sb.append(backupHelperFunctions.encodeB64(cf.getIV()));
                sb.append("\n");
                sb.append((backupHelperFunctions.encodeB64(backupHelperFunctions.encrypt(sk, backupHelperFunctions.decrypt(pr.notes, pr.notesIV, appkey)))));
                sb.append("\n");
                sb.append(backupHelperFunctions.encodeB64(cf.getIV()));
                sb.append("\n");
            }
            writer.write(sb.toString());
            writer.flush();
            writer.close();
    }

    private File getFile(Context context){
        File file = new File(context.getExternalFilesDir(null), "PassHash_export/");
        file.mkdirs();
        return file;
    }
}
