package com.dongdongwu.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import com.jelly.thor.maxnumberet.MaxEditTextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MaxEditTextView";

    private MaxEditTextView mMaxEditTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMaxEditTextView = (MaxEditTextView) findViewById(R.id.et);
        mMaxEditTextView.setModule(3, 100D, true, new MaxEditTextView.ICall() {
            @Override
            public void all(String s) {
                Log.d(TAG, "回调: " + s);
            }
        });
    }
}
c