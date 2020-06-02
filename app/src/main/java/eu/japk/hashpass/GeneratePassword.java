package eu.japk.hashpass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence;
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


        new BubbleShowCaseSequence()
                .addShowCase(buildBubble(sb, getString(R.string.length_bubble_title), getString(R.string.length_bubble_desc), "GenLen"))
                .addShowCase(buildBubble(allowedChars, getString(R.string.chars_bubble_title), getString(R.string.chars_bubble_desc), "GenChars"))
                .addShowCase(buildBubble(generate, getString(R.string.gen_bubble_title), getString(R.string.gen_bubble_desc), "GenBtn"))
                .addShowCase(buildBubble(fab, getString(R.string.done_bubble_title), getString(R.string.done_bubble_desc), "GenDone"))
                .show();

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        return true;
    }

    private BubbleShowCaseBuilder buildBubble(View view, String title, String desc, String once){
        return new BubbleShowCaseBuilder(this)
                .title(title)
                .description(desc)
                .highlightMode(BubbleShowCase.HighlightMode.VIEW_SURFACE)
                .backgroundColorResourceId(R.color.colorPrimary)
                .showOnce(once)
                .targetView(view);
    }

}
