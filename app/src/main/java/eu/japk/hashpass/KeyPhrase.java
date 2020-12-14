package eu.japk.hashpass;

import android.app.Application;

public class KeyPhrase {
    private String word1 = null;
    private String word2 = null;
    private String word3 = null;
    private String word4 = null;
    private String word5 = null;
    private String word6 = null;

    public boolean newPhrase (String w1, String w2, String w3, String w4, String w5, String w6)
    {
        if (isValidWord(w1) && isValidWord(w2) && isValidWord(w3) && isValidWord(w4) && isValidWord(w5) && isValidWord(w6))
        {
            word1 = w1;
            word2 = w2;
            word3 = w3;
            word4 = w4;
            word5 = w5;
            word6 = w6;
            return true;
        }
        return false;
    }

    private boolean isValidWord(String word) {
        return word != null && !word.equals("") && !word.equals(" ");
    }

    public String getPhrase(){
        if (isValidWord(word1) && isValidWord(word2) && isValidWord(word3) && isValidWord(word4) && isValidWord(word5) && isValidWord(word6))
        {
            String phrase = "";
            phrase += word1 + word2 + word3 + word4 + word5 + word6;
            return phrase;
        }
        return null;
    }
}
