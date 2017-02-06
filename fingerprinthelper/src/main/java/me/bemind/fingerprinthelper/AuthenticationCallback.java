package me.bemind.fingerprinthelper;

import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by angelomoroni on 06/02/17.
 */
public interface AuthenticationCallback {

    void onAuthenticationError(int errMsgId, CharSequence errString) ;

    void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

    void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result);

    void onAuthenticationFailed();

}
