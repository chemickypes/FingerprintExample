package me.bemind.fingerprintexample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import me.bemind.fingerprinthelper.AuthenticationCallback;
import me.bemind.fingerprinthelper.FingerPrintHelperBuilder;
import me.bemind.fingerprinthelper.IFingerPrintUiHelper;

public class MainActivity extends Activity implements AuthenticationCallback {



    private static final int REQUEST_FINGER_PRINT_PERMISSION = 234;
    private TextView descTextView;

    private IFingerPrintUiHelper fingerPrintUIHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        descTextView = (TextView) findViewById(R.id.desc);

        initFingerPrint();


    }

    private void initFingerPrint() {

        fingerPrintUIHelper = FingerPrintHelperBuilder.getFingerPrintUIHelper(this,this);

        // crypto = new FingerprintManagerCompat.CryptoObject(mChifer);


        if (!fingerPrintUIHelper.isHardwareDetected()) {
            // Device doesn't support fingerprint authentication
            descTextView.setText(R.string.no_finger_senso);
        } else if (!fingerPrintUIHelper.hasEnrolledFingerprints()) {
            // User hasn't enrolled any fingerprints to authenticate with
            descTextView.setText(R.string.note);
        } else {
            // Everything is ready for fingerprint authentication

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.USE_FINGERPRINT) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.USE_FINGERPRINT},
                        REQUEST_FINGER_PRINT_PERMISSION);

            } else {


                startListeningFingerPrint();
            }

        }
    }

    private void startListeningFingerPrint() {
        if(fingerPrintUIHelper.initCipher()){
            fingerPrintUIHelper.startListening();
        }else {
            //show error
            descTextView.setText(R.string.errore_generico);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_FINGER_PRINT_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListeningFingerPrint();
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        descTextView.setText(errString);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        descTextView.setText(helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        fingerPrintUIHelper.stopListening();
        startActivity(new Intent(this,LoggedActivity.class));
    }

    @Override
    public void onAuthenticationFailed() {
        descTextView.setText(R.string.auth_failed);
    }






}
