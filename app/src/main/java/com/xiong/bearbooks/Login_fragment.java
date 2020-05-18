package com.xiong.bearbooks;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xiong.bearbooks.db.User;

import org.litepal.crud.DataSupport;

import java.util.List;

public class Login_fragment extends Fragment {
    private EditText email_login_edit;
    private EditText password_login_edit;
    private Button userLogin_button;
    private Button userRegister_button;
    private String password;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login_fragment,container,false);
        email_login_edit=view.findViewById(R.id.email_login_edit);
        password_login_edit=view.findViewById(R.id.password_login_edit);
        userLogin_button=view.findViewById(R.id.userLogin_button);
        userRegister_button=view.findViewById(R.id.userRegister_button);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userLogin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email_login_edit.getText().toString().equals("")){
                    Toast.makeText(MyApplication.getContext(),"输入注册的邮箱才能登陆",Toast.LENGTH_SHORT).show();
                }else if (password_login_edit.getText().toString().equals("")){
                        Toast.makeText(MyApplication.getContext(),"输入密码才能登陆",Toast.LENGTH_SHORT).show();
                    }else{
                    List<User> userList=DataSupport.where("email=?",email_login_edit.getText().toString()).find(User.class);
                    if (userList.size()==0){
                        email_login_edit.setText("");
                        Toast.makeText(MyApplication.getContext(),"该邮箱不存在，请重新输入",Toast.LENGTH_SHORT).show();
                    }else{
                        User user=DataSupport.where("email=?",email_login_edit.getText().toString()).findFirst(User.class);
                        password=user.getPassword();
                        int userId=user.getUserId();
                        Log.d("userId", String.valueOf(userId));
                        if (password_login_edit.getText().toString().equals(password)){
                            setCurrent(userId,2);
                            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
                            editor.putInt("loginState",1);
                            editor.apply();
                            Intent intent =new Intent(MyApplication.getContext(),MainActivity.class);
                            startActivity(intent);
                        }else {
                            password_login_edit.setText("");
                            Toast.makeText(MyApplication.getContext(),"密码错误，请重新输入",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        userRegister_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity loginActivity= (LoginActivity) getActivity();
                loginActivity.replaceLineChartFragment(new Register_fragment());
            }
        });

    }
    private void setCurrent(int Id,int isCurrent){
        User user=new User();
        user.setIsCurrent(isCurrent);
        user.updateAll("id=?",String.valueOf(Id));
    }
}
