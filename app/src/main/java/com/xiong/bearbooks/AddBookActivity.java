package com.xiong.bearbooks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.util.Util;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddBookActivity extends AppCompatActivity {
    private EditText newBookName_edit;
    private EditText newBookAmount_edit;
    private TextView newBookStartDate_text;
    private Spinner newBookCycle_spinner;
    private Button newBookSubmit_button;
    private Button newBookBack_button;
    private ArrayAdapter<String> newBookCycleAdapter;
    private String newBookCycle_spinner_result;

    private int mYear,mMonth,mDay;

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
        newBookStartDate_text=findViewById(R.id.newBookStartDate_text);
        newBookCycle_spinner=findViewById(R.id.newBookCycle_spinner);
        newBookSubmit_button=findViewById(R.id.newBookSubmit_button);
        newBookBack_button=findViewById(R.id.newBookBack_button);

//        newBookStartDate_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(AddBookActivity.this);
//                View view = View.inflate(AddBookActivity.this, R.layout.date_time_dialog2, null);
//                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
//                builder.setView(view);
//
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(new Date());
//                datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
//                if (v.getId()== R.id.newBookStartDate_text){
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            mYear=datePicker.getYear();
//                            mMonth=datePicker.getMonth()+1;
//                            mDay=datePicker.getDayOfMonth();
//                            String s=mYear+"-"+mMonth+"-"+mDay;
//                            newBookStartDate_text.setText(s);
//                            dialog.cancel();
//                        }
//                    });
//                }
//                Dialog dialog=builder.create();
//                dialog.show();
//            }
//        });

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

        final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        newBookStartDate_text.setText(simpleDateFormat.format(new Date()));
        newBookSubmit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recheck(newBookName_edit.getText().toString())==false){
                    Toast.makeText(AddBookActivity.this,"同学，该帐本名已经有了，来个新的",Toast.LENGTH_SHORT).show();
                }else if (newBookName_edit.getText().toString().equals("")){
                    Toast.makeText(AddBookActivity.this,"同学，账本名是不是忘了输入",Toast.LENGTH_SHORT).show();
                }else if (newBookAmount_edit.getText().toString().equals("")){
                    Toast.makeText(AddBookActivity.this,"同学，预算金额你忘了吧",Toast.LENGTH_SHORT).show();
                }else if(newBookStartDate_text.getText().toString().equals("")){
                    Toast.makeText(AddBookActivity.this,"同学，始计周期日点击输入一下下",Toast.LENGTH_SHORT).show();
                } else{
                    Book book=new Book();
                    book.setName(newBookName_edit.getText().toString());
                    book.setAmount(Integer.parseInt((newBookAmount_edit.getText().toString())));
                        book.setSetCycleDate(new Date());
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
