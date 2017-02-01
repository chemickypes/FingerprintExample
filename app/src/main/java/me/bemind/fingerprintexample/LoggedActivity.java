package me.bemind.fingerprintexample;

import android.app.Activity;
import android.os.Bundle;

public class LoggedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
    }
}
