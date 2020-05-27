package eu.japk.hashpass;


import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecretKeyFunctions.class, KeyGenerator.class})
public class SecretKeyFunctionsTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGenerateKeySucceeds() throws Exception {
        PowerMockito.mockStatic(KeyStore.class);
        KeyGenerator keyGenerator = PowerMockito.mock(KeyGenerator.class);
        PowerMockito.mockStatic(KeyProperties.class);
        PowerMockito.mockStatic(KeyGenerator.class);
        SecretKey expected = PowerMockito.mock(SecretKey.class);
        PowerMockito.when(keyGenerator.generateKey()).thenReturn(expected);
        PowerMockito.when(KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")).thenReturn(keyGenerator);
        KeyGenParameterSpec keyGenParameterSpec = PowerMockito.mock(KeyGenParameterSpec.class);
        KeyGenParameterSpec.Builder builder = newKeyGenParameterSpecBuilder(keyGenParameterSpec);
        PowerMockito.whenNew(KeyGenParameterSpec.Builder.class).withAnyArguments().thenReturn(builder);


        SecretKeyFunctions skf = new SecretKeyFunctions();
        SecretKey sk = skf.generateKey();

        Mockito.verify(builder).setBlockModes(KeyProperties.BLOCK_MODE_GCM);
        Mockito.verify(builder).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE);
        Mockito.verify(keyGenerator).generateKey();

        assertThat(sk, is(expected));


    }

    @Test
    public void testThrowsNoSuchProviderExceptionWhenProviderNotAvailable() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        exception.expect(NoSuchProviderException.class);
        KeyGenerator keyGenerator = PowerMockito.mock(KeyGenerator.class);
        PowerMockito.when(KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")).thenReturn(keyGenerator);
        SecretKeyFunctions skf = new SecretKeyFunctions();
        skf.generateKey();
    }

    @Test
    public void testThrowsNoSuchAlgorithmExceptionWhenAlgorithmNotSpecifiedCorrectlyNotAvailable() throws Exception {
        exception.expect(NoSuchAlgorithmException.class);
        PowerMockito.mockStatic(KeyStore.class);
        KeyGenerator keyGenerator = PowerMockito.mock(KeyGenerator.class);
        PowerMockito.mockStatic(KeyProperties.class);
        PowerMockito.mockStatic(KeyGenerator.class);
        SecretKey expected = PowerMockito.mock(SecretKey.class);
        PowerMockito.when(keyGenerator.generateKey()).thenReturn(expected);
        PowerMockito.when(KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")).thenThrow(NoSuchAlgorithmException.class);
        KeyGenParameterSpec keyGenParameterSpec = PowerMockito.mock(KeyGenParameterSpec.class);
        KeyGenParameterSpec.Builder builder = newKeyGenParameterSpecBuilder(keyGenParameterSpec);
        PowerMockito.whenNew(KeyGenParameterSpec.Builder.class).withAnyArguments().thenReturn(builder);


        SecretKeyFunctions skf = new SecretKeyFunctions();
        skf.generateKey();
    }

    @Test
    @Ignore
    public void testGetKeyReturnsASecretKey() throws Exception {
        PowerMockito.mockStatic(KeyStore.class);
        SecretKey expected = PowerMockito.mock(SecretKey.class);
        KeyStore keyStore = PowerMockito.mock(KeyStore.class);
        PowerMockito.when(KeyStore.getInstance("AndroidKeyStore")).thenReturn(keyStore);

        KeyStore.SecretKeyEntry secretKeyEntry = PowerMockito.mock(KeyStore.SecretKeyEntry.class);
        PowerMockito.when(keyStore.getEntry("SecretEncryptionKey", null)).thenReturn(secretKeyEntry);

        SecretKeyFunctions skf = new SecretKeyFunctions();
        SecretKey sk = skf.getKey();

        Mockito.verify(keyStore).load(null);

        assertThat(sk, is(expected));


    }

    private KeyGenParameterSpec.Builder newKeyGenParameterSpecBuilder(KeyGenParameterSpec expectedBuilderOutput) {
        KeyGenParameterSpec.Builder builder = PowerMockito.mock(KeyGenParameterSpec.Builder.class);
        PowerMockito.when(builder.setEncryptionPaddings(anyString())).thenReturn(builder);
        PowerMockito.when(builder.setBlockModes(anyString())).thenReturn(builder);
        PowerMockito.when(builder.build()).thenReturn(expectedBuilderOutput);
        return builder;
    }
}
