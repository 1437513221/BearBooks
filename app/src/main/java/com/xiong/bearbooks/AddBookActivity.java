package com.xiong.bearbooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.util.Util;

import org.litepal.crud.DataSupport;

import java.util.List;

public class AddBookActivity extends AppCompatActivity {
    private EditText newBookName_edit;
    private EditText newBookAmount_edit;
    private Spinner newBookCycle_spinner;
    private Button newBookSubmit_button;
    private Button newBookBack_button;
    private ArrayAdapter<String> newBookCycleAdapter;
    private String newBookCycle_spinner_result;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(this, BookActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        newBookName_edit=findViewById(R.id.newBookName_edit);
        newBookAmount_edit=findViewById(R.id.newBookAmount_edit);
        newBookCycle_spinner=findViewById(R.id.newBookCycle_spinner);
        newBookSubmit_button=findViewById(R.id.newBookSubmit_button);
        newBookBack_button=findViewById(R.id.newBookBack_button);
        final String[] newBookCycle = new String[]{"月", "年", "永久"};
        newBookCycleAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, newBookCycle);
        newBookCycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newBookCycle_spinner.setAdapter(newBookCycleAdapter);
        newBookCycle_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newBookCycle_spinner_result=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        newBookSubmit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recheck(newBookName_edit.getText().toString())==false){
                    Toast.makeText(AddBookActivity.this,"该帐本已存在，请重新输入",Toast.LENGTH_SHORT).show();
                }else if (newBookAmount_edit.getText().toString().equals("")){
                    Toast.makeText(AddBookActivity.this,"预算金额未输入",Toast.LENGTH_SHORT).show();
                }else {
                    Book book=new Book();
                    book.setName(newBookName_edit.getText().toString());
                    book.setAmount(Double.parseDouble(newBookAmount_edit.getText().toString()));
                    if (newBookCycle_spinner_result.equals("月")){
                        book.setCycle(1);
                    }else if (newBookCycle_spinner_result.equals("年")){
                        book.setCycle(2);
                    }else {
                        book.setCycle(3);
                    }
                    book.setCanDel(true);
                    book.save();
                    Util.synBook();
                    int id= DataSupport.where("name=?",newBookName_edit.getText().toString()).select("bookId").find(Book.class).get(0).getBookId();
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(AddBookActivity.this).edit();
                    editor.putInt("bookId",id);
                    editor.apply();
                    Intent intent=new Intent(AddBookActivity.this,BookActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
    private boolean recheck(String bookName) {
        List<Book> bookList = DataSupport.findAll(Book.class);
        for (Book book : bookList) {
            if (book.getName().equals(bookName)) {
                return false;
            }
        }
        return true;
    }
}
