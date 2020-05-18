package com.xiong.bearbooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class AddJournalPlusActivity extends AppCompatActivity {
    private Spinner newJournalPlus_category_spinner;
    private String newJournalPlus_category_spinner_result;
    private EditText newJournalPlus_time_edit;
    private EditText newJournalPlus_info_edit;
    private Spinner newJournalPlus_type_spinner;
    private String newJournalPlus_type_spinner_result;
    private EditText newJournalPlus_amount_edit;
    private Button newJournalPlus_submit_button;
    private ListView wordsListView;
    private List<String> words=new ArrayList<>();
    private List<String> categoryNameList=new ArrayList<>();
    private ArrayAdapter<String> categoryNameAdapter;
    private ArrayAdapter<String> typeAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal_plus);
        newJournalPlus_category_spinner=findViewById(R.id.newJournalPlus_category_spinner);
        newJournalPlus_time_edit=findViewById(R.id.newJournalPlus_time_edit);
        newJournalPlus_info_edit=findViewById(R.id.newJournalPlus_info_edit);
        newJournalPlus_type_spinner=findViewById(R.id.newJournalPlus_type_spinner);
        newJournalPlus_amount_edit=findViewById(R.id.newJournalPlus_amount_edit);
        newJournalPlus_submit_button=findViewById(R.id.newJournalPlus_submit_button);
        wordsListView=findViewById(R.id.wordsListView);

        Bundle bundle=this.getIntent().getExtras();
         words=bundle.getStringArrayList("words");

        categoryNameList=getCategoryNameList();
        categoryNameAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryNameList);
        categoryNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newJournalPlus_category_spinner.setAdapter(categoryNameAdapter);
        newJournalPlus_category_spinner.setSelection(0);
        newJournalPlus_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newJournalPlus_category_spinner_result=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final String[] typeList = new String[]{"支出", "收入"};
        typeAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newJournalPlus_type_spinner.setAdapter(typeAdapter);
        newJournalPlus_type_spinner.setSelection(0);
        newJournalPlus_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newJournalPlus_type_spinner_result=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMM-ddHH:mm");

        newJournalPlus_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newJournalPlus_amount_edit.getText().toString().equals("")){
                    Toast.makeText(AddJournalPlusActivity.this,"账单金额未输入",Toast.LENGTH_SHORT).show();
                }else if(newJournalPlus_time_edit.getText().toString().equals("")){
                    Toast.makeText(AddJournalPlusActivity.this,"记账时间未输入",Toast.LENGTH_SHORT).show();
                }else{
                    Journal journal=new Journal();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddJournalPlusActivity.this);
                    int bookId = preferences.getInt("bookId", 0);
                    journal.setBookId(bookId);
                    journal.setCategoryId(DatabaseTools.findCategoryIdByName(newJournalPlus_category_spinner_result));
                    try {
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTime(new Date());
                        journal.setDate(simpleDateFormat.parse(calendar.get(Calendar.YEAR)+newJournalPlus_time_edit.getText().toString()));
                        Log.d("Parse", calendar.get(Calendar.YEAR)+newJournalPlus_time_edit.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    journal.setInfo(newJournalPlus_info_edit.getText().toString());
                    if (newJournalPlus_type_spinner_result.equals("支出")){
                        journal.setType(1);
                    }else {
                        journal.setType(2);}
                    journal.setAmount((int) Double.parseDouble(newJournalPlus_amount_edit.getText().toString()));
                    journal.save();
                    Util.sysJournal();
                    Toast.makeText(AddJournalPlusActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                    newJournalPlus_amount_edit.setText("");
                    newJournalPlus_time_edit.setText("");
                    newJournalPlus_info_edit.setText("");
                }

            }
        });

         ArrayAdapter<String> adapter=new ArrayAdapter<String>(AddJournalPlusActivity.this,android.R.layout.simple_list_item_1,words);
         wordsListView.setAdapter(adapter);
         wordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 String word=words.get(position);
                 if (newJournalPlus_time_edit.isFocused()){
                     newJournalPlus_time_edit.setText(word);
                 }else if (newJournalPlus_info_edit.isFocused()){
                     newJournalPlus_info_edit.setText(word);
                 }else if (newJournalPlus_amount_edit.isFocused()){
                     newJournalPlus_amount_edit.setText(word);
                 }else {}
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
