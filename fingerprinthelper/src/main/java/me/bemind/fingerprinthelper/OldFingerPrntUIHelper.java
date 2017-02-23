package me.bemind.fingerprinthelper;

import java.security.spec.InvalidParameterSpecException;

import javax.crypto.Cipher;

/**
 * Created by angelomoroni on 05/02/17.
 */
public class OldFingerPrntUIHelper implements IFingerPrintUiHelper {
    @Override
    public boolean isHardwareDetected() {
        return false;
    }

    @Override
    public boolean hasEnrolledFingerprints() {
        return false;
    }

    @Override
    public void startListening() {

    }

    @Override
    public boolean initCipher() {
        return false;
    }

    @Override
    public void stopListening() {

    }

    @Override
    public void removeListener() {

    }

    @Override
    public void setAuthenticationCallback(AuthenticationCallback authenticationCallback) {

    }

    @Override
    public Cipher getCipher() {
        return null;
    }

    @Override
    public byte[] saveIvParams() throws InvalidParameterSpecException {
        return new byte[0];
    }
}
