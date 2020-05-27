package eu.japk.hashpass;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class NonSpecialCharactersTest {

    private NonSpecialCharacters nonSpecialCharacters;

    @Before
    public void setup(){
        nonSpecialCharacters = new NonSpecialCharacters();
    }

    @Test
    public void testNonSpecialIsReturned(){
        assertEquals((char)nonSpecialCharacters.getCharacter(11), 'B');
        
    }

    @Test
    public void testNonSpecialIsReturnedMod(){
        assertEquals((char)nonSpecialCharacters.getCharacter(nonSpecialCharacters.getNumberOfChars()*2 % nonSpecialCharacters.getNumberOfChars()), '0');

    }

    @Test
    public void testSizeCorrect(){
        assertEquals(nonSpecialCharacters.getNumberOfChars(), 62);
    }

    @Test
    public void testIncorrectIndexTooLarge(){
        assertEquals(nonSpecialCharacters.getCharacter(99), -1);
    }

    @Test
    public void testIncorrectIndexTooSmall(){
        assertEquals(nonSpecialCharacters.getCharacter(-1), -1);
    }


}
