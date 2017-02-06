package me.bemind.fingerprinthelper;

import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by angelomoroni on 06/02/17.
 */
public class NullAuthenticationCallback implements AuthenticationCallback {

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
