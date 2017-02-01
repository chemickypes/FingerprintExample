package me.bemind.fingerprintexample;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by angelomoroni on 01/02/17.
 */

public class FingerPrintUIHelper extends FingerprintManagerCompat.AuthenticationCallback {

    private static final String KEY_NAME = "fp_key";

    private FingerprintManagerCompat fingeprintManager;
    private AuthenticationCallback authenticationCallback;
    private CancellationSignal mCancellationSignal;
    private boolean mSelfCancelled;


    private FingerprintManagerCompat.CryptoObject crypto;
    private Cipher mCipher;
    private KeyStore mKeyStore;
    private String keyName = KEY_NAME;


    public FingerPrintUIHelper(FingerprintManagerCompat fingerprintManager,AuthenticationCallback authenticationCallback) {
        super();
        this.authenticationCallback = authenticationCallback;
        this.fingeprintManager = fingerprintManager;
    }

    public FingerPrintUIHelper(Context context, AuthenticationCallback authenticationCallback) {
        super();
        this.authenticationCallback = authenticationCallback;
        this.fingeprintManager = FingerprintManagerCompat.from(context);
    }

    public FingerPrintUIHelper(FingerprintManagerCompat fingerprintManager,AuthenticationCallback authenticationCallback,String keyName) {
        super();
        this.authenticationCallback = authenticationCallback;
        this.fingeprintManager = fingerprintManager;

        this.keyName = keyName;
    }

    public void startListening() {
        crypto = new FingerprintManagerCompat.CryptoObject(mCipher);
        startListening(crypto);
    }


    public void startListening(FingerprintManagerCompat.CryptoObject crypto){
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        fingeprintManager.authenticate(crypto,0,mCancellationSignal,this,new Handler(Looper.getMainLooper()));
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    public AuthenticationCallback getAuthenticationCallback() {
        return authenticationCallback;
    }

    public void setAuthenticationCallback(AuthenticationCallback authenticationCallback) {
        this.authenticationCallback = authenticationCallback;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            if (mKeyStore == null) {
                createKey();
            }
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);

            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void createKey() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);

            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | KeyStoreException
                | CertificateException | NoSuchProviderException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        //super.onAuthenticationError(errMsgId, errString);
        authenticationCallback.onAuthenticationError(errMsgId,errString);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        authenticationCallback.onAuthenticationHelp(helpMsgId, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        authenticationCallback.onAuthenticationSucceeded(result);
    }

    @Override
    public void onAuthenticationFailed() {
        authenticationCallback.onAuthenticationFailed();
    }

    public boolean isHardwareDetected(){
        return fingeprintManager.isHardwareDetected();
    }

    public boolean hasEnrolledFingerprints(){
        return fingeprintManager.hasEnrolledFingerprints();
    }




    public interface AuthenticationCallback{
        void onAuthenticationError(int errMsgId, CharSequence errString) ;

        void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

        void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result);

        void onAuthenticationFailed();
    }

    public class NullAuthenticationCallback implements AuthenticationCallback{

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {

        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {

        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {

        }

        @Override
        public void onAuthenticationFailed() {

        }
    }
}
