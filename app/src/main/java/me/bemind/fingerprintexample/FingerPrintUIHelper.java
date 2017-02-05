package me.bemind.fingerprintexample;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

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

public class FingerPrintUIHelper implements IFingerPrintUiHelper {

    private static final String KEY_NAME = "fp_key";
    private Context context;


    private FingerprintManager fingeprintManager;
    private AuthenticationCallback authenticationCallback;
    private CancellationSignal mCancellationSignal;
    private boolean mSelfCancelled;


    private FingerprintManager.CryptoObject crypto;
    private Cipher mCipher;
    private KeyStore mKeyStore;
    private String keyName = KEY_NAME;


    //compat
    private FingerprintManagerCompat fingeprintManagerCompat;


    public FingerPrintUIHelper(FingerprintManager fingerprintManager, AuthenticationCallback authenticationCallback) {
        super();
        this.authenticationCallback = authenticationCallback;
        this.fingeprintManager = fingerprintManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public FingerPrintUIHelper(Context context, AuthenticationCallback authenticationCallback) {
        super();
        this.context = context;
        this.authenticationCallback = authenticationCallback;
        this.fingeprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
    }

    public FingerPrintUIHelper(FingerprintManager fingerprintManager, AuthenticationCallback authenticationCallback, String keyName) {
        super();
        this.authenticationCallback = authenticationCallback;
        this.fingeprintManager = fingerprintManager;

        this.keyName = keyName;
    }

    public void startListening() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Do something for lollipop and above versions

            crypto = new FingerprintManager.CryptoObject(mCipher);
            startListening(crypto);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startListening(FingerprintManager.CryptoObject crypto) {
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            authenticationCallback.onAuthenticationError(111,"Concedi i pemessi");
        }

        fingeprintManager.authenticate(crypto, mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                authenticationCallback.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                authenticationCallback.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                authenticationCallback.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                authenticationCallback.onAuthenticationFailed();
            }
        }, new Handler(Looper.getMainLooper()));
    }


    public void stopListening() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            if (mCancellationSignal != null) {
                mSelfCancelled = true;
                mCancellationSignal.cancel();
                mCancellationSignal = null;
            }
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


    public boolean isHardwareDetected() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Do something for lollipop and above versions
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                authenticationCallback.onAuthenticationError(111,"Concedi i pemessi");
            }
            return fingeprintManager.isHardwareDetected();
        } else {
            // do something for phones running an SDK before lollipop
            return false;
        }


    }

    public boolean hasEnrolledFingerprints() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Do something for lollipop and above versions


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
               authenticationCallback.onAuthenticationError(111,"Concedi i pemessi");
            }
            return fingeprintManager.hasEnrolledFingerprints();
        } else{
            // do something for phones running an SDK before lollipop
            return false;
        }


    }

    public boolean permissionGranted(){
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED);
    }




    public interface AuthenticationCallback{
        void onAuthenticationError(int errMsgId, CharSequence errString) ;

        void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

        void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result);

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
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        }

        @Override
        public void onAuthenticationFailed() {

        }
    }
}
