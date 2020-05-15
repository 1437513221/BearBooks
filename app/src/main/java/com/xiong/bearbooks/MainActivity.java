package com.xiong.bearbooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
//        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
//        editor.putInt("bookId",2);
//        editor.apply();

//        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
//        editor.putInt("loginState",0);
//        editor.apply();

        Intent intent=new Intent(this, BookActivity.class);
        startActivity(intent);
        finish();


    }


}
