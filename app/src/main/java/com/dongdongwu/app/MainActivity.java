package com.dongdongwu.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dongdongwu.maxnumet.MaxEditTextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MaxEditTextView";

    private MaxEditTextView mMaxEditTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMaxEditTextView = (MaxEditTextView) findViewById(R.id.et);
        mMaxEditTextView.setModule(99.99D, new MaxEditTextView.ICall() {
            @Override
            public void Call(String s) {
                Log.d(TAG, "回调: " + s);
            }
        });
    }
}
