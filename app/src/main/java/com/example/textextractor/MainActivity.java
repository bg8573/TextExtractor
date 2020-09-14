package com.example.textextractor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.extractor.worker;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        worker w = new worker(this.getApplicationContext(),getResources().openRawResource(R.raw.credentials)){
            @Override
            public void getReply(String reply) {
                super.getReply(reply);
                Log.i("abc",reply);
            }
        };
        w.setUpBot();
        w.sendMessageToBot("i want jeans");
    }
}
