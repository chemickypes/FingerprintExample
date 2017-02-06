package me.bemind.fingerprinthelper;

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
}
