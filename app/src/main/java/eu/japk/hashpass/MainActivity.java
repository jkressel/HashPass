package eu.japk.hashpass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.SectionIndexer;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import eu.japk.hashpass.db.AppDatabase;
import eu.japk.hashpass.db.PasswordRecord;
import eu.japk.hashpass.db.PasswordRecordDAO;
import eu.japk.hashpass.db.RecordViewModel;

public class MainActivity extends AppCompatActivity {

    private RecordViewModel mRecordViewModel;
    RecordListAdapter adapter;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle t;
    Toolbar toolbar;

    RecyclerView recyclerView;
    int request_Code = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        recyclerView = findViewById(R.id.rec_view);
        adapter = new RecordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabMain);
        fab.setSize(FloatingActionButton.SIZE_AUTO);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNew.class);
                startActivity(intent);
            }
        });

        mRecordViewModel.getAllRecords().observe(this, new Observer<List<PasswordRecord>>() {
            @Override
            public void onChanged(@Nullable final List<PasswordRecord> records) {
                // Update the cached copy of the words in the adapter.
                adapter.setRecords(records);
            }
        });

        //set up the navigation drawer
        initDrawer();

        SecretKeyFunctions skf = new SecretKeyFunctions();
        try {
            if(!skf.secretKeyExists()){
                skf.generateKey();
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            Salt salt = new Salt();
            salt.generateSalt();
            SecretKey sk = skf.generateDerivedKey("Hello", ("boo").getBytes(), 256);
            Toast.makeText(MainActivity.this, new String(sk.getEncoded()), Toast.LENGTH_LONG).show();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        AppDatabase db = AppDatabase.getDatabase(this.getApplication());
        PasswordRecordDAO dao = db.recordDao();
        SecretKey appK = null;
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
        Salt salt = new Salt();
        salt.generateSalt();
        SecretKey sk = null;
        try {
            sk = skf.generateDerivedKey("Hello.txt", salt.getSalt().getBytes(), 256);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        //new getAllAsyncTask(dao, sk, salt, appK, this).execute();
       new importFile(mRecordViewModel, "Hello.txt", appK, this).execute();

    }


    private void initDrawer() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimary));

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        t = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_app_bar_open_drawer_description, R.string.nav_app_bar_navigate_up_description);

        drawerLayout.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nv = (NavigationView)findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.new_phrase:
                        Intent i = new Intent(MainActivity.this, EnterPhrase.class);
                        i.putExtra("save", true);
                        startActivityForResult(i, request_Code);

                    default:
                        return true;
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return t.onOptionsItemSelected(item);
    }


    private static class getAllAsyncTask extends android.os.AsyncTask<Void, Void, List<PasswordRecord>> {

        private PasswordRecordDAO mAsyncTaskDao;
        List<PasswordRecord> pwList;
        SecretKey secretKey;
        SecretKey appkey;
        Salt salt;
        Context context;

        getAllAsyncTask(PasswordRecordDAO dao, SecretKey secretKey, Salt salt, SecretKey ak, Context context) {
            mAsyncTaskDao = dao;
            this.secretKey = secretKey;
            this.salt = salt;
            appkey = ak;
            this.context = context;
        }

        @Override
        protected List<PasswordRecord> doInBackground(Void... voids) {
            return mAsyncTaskDao.getAllUnordered();
        }

        @Override
        protected void onPostExecute(List<PasswordRecord> passwordRecords) {
            super.onPostExecute(passwordRecords);

            ExportDatabase ed = new ExportDatabase(salt, secretKey, appkey);
            try {
                ed.export(context, "hashpass_db_export.txt", passwordRecords);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }

        }
    }


    private static class importFile extends android.os.AsyncTask<Void, Void, List<PasswordRecord>> {

        private RecordViewModel rvm;
        List<PasswordRecord> pwList;
        SecretKey appkey;
        Context context;
        String userPass;

        importFile(RecordViewModel recordViewModel, String userPass, SecretKey ak, Context context) {
            rvm = recordViewModel;
            appkey = ak;
            this.context = context;
            this.userPass = userPass;
        }

        @Override
        protected List<PasswordRecord> doInBackground(Void... voids) {
            ImportDatabase id = new ImportDatabase(appkey, userPass);
            try {
                return id.importDatabase(context, context.getExternalFilesDir(null)+"/hashpass_export/hashpass_db_export.txt");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<PasswordRecord> passwordRecords) {
            super.onPostExecute(passwordRecords);

            if(passwordRecords == null) return;

            rvm.insertList(passwordRecords);



        }
    }



}
