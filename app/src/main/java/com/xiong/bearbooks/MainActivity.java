package com.xiong.bearbooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.xiong.bearbooks.util.Util;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
//            Util.initialize();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        Util.synBook();
//        Util.sysJournal();
//        Util.sysCategory();

        Intent intent=new Intent(this, BookActivity.class);
        startActivity(intent);
        finish();

//        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
//        editor.putInt("bookId",2);
//        editor.apply();
    }
}
