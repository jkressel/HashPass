package eu.japk.hashpass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GeneratePassword extends AppCompatActivity {

    int request_Code = 2;
    String phrase = null;
    String password = null;
    Salt salt = null;
    String chars = null;
    int length = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_password);

        Toolbar toolbar = findViewById(R.id.toolbarGenerate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = new Intent(this, EnterPhrase.class);
        startActivityForResult(i, request_Code);

        final EditText allowedChars = findViewById(R.id.allowedCharacters);
        final EditText finalPwd = findViewById(R.id.genOutput);
        final SeekBar sb = findViewById(R.id.seekLength);
        final TextView seekProgress = findViewById(R.id.labelCurrentValue);
        FloatingActionButton fab = findViewById(R.id.fabGen);

        Button generate = findViewById(R.id.generatePwd);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phrase != null){
                    salt = new Salt();
                    chars = allowedChars.getText().toString();
                    salt.generateSalt();
                    PasswordFunctions pwf = new PasswordFunctions(phrase, salt);
                    length = sb.getProgress();
                    password = pwf.getCustomPwd(chars, length);
                    finalPwd.setText(password);

                }
                else{
                    Toast.makeText(GeneratePassword.this, "Error! You did not enter your phrase!", Toast.LENGTH_LONG).show();
                }
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekProgress.setText(progress + " / 64");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(password != null && salt!= null && chars != null && length != -1){
                Intent data1 = new Intent();
                data1.putExtra("chars",chars);
                data1.putExtra("password", password);
                data1.putExtra("length", length);
                data1.putExtra("salt", salt.getSalt());
                setResult(RESULT_OK,data1);
                finish();
            }
        }
    });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK) {
                phrase = data.getStringExtra("phrase");

            }else{
                finish();
            }
        }
    }

}
