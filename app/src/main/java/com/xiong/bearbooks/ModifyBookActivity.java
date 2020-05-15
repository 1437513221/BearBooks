package com.xiong.bearbooks;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.db.Journal;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ModifyBookActivity extends AppCompatActivity {
    private Toolbar modify_book_toolbar;
    private EditText new_amount_edit;
    private EditText new_name_edit;
    private Spinner canDel_spinner;
    private Spinner cycle_spinner;
    String canDel_spinner_result;
    String cycle_spinner_result;
    private Book book;
    private ArrayAdapter<String> canDelAdapter;
    private ArrayAdapter<String> cycleAdapter;
    private TextView newBookCycleDate_text;
    private int mYear,mMonth,mDay;
//    private int bookId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                showWarnDialog();
                break;
            default:
        }
        return true;
    }

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
        setContentView(R.layout.activity_modify_book);
        modify_book_toolbar= (Toolbar) findViewById(R.id.modify_book_toolbar);
        new_name_edit = (EditText) findViewById(R.id.new_name_edit);
        new_amount_edit = (EditText) findViewById(R.id.newBookAmount_edit);
        canDel_spinner = (Spinner) findViewById(R.id.canDel_spinner);
        cycle_spinner = (Spinner) findViewById(R.id.newBookCycle_spinner);
        newBookCycleDate_text=findViewById(R.id.newBookCycleDate_text);
        Button submit_button = (Button) findViewById(R.id.submit_button);
        Button back_button = (Button) findViewById(R.id.back_button);

        final String[] canDel = new String[]{"不能", "能"};
        final String[] cycle = new String[]{"月", "年", "永久"};
        setSupportActionBar(modify_book_toolbar);
        canDelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, canDel);
        canDelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        canDel_spinner.setAdapter(canDelAdapter);

        cycleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cycle);
        cycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cycle_spinner.setAdapter(cycleAdapter);
        getBook();
        new_name_edit.setText(book.getName());
        new_amount_edit.setText(String.valueOf((int) book.getAmount()));
        final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        newBookCycleDate_text.setText(simpleDateFormat.format(book.getSetCycleDate()));
        if (book.isCanDel() == false) {
            canDel_spinner.setSelection(0);
        } else {
            canDel_spinner.setSelection(1);
        }
        cycle_spinner.setSelection(book.getCycle()-1);

        canDel_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                canDel_spinner_result = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        newBookCycleDate_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyBookActivity.this);
//                View view = View.inflate(ModifyBookActivity.this, R.layout.date_time_dialog2, null);
//                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
//                builder.setView(view);
//
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(new Date());
//                datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
//                if (v.getId()== R.id.newBookCycleDate_text){
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            mYear=datePicker.getYear();
//                            mMonth=datePicker.getMonth()+1;
//                            mDay=datePicker.getDayOfMonth();
//                            String s=mYear+"-"+mMonth+"-"+mDay;
//                            newBookCycleDate_text.setText(s);
//                            dialog.cancel();
//                        }
//                    });
//                }
//                Dialog dialog=builder.create();
//                dialog.show();
//            }
//        });

        cycle_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cycle_spinner_result = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new_name_edit.getText().toString().equals("")){
                    Toast.makeText(ModifyBookActivity.this,"同学，账本名你给忘了",Toast.LENGTH_SHORT).show();
                }else if (new_amount_edit.getText().toString().equals("")){
                    Toast.makeText(ModifyBookActivity.this,"同学，预算金额未输入",Toast.LENGTH_SHORT).show();
                }else {
                    Book book = new Book();
                    book.setName(new_name_edit.getText().toString());
                    book.setAmount(Integer.parseInt(new_amount_edit.getText().toString()));
                    try {
                        book.setSetCycleDate(simpleDateFormat.parse(newBookCycleDate_text.getText().toString()
                        ));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (canDel_spinner_result.equals("不能")) {
                        book.setToDefault("canDel");
                    } else{
                        book.setCanDel(true);
                    }

                    if (cycle_spinner_result.equals("月")) {
                        book.setCycle(1);
                    } else if (cycle_spinner_result.equals("年")) {
                        book.setCycle(2);
                    } else {
                        book.setCycle(3);
                    }
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ModifyBookActivity.this);
                    int bookId = preferences.getInt("bookId", 0);
                    book.updateAll("id=?", String.valueOf(bookId));
                    Intent intent=new Intent(ModifyBookActivity.this,BookActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ModifyBookActivity.this,BookActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getBook() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int bookId = preferences.getInt("bookId", 0);
        book = DataSupport.where("bookId=?", String.valueOf(bookId)).find(Book.class).get(0);
    }

    //更新的账本名查重
    private boolean recheck(String bookName) {
        List<Book> bookList = DataSupport.findAll(Book.class);
        for (Book book : bookList) {
            if (book.getName().equals(bookName)) {
                return false;
            }
        }
        return true;
    }

    //查询账本是否能删除
    private boolean recheckCanDel(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int bookId = preferences.getInt("bookId", 0);
        Boolean b=DataSupport.where("id=?",String.valueOf(bookId)).find(Book.class).get(0).isCanDel();
        return b;
    }

    private void showWarnDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("确定删除吗？").setMessage("删除之后不可恢复哦")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ModifyBookActivity.this);
                        int bookId = preferences.getInt("bookId", 0);
                        if (recheckCanDel()==false){
                            Toast.makeText(ModifyBookActivity.this,"默认账本不可删除",Toast.LENGTH_SHORT).show();
                        }else {DataSupport.delete(Book.class,bookId);
                            DataSupport.deleteAll(Journal.class,"bookId=?",String.valueOf(bookId));
                            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(ModifyBookActivity.this).edit();
                            editor.putInt("bookId",1);
                            editor.apply();
                            Intent intent =new Intent(ModifyBookActivity.this,BookActivity.class);
                            startActivity(intent);
                            Toast.makeText(ModifyBookActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}
