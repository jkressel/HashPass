package eu.japk.hashpass;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class KeyPhraseTest {

    KeyPhrase keyPhrase;

    @Before
    public void setup(){
        keyPhrase = new KeyPhrase();
    }

    @Test
    public void testAddingKeyPhrase(){
        assertTrue(keyPhrase.newPhrase("A","B","C","D","E","F"));
    }
    @Test
    public void testAddingTooFewWordsKeyPhrase(){
        assertFalse(keyPhrase.newPhrase("A","B","C","D","E",null));
    }

    @Test
    public void testAddingEmptyWordKeyPhrase(){
        assertFalse(keyPhrase.newPhrase("A","B","C"," ","E","F"));
    }

    @Test
    public void testAddingEmptyStringKeyPhrase(){
        assertFalse(keyPhrase.newPhrase("A","B","C","D","","F"));
    }

    @Test
    public void testGettingKeyPhrase(){
        keyPhrase.newPhrase("A","B","C","D","E","F");
        assertEquals(keyPhrase.getPhrase(), "ABCDEF");
    }

    @Test
    public void testGettingNoKeyPhrase(){
        assertNull(keyPhrase.getPhrase());
    }


}
