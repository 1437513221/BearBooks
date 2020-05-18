package com.xiong.bearbooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xiong.bearbooks.db.User;

public class LoginOut_fragment extends Fragment {
    private Button loginOut_button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login_out_fragment,container,false);
        loginOut_button=view.findViewById(R.id.loginOut_button);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginOut_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user=new User();
                user.setIsCurrent(1);
                user.updateAll();
                Toast.makeText(MyApplication.getContext(),"注销成功",Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
                editor.putInt("loginState",0);
                editor.apply();
                Intent intent=new Intent(MyApplication.getContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
