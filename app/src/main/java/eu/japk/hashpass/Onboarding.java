package eu.japk.hashpass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Onboarding extends AppCompatActivity {

    int state = 1;
    TextView title;
    TextView content;
    Button continueBtn;
    int RESULT_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Toolbar toolbar = findViewById(R.id.toolbarOnboarding);
        setSupportActionBar(toolbar);

        continueBtn = findViewById(R.id.OnBtnContinue);
        Button negativeBtn = findViewById(R.id.OnBtnNegative);
        title = findViewById(R.id.onboardingTitle);
        content = findViewById(R.id.onboardingDesc);

        SecretKeyFunctions skf = new SecretKeyFunctions();
        try {
            if(!skf.secretKeyExists()){
                skf.generateKey();
            }
        } catch (Exception e) {
            Toast.makeText(Onboarding.this, "An unexpected error occurred when setting up the encryption key for the app", Toast.LENGTH_LONG).show();
        }

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(state){
                    case 1:
                        animateTextChange(getString(R.string.onboard_title_2), title);
                        animateTextChange(getString(R.string.onboard_desc_2), content);
                        state = 2;
                        break;
                    case 2:
                        Intent i = new Intent(Onboarding.this, EnterPhrase.class);
                        i.putExtra("save", true);
                        startActivityForResult(i, RESULT_CODE);
                        break;
                    case 3:
                        startActivity(new Intent("android.settings.INPUT_METHOD_SETTINGS"));
                        animateTextChange(getString(R.string.onboard_title_4), title);
                        animateTextChange(getString(R.string.onboard_desc_4), content);
                        continueBtn.setText("Finish");
                        state = 4;
                        break;
                    case 4:
                        setResult(RESULT_OK);
                        finish();
                        break;
                }
            }
        });

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });




    }

    private void animateTextChange(final String text, final TextView view){
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                view.setText(text);
            }
        });

        view.startAnimation(anim);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                animateTextChange(getString(R.string.onboard_title_3), title);
                animateTextChange(getString(R.string.onboard_desc_3), content);
                continueBtn.setText("Enable");
                state = 3;
            }
            else{
                Toast.makeText(Onboarding.this, "You must set a phrase!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
