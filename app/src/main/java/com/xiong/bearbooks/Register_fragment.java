package com.xiong.bearbooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.xiong.bearbooks.util.Util;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register_fragment extends Fragment {
    private EditText account_register_edit;
    private EditText email_register_edit;
    private EditText password_register_edit;
    private EditText passwordConfirm_register_edit;
    private Button user_register_button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.register_fragment,container,false);
        account_register_edit=view.findViewById(R.id.account_register_edit);
        email_register_edit=view.findViewById(R.id.email_register_edit);
        password_register_edit=view.findViewById(R.id.password_register_edit);
        passwordConfirm_register_edit=view.findViewById(R.id.passwordConfirm_register_edit);
        user_register_button=view.findViewById(R.id.user_register_button);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user_register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account_register_edit.getText().toString().equals("")){
                    Toast.makeText(MyApplication.getContext(),"请输入昵称",Toast.LENGTH_SHORT).show();
                }else if (email_register_edit.getText().toString().equals("")){
                    Toast.makeText(MyApplication.getContext(),"请输入注册邮箱",Toast.LENGTH_SHORT).show();
                }else if (password_register_edit.getText().toString().equals("")){
                    Toast.makeText(MyApplication.getContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                }else if (passwordConfirm_register_edit.getText().toString().equals("")){
                    Toast.makeText(MyApplication.getContext(),"请再次输入密码",Toast.LENGTH_SHORT).show();
                }else if (RecheckAccount(account_register_edit.getText().toString())==true){
                    Toast.makeText(MyApplication.getContext(),"昵称已经存在了，请重新取一个",Toast.LENGTH_SHORT).show();
                }else if (!isEmail(email_register_edit.getText().toString())){
                    email_register_edit.setText("");
                    Toast.makeText(MyApplication.getContext(),"邮箱格式不对，请重新输入",Toast.LENGTH_SHORT).show();
                }else if (RecheckEmail(email_register_edit.getText().toString())==true){
                    Toast.makeText(MyApplication.getContext(),"邮箱已经被注册，请重新输入一个",Toast.LENGTH_SHORT).show();
                }else if (!password_register_edit.getText().toString().equals(passwordConfirm_register_edit.getText().toString())){
                    password_register_edit.setText("");
                    passwordConfirm_register_edit.setText("");
                    Toast.makeText(MyApplication.getContext(),"两次输入的密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
                }else {
                    User user=new User();
                    user.setUserName(account_register_edit.getText().toString());
                    user.setEmail(email_register_edit.getText().toString());
                    user.setPassword(password_register_edit.getText().toString());
                    user.setIsCurrent(2);
                    user.save();
                    Util.sysUser();
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
                    editor.putInt("loginState",1);
                    editor.apply();
                    Intent intent=new Intent(MyApplication.getContext(),MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean RecheckAccount(String account){
        List<User> userList;
        userList= DataSupport.findAll(User.class);
        for (User user:userList) {
            if (user.getUserName().equals(account)) {
                return true;
            }
        }
        return false;
    }

    private boolean RecheckEmail(String email){
        List<User> userList;
        userList= DataSupport.findAll(User.class);
        for (User user:userList) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
    private static boolean isEmail(String email){
        String str="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
