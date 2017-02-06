# FingerprintExample
Example Project to show how to use Finger Print Authentication

My goal is to implement a little module to help someone who wants to use fingerprint in Android APP. 
However, in this moment library isn't on Marvel or Gradle Server. Sorry.

Anyway, I hope this project can help someone. 

#Example

Check if device has Fingerprint hardware and if user has enrolled one fingerprint

``` 
  fingerPrintUIHelper = FingerPrintHelperBuilder.getFingerPrintUIHelper(this,authenticationCallback);
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
 ```
 
 And now, start listening...
 
 ```
  private void startListeningFingerPrint() {
        if(fingerPrintUIHelper.initCipher()){
            fingerPrintUIHelper.startListening();
        }else {
            //show error
            descTextView.setText(R.string.errore_generico);
        }
    }
 ```
 
*AuthenticationCallback* has these methods
```
    void onAuthenticationError(int errMsgId, CharSequence errString) ;

    void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

    void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result);

    void onAuthenticationFailed();
```

#Android FingerprintManagerCompat has a bug
As reported [here](https://www.reddit.com/r/androiddev/comments/5r0vox/psa_fingerprintmanagercompat_is_broken_on_the/) ***FingerprintManagerCompat*** returns always *false* (using support library 25.1.0) in any device when we ask if this device has hardware.
So, in order to avoid this issue I used a manually version control. In this way, min sdk version supported was 15 without problem.
