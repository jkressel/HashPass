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
                if (!name.getText().toString().isEmpty() && !user.getText().toString().isEmpty() && length != -1 && chars != null && salt != null && !update) {
                    SecretKeyFunctions skf = new SecretKeyFunctions();
                    try {
                        if (skf.secretKeyExists()) {
                            CryptoFunctions cf = new CryptoFunctions();
                            try {
                                SecretKey sk = skf.getKey();
                                try {
                                    byte[] saltCipher = cf.encrypt(sk, salt.getBytes(StandardCharsets.UTF_8));
                                    byte[] saltIV = cf.getIV();
                                    byte[] userCipher = cf.encrypt(sk, user.getText().toString().getBytes(StandardCharsets.UTF_8));
                                    byte[] userIV = cf.getIV();
                                    byte[] notesCipher = cf.encrypt(sk, notes.getText().toString().getBytes(StandardCharsets.UTF_8));
                                    byte[] notesIV = cf.getIV();

                                    PasswordRecord record = new PasswordRecord(saltCipher,userCipher,notesCipher,name.getText().toString(),length, chars, saltIV, userIV, notesIV);
                                    mRecordViewModel.insert(record);
                                    onBackPressed();

                                } catch (NoSuchPaddingException e) {
                                    e.printStackTrace();
                                } catch (InvalidKeyException e) {
                                    e.printStackTrace();
                                } catch (BadPaddingException e) {
                                    e.printStackTrace();
                                } catch (IllegalBlockSizeException e) {
                                    e.printStackTrace();
                                }
                            } catch (UnrecoverableEntryException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if (!name.getText().toString().isEmpty() && !user.getText().toString().isEmpty() && length != -1 && chars != null && salt != null && update){
                    SecretKeyFunctions skf = new SecretKeyFunctions();
                    try {
                        if (skf.secretKeyExists()) {
                            CryptoFunctions cf = new CryptoFunctions();
                            try {
                                SecretKey sk = skf.getKey();
                                try {
                                    byte[] saltCipher = cf.encrypt(sk, salt.getBytes(StandardCharsets.UTF_8));
                                    byte[] saltIV = cf.getIV();
                                    byte[] userCipher = cf.encrypt(sk, user.getText().toString().getBytes(StandardCharsets.UTF_8));
                                    byte[] userIV = cf.getIV();
                                    byte[] notesCipher = cf.encrypt(sk, notes.getText().toString().getBytes(StandardCharsets.UTF_8));
                                    byte[] notesIV = cf.getIV();

                                    PasswordRecord record = new PasswordRecord(id, saltCipher,userCipher,notesCipher,name.getText().toString(),length, chars, saltIV, userIV, notesIV);
                                    mRecordViewModel.updateItem(record);
                                    onBackPressed();

                                } catch (NoSuchPaddingException e) {
                                    e.printStackTrace();
                                } catch (InvalidKeyException e) {
                                    e.printStackTrace();
                                } catch (BadPaddingException e) {
                                    e.printStackTrace();
                                } catch (IllegalBlockSizeException e) {
                                    e.printStackTrace();
                                }
                            } catch (UnrecoverableEntryException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

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

}
