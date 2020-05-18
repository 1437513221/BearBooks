package com.xiong.bearbooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class LoginActivity extends AppCompatActivity {
    private int loginState;

    public FrameLayout getLoginFrameLayout() {
        return loginFrameLayout;
    }

    private FrameLayout loginFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginFrameLayout=findViewById(R.id.loginFrameLayout);
        setContentView(R.layout.activity_login);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        loginState = preferences.getInt("loginState", 0);
        if (loginState==0){
            replaceLineChartFragment(new Login_fragment());
        }else {
            replaceLineChartFragment(new LoginOut_fragment());
        }
    }

    public void replaceLineChartFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.loginFrameLayout,fragment);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }

}
