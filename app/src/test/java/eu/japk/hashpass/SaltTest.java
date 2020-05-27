package eu.japk.hashpass;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SaltTest {

    Salt salt;

    @Before
    public void setup(){
        salt = new Salt();
    }

    @Test
    public void testGeneratedSaltCorrectLength(){
        salt.generateSalt();
        assertEquals(salt.getSalt().length(), 64);

    }

    @Test
    public void testAddCorrectSalt(){
        assertTrue(salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));

    }

    @Test
    public void testAddAndGetCorrectSalt(){
        assertTrue(salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        assertEquals(salt.getSalt(), "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    }

    @Test
    public void testAddTooLongSalt(){
        assertFalse(salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));

    }

    @Test
    public void testAddTooShortSalt(){
        assertFalse(salt.setSalt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));

    }

    @Test
    public void testNoSaltReturnsNull(){
        assertNull(salt.getSalt());
    }
}
