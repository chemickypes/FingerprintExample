package me.bemind.fingerprinthelper;

import java.security.spec.InvalidParameterSpecException;

import javax.crypto.Cipher;

/**
 * Created by angelomoroni on 05/02/17.
 */

public interface IFingerPrintUiHelper {

    boolean isHardwareDetected();

    boolean hasEnrolledFingerprints();

    void startListening();

    boolean initCipher();

    void stopListening();


    void removeListener();

    void setAuthenticationCallback(AuthenticationCallback authenticationCallback);

    Cipher getCipher();

    byte[] saveIvParams() throws InvalidParameterSpecException;
}
