package eu.japk.hashpass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import javax.crypto.SecretKey;
import eu.japk.hashpass.db.AppDatabase;
import eu.japk.hashpass.db.PasswordRecord;
import eu.japk.hashpass.db.PasswordRecordDAO;
import eu.japk.hashpass.db.RecordViewModel;

public class ImportExport extends AppCompatActivity {

    TextView working;
    ProgressBar pb;
    EditText pw;
    String pass = null;
    SecretKey appK = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        Toolbar toolbar = findViewById(R.id.toolbarImportExport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        working = findViewById(R.id.workingTV);
        pb = findViewById(R.id.indeterminateBar);
        pw = findViewById(R.id.backupPW);

        AppDatabase db = AppDatabase.getDatabase(this.getApplication());
        final PasswordRecordDAO dao = db.recordDao();

        final SecretKeyFunctions skf = new SecretKeyFunctions();

        try {
            appK = skf.getKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        }
        final Salt salt = new Salt();
        salt.generateSalt();

        Button exportBtn = findViewById(R.id.exportBtn);
        final SecretKey finalAppK = appK;
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pw.getText().toString().isEmpty()) {
                    SecretKey sk = null;
                    try {
                        sk = skf.generateDerivedKey(pw.getText().toString(), salt.getSalt().getBytes(), 256);
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        Toast.makeText(ImportExport.this, "Sorry! An error occurred when exporting.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    new getAllAsyncTask(dao, sk, salt, finalAppK, ImportExport.this, working, pb).execute();
                }
                else{
                    Toast.makeText(ImportExport.this, "You must enter a password to encrypt the database", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button importBtn = findViewById(R.id.importBtn);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pw.getText().toString().isEmpty()) {
                    pass = pw.getText().toString();
                        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                        chooseFile.setType("text/plain");
                        startActivityForResult(
                                Intent.createChooser(chooseFile, "Choose a file"),
                                12
                        );
                }else{
                    Toast.makeText(ImportExport.this, "You must enter a password to decrypt the database", Toast.LENGTH_LONG).show();
                }
            }
        });


        new BubbleShowCaseSequence()
                .addShowCase(buildBubble(pw, "Enter password", "If your're exporting your database, then think of a new password, if you're importing then use the password you set when exporting. This password is used to encrypt and decrypt your exported database, to keep it secure", "ExpPw")) //First BubbleShowCase to show
                .addShowCase(buildBubble(exportBtn, "Export", "Export your database to an encrypted text file", "ExpExp"))
                .addShowCase(buildBubble(importBtn, "Import", "Import a text file you previously generated by exporting your database, this adds those passwords to the database", "ExpImp"))
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static class getAllAsyncTask extends android.os.AsyncTask<Void, Void, List<PasswordRecord>> {

        private PasswordRecordDAO mAsyncTaskDao;
        List<PasswordRecord> pwList;
        SecretKey secretKey;
        SecretKey appkey;
        Salt salt;
        private Context context;
        private TextView working;
        private ProgressBar pb;

        getAllAsyncTask(PasswordRecordDAO dao, SecretKey secretKey, Salt salt, SecretKey ak, Context context, TextView tv, ProgressBar pb) {
            mAsyncTaskDao = dao;
            this.secretKey = secretKey;
            this.salt = salt;
            appkey = ak;
            this.context = context;
            this.pb = pb;
            this.working = tv;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pb.setVisibility(View.VISIBLE);
            working.setVisibility(View.VISIBLE);
            working.setText(R.string.working);
        }

        @Override
        protected List<PasswordRecord> doInBackground(Void... voids) {
            List<PasswordRecord> list = mAsyncTaskDao.getAllUnordered();
            ExportDatabase ed = new ExportDatabase(salt, secretKey, appkey);
            try {
                ed.export(context, "hashpass_db_export.txt", list);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(List<PasswordRecord> passwordRecords) {
            super.onPostExecute(passwordRecords);


            working.setText("Successfully exported to " + context.getExternalFilesDir(null) + "/hashpass_export/hashpass_db_export.txt"
            );
            working.setTextSize((float)14.5);
            pb.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == Activity.RESULT_OK){
            Uri path = data.getData();
            RecordViewModel mRecordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
            if(pass != null)
                new importFile(mRecordViewModel, pass, appK, this, working, pb, path).execute();


        }
    }

    private static class importFile extends android.os.AsyncTask<Void, Void, List<PasswordRecord>> {

        private RecordViewModel rvm;
        List<PasswordRecord> pwList;
        SecretKey appkey;
        Context context;
        String userPass;
        TextView working;
        ProgressBar pb;
        Uri path;

        importFile(RecordViewModel recordViewModel, String userPass, SecretKey ak, Context context, TextView tv, ProgressBar pb, Uri filePath) {
            rvm = recordViewModel;
            appkey = ak;
            this.context = context;
            this.userPass = userPass;
            this.pb = pb;
            this.working = tv;
            path = filePath;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pb.setVisibility(View.VISIBLE);
            working.setVisibility(View.VISIBLE);
            working.setText(R.string.working);
        }

        @Override
        protected List<PasswordRecord> doInBackground(Void... voids) {
            ImportDatabase id = new ImportDatabase(appkey, userPass);
            try {
                return id.importDatabase(context, path);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<PasswordRecord> passwordRecords) {
            super.onPostExecute(passwordRecords);

            if(passwordRecords == null){
                working.setText("An error occurred! Please check that you have used the correct password and have selected the correct file.");

            }else{
                rvm.insertList(passwordRecords);
                working.setText("Successfully Imported!");
            }


            working.setTextSize((float)14.5);
            pb.setVisibility(View.GONE);



        }
    }

    private BubbleShowCaseBuilder buildBubble(View view, String title, String desc, String once){
        return new BubbleShowCaseBuilder(this) //Activity instance
                .title(title) //Any title for the bubble view
                .description(desc)
                .highlightMode(BubbleShowCase.HighlightMode.VIEW_SURFACE)
                .backgroundColorResourceId(R.color.colorPrimary)
                .showOnce(once)
                .targetView(view);
    }

}
