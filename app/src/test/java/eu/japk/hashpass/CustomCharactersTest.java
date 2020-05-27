package eu.japk.hashpass;

import org.junit.Test;
import static org.junit.Assert.*;

public class CustomCharactersTest {

    private CustomCharacters customCharacters;

    @Test
    public void testCorrectCharacterNisReturned(){
        customCharacters = new CustomCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890)($&£*");
        assertEquals('N', (char)customCharacters.getCharacter(13));
    }

    @Test
    public void testCorrectCharNumber(){
        customCharacters = new CustomCharacters("ABC");
        assertEquals(3, customCharacters.getNumberOfChars());
    }

    @Test
    public void testInvalidIndexGivesExpectedResponse(){
        customCharacters = new CustomCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890)($&£*");
        assertEquals(-1, customCharacters.getCharacter(678));
    }
}
