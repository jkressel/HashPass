package eu.japk.hashpass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import eu.japk.hashpass.db.PasswordRecord;
import eu.japk.hashpass.db.RecordViewModel;

public class AddNew extends AppCompatActivity {

    int request_Code = 1;
    private String chars = null;
    private String password = null;
    private int length = -1;
    private String salt = null;
    EditText pass;
    private int id;
    private RecordViewModel mRecordViewModel;
    boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        final EditText name = findViewById(R.id.pwName);
        final EditText user = findViewById(R.id.pwUser);
        final EditText notes = findViewById(R.id.pwNotes);
        pass = findViewById(R.id.pw);

        Intent i1 = getIntent();
        if(i1.hasExtra("update")){
            update = true;
            name.setText(i1.getStringExtra("name"));
            length = i1.getIntExtra("length", 30);
            id = i1.getIntExtra("id",0);
            notes.setText(i1.getStringExtra("notes"));
            user.setText(i1.getStringExtra("user"));
            pass.setText(i1.getStringExtra("password"));
            salt = i1.getStringExtra("salt");
            chars = i1.getStringExtra("chars");
        }

        Toolbar toolbar = findViewById(R.id.toolbarAddNew);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        Button generate = findViewById(R.id.genBtn);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddNew.this, GeneratePassword.class);
                startActivityForResult(i, request_Code);
            }
        });



        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty() && !user.getText().toString().isEmpty() && length != -1 && chars != null && salt != null) {
                    encryptAndInsert(update, user.getText().toString(), notes.getText().toString(), name.getText().toString());
                }
            }
        });


        new BubbleShowCaseSequence()
                .addShowCase(buildBubble(name, "Enter Name/Label", "This is the name of your password, so that you remember what it's for.", "AddPWName")) //First BubbleShowCase to show
                .addShowCase(buildBubble(user, "Enter Username", "This is the username associated with this password", "AddPwUser"))
                .addShowCase(buildBubble(generate, "Generate Password", "You must generate a password, a combination of a secret stored by the app and your phrase.", "AddPwPw"))
                .addShowCase(buildBubble(notes, "Enter Notes", "This is not required, but may be useful if you wish to store any extra data", "AddPwNotes"))
                .addShowCase(buildBubble(fab, "Save", "Once you have finished, press to encrypt your data and store it in the database", "AddPwSave"))
                .show();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK) {
                chars = data.getStringExtra("chars");
                password = data.getStringExtra("password");
                length = data.getIntExtra("length", 30);
                salt = data.getStringExtra("salt");
                pass.setText(password);

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    private void encryptAndInsert(boolean update, String user, String notes, String name){
        SecretKeyFunctions skf = new SecretKeyFunctions();
        try {
            if (skf.secretKeyExists()) {
                CryptoFunctions cf = new CryptoFunctions();
                try {
                    SecretKey sk = skf.getKey();
                    try {
                        byte[] saltCipher = cf.encrypt(sk, salt.getBytes(StandardCharsets.UTF_8));
                        byte[] saltIV = cf.getIV();
                        byte[] userCipher = cf.encrypt(sk, user.getBytes(StandardCharsets.UTF_8));
                        byte[] userIV = cf.getIV();
                        byte[] notesCipher = cf.encrypt(sk, notes.getBytes(StandardCharsets.UTF_8));
                        byte[] notesIV = cf.getIV();


                        if(update){
                            PasswordRecord record = new PasswordRecord(id, saltCipher,userCipher,notesCipher,name,length, chars, saltIV, userIV, notesIV);
                            mRecordViewModel.updateItem(record);
                        }else {
                            PasswordRecord record = new PasswordRecord(saltCipher,userCipher,notesCipher,name,length, chars, saltIV, userIV, notesIV);
                            mRecordViewModel.insert(record);
                        }
                        onBackPressed();

                    } catch (Exception e) {
                        Toast.makeText(AddNew.this, "An unexpected error occurred.", Toast.LENGTH_LONG).show();
                    }
                } catch (UnrecoverableEntryException e) {
                    Toast.makeText(AddNew.this, "An unexpected error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
