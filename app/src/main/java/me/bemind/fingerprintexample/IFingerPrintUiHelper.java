package me.bemind.fingerprintexample;

/**
 * Created by angelomoroni on 05/02/17.
 */

public interface IFingerPrintUiHelper {

    boolean isHardwareDetected();

    boolean hasEnrolledFingerprints();

    void startListening();

    boolean initCipher();

    void stopListening();
}
