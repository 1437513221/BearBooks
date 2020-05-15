package com.xiong.bearbooks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.xiong.bearbooks.db.Category;
import com.xiong.bearbooks.db.Journal;
import com.xiong.bearbooks.util.DatabaseTools;
import com.xiong.bearbooks.util.Util;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddJournalActivity extends AppCompatActivity {
    private Spinner newJournal_category_spinner;
    private TextView newJournal_timePicker_text;
    private EditText newJournal_info_edit;
    private Spinner newJournal_type_spinner;
    private  EditText newJournal_amount_edit;
    private Button newJournal_submit_button;
    private List<String> categoryNameList=new ArrayList<>();
    private ArrayAdapter<String> categoryNameAdapter;
    private ArrayAdapter<String> typeAdapter;

    private int mYear,mMonth,mDay,mHour,mMinute;
    String newJournal_category_spinner_result;
    String newJournal_type_spinner_result;


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
        setContentView(R.layout.activity_add_journal);
        newJournal_category_spinner=findViewById(R.id.newJournal_category_spinner);
        newJournal_timePicker_text=findViewById(R.id.newJournal_timePicker_text);
        newJournal_info_edit=findViewById(R.id.newJournal_info_edit);
        newJournal_type_spinner=findViewById(R.id.newJournal_type_spinner);
        newJournal_amount_edit=findViewById(R.id.newJournal_amount_edit);
        newJournal_submit_button=findViewById(R.id.newJournal_submit_button);

        categoryNameList=getCategoryNameList();
        categoryNameAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryNameList);
        categoryNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newJournal_category_spinner.setAdapter(categoryNameAdapter);
        newJournal_category_spinner.setSelection(0);
        newJournal_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newJournal_category_spinner_result=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        newJournal_timePicker_text.setText(simpleDateFormat.format(new Date()));
        newJournal_timePicker_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddJournalActivity.this);
                View view = View.inflate(AddJournalActivity.this, R.layout.date_time_dialog, null);
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
                builder.setView(view);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
                timePicker.setIs24HourView(true);
                timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));

                if (v.getId()== R.id.newJournal_timePicker_text){
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mYear=datePicker.getYear();
                            mMonth=datePicker.getMonth()+1;
                            mDay=datePicker.getDayOfMonth();
                            mHour=timePicker.getCurrentHour();
                            mMinute=timePicker.getCurrentMinute();
                            String s=mYear+"-"+mMonth+"-"+mDay+" "+mHour+":"+mMinute;
                            newJournal_timePicker_text.setText(s);
                            dialog.cancel();
                        }
                    });
                }
                Dialog dialog=builder.create();
                dialog.show();
            }
        });

        final String[] typeList = new String[]{"支出", "收入"};
        typeAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newJournal_type_spinner.setAdapter(typeAdapter);
        newJournal_type_spinner.setSelection(0);
        newJournal_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newJournal_type_spinner_result=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        newJournal_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newJournal_amount_edit.getText().equals("")){
                    Toast.makeText(AddJournalActivity.this,"账单金额未输入",Toast.LENGTH_SHORT).show();
                }else {
                    Journal journal=new Journal();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddJournalActivity.this);
                    int bookId = preferences.getInt("bookId", 0);
                    journal.setBookId(bookId);
                    journal.setCategoryId(DatabaseTools.findCategoryIdByName(newJournal_category_spinner_result));
                    try {
                        journal.setDate(simpleDateFormat.parse(newJournal_timePicker_text.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    journal.setInfo(newJournal_info_edit.getText().toString());
                    if (newJournal_type_spinner_result.equals("支出")){
                        journal.setType(1);
                    }else {
                        journal.setType(2);}
                    journal.setAmount((int) Double.parseDouble(newJournal_amount_edit.getText().toString()));
                    journal.save();
                    Util.sysJournal();
                    Intent intent=new Intent(AddJournalActivity.this, BookActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
    //获取categoryName列表
    private List<String> getCategoryNameList(){
        List<String> stringList=new ArrayList<>();
        List<Category> categoryList= DataSupport.findAll(Category.class);
        for (Category category:categoryList){
            stringList.add(category.getName());
        }
        return  stringList;
    }
}
