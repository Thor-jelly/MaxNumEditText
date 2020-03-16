package com.dongdongwu.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import com.jelly.thor.maxnumberet.MaxEditTextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MaxEditTextView";

    private MaxEditTextView mMaxEditTextView;

    private int model = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMaxEditTextView = (MaxEditTextView) findViewById(R.id.et);
        mMaxEditTextView.setModule(3, 999_999_999.999D, true, new MaxEditTextView.ICall() {
            @Override
            public void call(String s) {
                Log.d(TAG, "回调: " + s);
            }
        });
        mMaxEditTextView.setShowEnd0(true);

        View tv = findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model++;
                if (model % 2 == 0) {
                    mMaxEditTextView.changMaxNumber(3, 99999.99D);
                } else {
                    mMaxEditTextView.changMaxNumber(0, 99999);
                }
            }
        });
    }
}