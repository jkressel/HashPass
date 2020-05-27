package eu.japk.hashpass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Guideline;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import eu.japk.hashpass.db.PasswordRecord;

public class EnterPhrase extends AppCompatActivity {

    float topLeftPos = (float)0.34;
    float topRightPos = (float)0.66;
    float bottomLeftPos = (float)0.34;
    float bottomRightPos = (float)0.66;
    ValueAnimator animation = null;
    ValueAnimator animation1 = null;
    ValueAnimator animation2 = null;
    ValueAnimator animation3 = null;
    Guideline glLeftTop;
    Guideline glRightTop;
    Guideline glLeftBottom;
    Guideline glRightBottom;
    boolean save = false;
    SharedPreferences sharedPref;
    KeyPhrase kp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phrase);

        Toolbar toolbar = findViewById(R.id.toolbarEnterPhrase);
        setSupportActionBar(toolbar);


        final EditText word1 = findViewById(R.id.word1);
        final EditText word2 = findViewById(R.id.word2);
        final EditText word3 = findViewById(R.id.word3);
        final EditText word4 = findViewById(R.id.word4);
        final EditText word5 = findViewById(R.id.word5);
        final EditText word6 = findViewById(R.id.word6);

         glLeftTop = findViewById(R.id.top_left_guide);
         glRightTop= findViewById(R.id.top_right_guide);
         glLeftBottom= findViewById(R.id.bottom_left_guide);
         glRightBottom = findViewById(R.id.bottom_right_guide);

        sharedPref = EnterPhrase.this.getPreferences(Context.MODE_PRIVATE);


         //check if the phrase should be stored

        Intent i = getIntent();
        if(i.hasExtra("save")){
            save = true;
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    kp = new KeyPhrase();
                    if (kp.newPhrase(word1.getText().toString(), word2.getText().toString(),
                            word3.getText().toString(), word4.getText().toString(),
                            word5.getText().toString(), word6.getText().toString())) {

                        if (save) {
                            SecretKeyFunctions secretKeyFunctions = new SecretKeyFunctions();
                            HashPhrase hp = new HashPhrase();
                            try {
                                if (secretKeyFunctions.secretKeyExists()) {
                                    Salt salt = new Salt();
                                    salt.generateSalt();
                                    String saltString = salt.getSalt();
                                    byte[] hash = hp.hashPhrase(kp.getPhrase().getBytes(), salt);
                                    if (hash != null) {
                                        storeData("Salt1", saltString);
                                    }

                                    salt.generateSalt();
                                    saltString = salt.getSalt();
                                    hash = hp.hashPhrase(hash, salt);
                                    if (hash != null) {
                                        storeData("Salt2", saltString);
                                    }

                                    salt.generateSalt();
                                    saltString = salt.getSalt();
                                    hash = hp.hashPhrase(hash, salt);
                                    if (hash != null) {
                                        storeData("Salt3", saltString);
                                    }

                                    salt.generateSalt();
                                    saltString = salt.getSalt();
                                    hash = hp.hashPhrase(hash, salt);
                                    if (hash != null) {
                                        storeData("Salt4", saltString);
                                    }

                                    salt.generateSalt();
                                    saltString = salt.getSalt();
                                    hash = hp.hashPhrase(hash, salt);
                                    if (hash != null) {
                                        storeData("Salt5", saltString);
                                    }

                                    CryptoFunctions cryptoFunctions = new CryptoFunctions();
                                    byte[] encrypted = cryptoFunctions.encrypt(secretKeyFunctions.getKey(), hash);
                                    byte[] hashIV = cryptoFunctions.getIV();

                                    storeData("hash", Base64.encodeToString(encrypted, Base64.DEFAULT));
                                    storeData("hashIV", Base64.encodeToString(hashIV, Base64.DEFAULT));

                                    askSave();

                                }
                            } catch (KeyStoreException e) {
                                e.printStackTrace();
                            } catch (CertificateException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (UnrecoverableEntryException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String salt1 = getData("Salt1");
                            String salt2 = getData("Salt2");
                            String salt3 = getData("Salt3");
                            String salt4 = getData("Salt4");
                            String salt5 = getData("Salt5");

                            if (salt1 == null || salt2 == null || salt3 == null || salt4 == null || salt5 == null) {
                                throw new Exception("No salt stored");
                            }

                            HashPhrase hp = new HashPhrase();

                            Salt salt = new Salt();
                            salt.setSalt(salt1);
                            byte[] hash = hp.hashPhrase(kp.getPhrase().getBytes(), salt);
                            salt.setSalt(salt2);
                            hash = hp.hashPhrase(hash, salt);
                            salt.setSalt(salt3);
                            hash = hp.hashPhrase(hash, salt);
                            salt.setSalt(salt4);
                            hash = hp.hashPhrase(hash, salt);
                            salt.setSalt(salt5);
                            hash = hp.hashPhrase(hash, salt);

                            SecretKeyFunctions secretKeyFunctions = new SecretKeyFunctions();
                            try {
                                if (secretKeyFunctions.secretKeyExists()) {
                                    CryptoFunctions cryptoFunctions = new CryptoFunctions();
                                    byte[] decrypted = cryptoFunctions.decrypt(secretKeyFunctions.getKey(), Base64.decode(getData("hash"), Base64.DEFAULT), Base64.decode(getData("hashIV"), Base64.DEFAULT));
                                    if (Arrays.equals(decrypted, hash)) {
                                        returnResult();
                                    } else {
                                        askProceed();
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
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidAlgorithmParameterException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (UnrecoverableEntryException e) {
                                e.printStackTrace();
                            }


                        }


                    } else {
                        Toast.makeText(EnterPhrase.this, "Please check that you have entered your phrase correctly", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(EnterPhrase.this, "An error occurred", Toast.LENGTH_LONG).show();
                }
            }
        });

        word1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    word2.requestFocus();
                }
                return false;
            }
        });

        word2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    word3.requestFocus();
                }
                return false;
            }
        });

        word3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    word4.requestFocus();
                    animateTop((float)0.34, (float)0.66);
                    updateTopPos((float)0.34, (float)0.66);
                }
                return false;
            }
        });

        word4.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    word5.requestFocus();
                }
                return false;
            }
        });

        word5.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    word6.requestFocus();
                }
                return false;
            }
        });

        word6.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    word6.clearFocus();
                    animateBottom((float)0.34, (float)0.66);
                    updateBottomPos((float)0.34, (float)0.66);
                }
                return false;
            }
        });



        View.OnFocusChangeListener focus = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(v == word1){
                        animateBottom((float)0.34, (float)0.66);
                        updateBottomPos((float)0.34, (float)0.66);
                        animateTop((float)0.5, (float)0.75);
                        updateTopPos((float)0.5, (float)0.75);
                    }else if (v == word2){
                        animateBottom((float)0.34, (float)0.66);
                        updateBottomPos((float)0.34, (float)0.66);
                        animateTop((float)0.25, (float)0.75);
                        updateTopPos((float)0.25, (float)0.75);
                    } else if (v == word3){
                        animateBottom((float)0.34, (float)0.66);
                        updateBottomPos((float)0.34, (float)0.66);
                        animateTop((float)0.25, (float)0.5);
                        updateTopPos((float)0.25, (float)0.5);
                    }
                    else if (v == word4){
                        animateTop((float)0.34, (float)0.66);
                        updateTopPos((float)0.34, (float)0.66);
                        animateBottom((float)0.5, (float)0.75);
                        updateBottomPos((float)0.5, (float)0.75);
                    }
                    else if (v == word5){
                        animateTop((float)0.34, (float)0.66);
                        updateTopPos((float)0.34, (float)0.66);
                        animateBottom((float)0.25, (float)0.75);
                        updateBottomPos((float)0.25, (float)0.75);
                    }
                    else if (v == word6){
                        animateTop((float)0.34, (float)0.66);
                        updateTopPos((float)0.34, (float)0.66);
                        animateBottom((float)0.25, (float)0.5);
                        updateBottomPos((float)0.25, (float)0.5);
                    }
                }

            }
        };
        word1.setOnFocusChangeListener(focus);
        word2.setOnFocusChangeListener(focus);
        word3.setOnFocusChangeListener(focus);
        word4.setOnFocusChangeListener(focus);
        word5.setOnFocusChangeListener(focus);
        word6.setOnFocusChangeListener(focus);




    }

    private ValueAnimator animateGuideline(float start, float end, final Guideline v){
        ValueAnimator animation = ValueAnimator.ofFloat(start, end);
        animation.setDuration(700);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                // You can use the animated value in a property that uses the
                // same type as the animation. In this case, you can use the
                // float value in the translationX property.
                float animatedValue = (float)updatedAnimation.getAnimatedValue();
                v.setGuidelinePercent(animatedValue);
            }
        });
        return animation;
    }

    private void animateTop(float one, float two){
        animation = animateGuideline(topLeftPos, one, glLeftTop);
        animation.start();
        animation1 = animateGuideline(topRightPos, two, glRightTop);
        animation1.start();
    }

    private void animateBottom(float one, float two){
        animation2 = animateGuideline(bottomLeftPos, one, glLeftBottom);
        animation2.start();
        animation3 = animateGuideline(bottomRightPos, two, glRightBottom);
        animation3.start();
    }

    private void updateTopPos(float left, float right){
        topLeftPos = left;
        topRightPos = right;
    }

    private void updateBottomPos(float left, float right){
        bottomLeftPos = left;
        bottomRightPos = right;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void storeData(String key, String data){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, data);
        editor.apply();
    }

    private String getData(String key){
        SharedPreferences sharedPref = EnterPhrase.this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key,null);
    }

    private void askSave(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.remember_message)
                .setTitle(R.string.remember_phrase_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                returnResult();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void askProceed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Phrase Incorrect")
                .setTitle("Would you like to proceed anyway?");
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                returnResult();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void returnResult(){
        Intent data = new Intent();
        data.putExtra("phrase", kp.getPhrase());
        setResult(RESULT_OK, data);
        finish();
    }

}
