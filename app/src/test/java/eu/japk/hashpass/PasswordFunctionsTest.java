package eu.japk.hashpass;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordFunctionsTest {
    PasswordFunctions passwordFunctions;
    Salt salt;
    KeyPhrase keyPhrase;

    @Before
    public void setup(){
        salt = new Salt();
        keyPhrase = new KeyPhrase();
    }

    @Test
    public void testSpecialCharsPasswordIsCorrectAnd30Length(){
        salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
        keyPhrase.newPhrase("A", "B", "C", "D", "E", "F");
        passwordFunctions = new PasswordFunctions(keyPhrase, salt);
        assertEquals(passwordFunctions.getPassword(true, 30).length(), 30);
        assertEquals(passwordFunctions.getPassword(true, 30), "@??0`zG/6&*%v6ZG2`ic`dJ0>Xdm2#");

    }

    @Test
    public void testSpecialCharsPasswordIsCorrectAnd100GivenLength(){
        salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
        keyPhrase.newPhrase("A", "B", "C", "D", "E", "F");
        passwordFunctions = new PasswordFunctions(keyPhrase, salt);
        assertEquals(passwordFunctions.getPassword(true, 100).length(), 64);
        assertEquals(passwordFunctions.getPassword(true, 100), "@??0`zG/6&*%v6ZG2`ic`dJ0>Xdm2#I>,(v:=)g#?HIlrsU\"3(\"<2%4>8[;hXb7<");

    }

    @Test
    public void testSpecialCharsPasswordIsCorrectAnd64Length(){
        salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
        keyPhrase.newPhrase("A", "B", "C", "D", "E", "F");
        passwordFunctions = new PasswordFunctions(keyPhrase, salt);
        assertEquals(passwordFunctions.getPassword(true, 64).length(), 64);
        assertEquals(passwordFunctions.getPassword(true, 64), "@??0`zG/6&*%v6ZG2`ic`dJ0>Xdm2#I>,(v:=)g#?HIlrsU\"3(\"<2%4>8[;hXb7<");

    }

    @Test
    public void testSpecialCharsPasswordWithDifferentSaltAnd64LengthDifferent(){
        salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaac");
        keyPhrase.newPhrase("A", "B", "C", "D", "E", "F");
        passwordFunctions = new PasswordFunctions(keyPhrase, salt);
        assertEquals(passwordFunctions.getPassword(true, 64).length(), 64);
        assertNotEquals(passwordFunctions.getPassword(true, 64), "@??0`zG/6&*%v6ZG2`ic`dJ0>Xdm2#I>,(v:=)g#?HIlrsU\"3(\"<2%4>8[;hXb7<");

    }

    @Test
    public void testNoSpecialCharsPasswordIsCorrectAnd30Length(){
        salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
        keyPhrase.newPhrase("A", "B", "C", "D", "E", "F");
        passwordFunctions = new PasswordFunctions(keyPhrase, salt);
        assertEquals(passwordFunctions.getPassword(false, 30).length(), 30);
        assertEquals(passwordFunctions.getPassword(false, 30), "0Uzk1Rcjq5eZNLvcH1A415fkyt5EmX");

    }

    @Test
    public void testNoSpecialCharsPasswordIsCorrectAnd100GivenLength(){
        salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
        keyPhrase.newPhrase("A", "B", "C", "D", "E", "F");
        passwordFunctions = new PasswordFunctions(keyPhrase, salt);
        assertEquals(passwordFunctions.getPassword(false, 100).length(), 64);
        assertEquals(passwordFunctions.getPassword(false, 100), "0Uzk1Rcjq5eZNLvcH1A415fkyt5EmXeyBcNPx88XzdeDJKqWI71wmZoTswv9t3Mw");

    }

    @Test
    public void testNoSpecialCharsPasswordIsCorrectAnd64Length(){
        salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
        keyPhrase.newPhrase("A", "B", "C", "D", "E", "F");
        passwordFunctions = new PasswordFunctions(keyPhrase, salt);
        assertEquals(passwordFunctions.getPassword(false, 64).length(), 64);
        assertEquals(passwordFunctions.getPassword(false, 64), "0Uzk1Rcjq5eZNLvcH1A415fkyt5EmXeyBcNPx88XzdeDJKqWI71wmZoTswv9t3Mw");

    }

    @Test
    public void testNoSpecialCharsPasswordWithDifferentSaltAnd64LengthDifferent(){
        salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaac");
        keyPhrase.newPhrase("A", "B", "C", "D", "E", "F");
        passwordFunctions = new PasswordFunctions(keyPhrase, salt);
        assertEquals(passwordFunctions.getPassword(false, 64).length(), 64);
        assertNotEquals(passwordFunctions.getPassword(false, 64), "0Uzk1Rcjq5eZNLvcH1A415fkyt5EmXeyBcNPx88XzdeDJKqWI71wmZoTswv9t3Mw");

    }



}
