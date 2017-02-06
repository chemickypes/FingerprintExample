package me.bemind.fingerprinthelper;

import android.content.Context;
import android.os.Build;

/**
 * Created by angelomoroni on 06/02/17.
 */

public class FingerPrintHelperBuilder {

    public static IFingerPrintUiHelper getFingerPrintUIHelper(Context context, AuthenticationCallback authenticationCallback){
        IFingerPrintUiHelper fingerPrintUIHelper;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            fingerPrintUIHelper = new FingerPrintUIHelper(context, authenticationCallback);
        }else {
            fingerPrintUIHelper = new OldFingerPrntUIHelper();
        }

        return fingerPrintUIHelper;
    }
}
