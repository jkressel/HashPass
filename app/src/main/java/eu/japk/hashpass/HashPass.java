package eu.japk.hashpass;

import android.app.Application;

public class HashPass extends Application {
    private boolean phraseEntered = false;
    private String phrase = null;

    public void setPhraseEntered(boolean value) {
        phraseEntered = value;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getPhrase() {
        return phrase;
    }

    public boolean getPhraseEntered() {
        return phraseEntered;
    }
}
