package com.xiong.bearbooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiong.bearbooks.db.User;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ModifyUserActivity extends AppCompatActivity {
    private EditText account_modifyUser_edit;
    private TextView email_modifyUser_text;
    private EditText newPassword_modifyUser_edit;
    private EditText newPasswordConfirm_modifyUser_edit;
    private Button user_modify_button;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);
        account_modifyUser_edit=findViewById(R.id.account_modifyUser_edit);
        email_modifyUser_text=findViewById(R.id.email_modifyUser_text);
        newPassword_modifyUser_edit=findViewById(R.id.newPassword_modifyUser_edit);
        newPasswordConfirm_modifyUser_edit=findViewById(R.id.newPasswordConfirm_modifyUser_edit);
        user_modify_button=findViewById(R.id.user_modify_button);
        user= DataSupport.where("isCurrent=?","2").findFirst(User.class);

        account_modifyUser_edit.setText(user.getUserName());
        email_modifyUser_text.setText(user.getEmail());
        user_modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account_modifyUser_edit.getText().toString().equals("")){
                    Toast.makeText(MyApplication.getContext(),"请输入昵称",Toast.LENGTH_SHORT).show();
                }else if (RecheckAccount(account_modifyUser_edit.getText().toString())&&!account_modifyUser_edit.getText().toString().equals(user.getUserName())){
                    account_modifyUser_edit.setText("");
                    Toast.makeText(MyApplication.getContext(),"昵称已存在",Toast.LENGTH_SHORT).show();
                }else if (newPassword_modifyUser_edit.getText().toString().equals("")){
                    Toast.makeText(MyApplication.getContext(),"请输入新密码",Toast.LENGTH_SHORT).show();
                }else if (newPasswordConfirm_modifyUser_edit.getText().toString().equals("")){
                    Toast.makeText(MyApplication.getContext(),"请确认新密码",Toast.LENGTH_SHORT).show();
                }else if (!newPassword_modifyUser_edit.getText().toString().equals(newPasswordConfirm_modifyUser_edit.getText().toString())){
                    newPassword_modifyUser_edit.setText("");
                    newPasswordConfirm_modifyUser_edit.setText("");
                    Toast.makeText(MyApplication.getContext(),"两次密码输入不一致",Toast.LENGTH_SHORT).show();
                }else {
                    User user1=new User();
                    user1.setUserName(account_modifyUser_edit.getText().toString());
                    user1.setPassword(newPassword_modifyUser_edit.getText().toString());
                    user1.updateAll("id=?",String.valueOf(user.getUserId()));
                    User user2=new User();
                    user2.setIsCurrent(1);
                    user2.updateAll();
                    Toast.makeText(MyApplication.getContext(),"更改成功，请重新登录",Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
                    editor.putInt("loginState",0);
                    editor.apply();
                    Intent intent=new Intent(ModifyUserActivity.this,MainActivity.class);
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
}
