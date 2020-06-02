package eu.japk.hashpass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import eu.japk.hashpass.db.AppDatabase;
import eu.japk.hashpass.db.PasswordRecord;
import eu.japk.hashpass.db.PasswordRecordDAO;
import eu.japk.hashpass.db.RecordRepository;

public class ViewAndEditPassword extends AppCompatActivity {

    int request_Code = 3;
    private String phrase = null;
    private String user = null;
    private String salt = null;
    private int length;
    private String notes = null;
    private String name = null;
    private String allowedChars = null;
    private int uid;
    private String pw;
    TextView nameET;
    EditText userET;
    EditText pwET;
    EditText notesET;
    private boolean display = false;
    RecordRepository rr;
    private static InputDetails inputDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_and_edit_password);

        rr = new RecordRepository(getApplication());

        Toolbar toolbar = findViewById(R.id.toolbarView);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent i = getIntent();

        try{
            uid = i.getIntExtra("id", 0);
            user = i.getStringExtra("user");
            salt = i.getStringExtra("salt");
            notes = i.getStringExtra("notes");
            length = i.getIntExtra("length", 30);
            name = i.getStringExtra("name");
            allowedChars = i.getStringExtra("charsAllowed");

            nameET = findViewById(R.id.pwNameView);
            userET = findViewById(R.id.pwUserView);
            notesET = findViewById(R.id.pwNotesView);
            pwET = findViewById(R.id.pwView);


            Intent intent = new Intent(this, EnterPhrase.class);
            startActivityForResult(intent, request_Code);



        }catch(Exception e){
            Toast.makeText(this, "An error has occurred!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }


        ImageButton copyPass = findViewById(R.id.viewBtnCopyPwd);
        copyPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("password", pwET.getText().toString());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ViewAndEditPassword.this, "Password copied", Toast.LENGTH_LONG).show();
            }
        });

        ImageButton copyUser = findViewById(R.id.viewBtnCopyUser);
        copyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("username", userET.getText().toString());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ViewAndEditPassword.this, "Username copied", Toast.LENGTH_LONG).show();
            }
        });


        FloatingActionButton floatingActionButton = findViewById(R.id.fabKeyboard);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDetails = new InputDetails(user, pw);

                SharedPreferences sharedPref = ViewAndEditPassword.this.getPreferences(Context.MODE_PRIVATE);
                boolean firstKeyboard = sharedPref.getBoolean("firstKeyboard", true);
                if(firstKeyboard){
                    startActivity(new Intent("android.settings.INPUT_METHOD_SETTINGS"));
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("firstKeyboard", false);
                    editor.apply();
                }else {
                    InputMethodManager mgr =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (mgr != null) {
                        mgr.showInputMethodPicker();
                    }
                }
            }
        });

        buildBubble(floatingActionButton, "Secure Input", "Activate Secure Input to securely enter your username and password without the need for copy and paste", "ViewIP")
                .show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK && name != null && user != null && salt != null && notes != null && allowedChars !=null) {
                phrase = data.getStringExtra("phrase");

                Salt s = new Salt();
                s.setSalt(salt);
                PasswordFunctions pf = new PasswordFunctions(phrase, s);
                pw = pf.getCustomPwd(allowedChars, length);

                nameET.setText(name);
                userET.setText(user);
                pwET.setText(pw);
                notesET.setText(notes);
                pwET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            }
            else{
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.details_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) {
            askDelete();

        }else if(id == R.id.edit){
            Intent intent = new Intent(ViewAndEditPassword.this, AddNew.class);
            intent.putExtra("update", true);
            intent.putExtra("name", name);
            intent.putExtra("length", length);
            intent.putExtra("id", uid);
            intent.putExtra("notes", notes);
            intent.putExtra("user", user);
            intent.putExtra("password", pw);
            intent.putExtra("salt", salt);
            intent.putExtra("chars", allowedChars);
            startActivity(intent);
            finish();
        }else if(id == R.id.show){
            if(!display){
                pwET.setInputType(InputType.TYPE_CLASS_TEXT);
                item.setIcon(R.drawable.ic_visibility_off_white_24dp);
            }else{
                pwET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                item.setIcon(R.drawable.ic_visibility_white_24dp);
            }
            display = !display;
        }
        return super.onOptionsItemSelected(item);
    }

    private void askDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg)
                .setTitle(R.string.delete_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                PasswordRecord deletePw = new PasswordRecord(uid, new byte[0], new byte[0], new byte[0], null, 0, null, new byte[0], new byte[0], new byte[0]);
                rr.deleteItem(deletePw);
                finish();

            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    public static InputDetails getInputDetails(){
        return inputDetails;
    }

}
