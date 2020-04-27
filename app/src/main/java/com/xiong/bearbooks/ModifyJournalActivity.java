package com.xiong.bearbooks;



import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.xiong.bearbooks.db.Category;
import com.xiong.bearbooks.db.Journal;
import com.xiong.bearbooks.util.DatabaseTools;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ModifyJournalActivity extends AppCompatActivity {
    private int journalId;
    private Journal journal;
    private Spinner modify_category_spinner;
    private TextView modify_timePicker_text;
    private EditText modify_info_edit;
    private Spinner modify_type_spinner;
    private EditText modify_amount_edit;
    private List<String> categoryNameList=new ArrayList<>();
    private ArrayAdapter<String> categoryNameAdapter;
    private ArrayAdapter<String> typeAdapter;
    private Button modify_submit_button;
    private Toolbar modify_journal_toolbar;

    private int mYear,mMonth,mDay,mHour,mMinute;
    String modify_category_spinner_result;
    String modify_type_spinner_result;

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
        setContentView(R.layout.activity_modify_journal);
        Bundle bundle=this.getIntent().getExtras();
        journalId=bundle.getInt("journalId");
        journal=getJournal(journalId);
        modify_journal_toolbar= (Toolbar) findViewById(R.id.modify_journal_toolbar);
        modify_category_spinner= (Spinner) findViewById(R.id.newJournal_category_spinner);
        modify_timePicker_text= (TextView) findViewById(R.id.newJournal_timePicker_text);
        modify_info_edit= (EditText) findViewById(R.id.newJournal_info_edit);
        modify_type_spinner= (Spinner) findViewById(R.id.newJournal_type_spinner);
        modify_amount_edit= (EditText) findViewById(R.id.newJournal_amount_edit);
        modify_submit_button= (Button) findViewById(R.id.newJournal_submit_button);
        setSupportActionBar(modify_journal_toolbar);



        categoryNameList=getCategoryNameList();
        categoryNameAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryNameList);
        categoryNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modify_category_spinner.setAdapter(categoryNameAdapter);
        modify_category_spinner.setSelection(journal.getCategoryId()-1);
        modify_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modify_category_spinner_result=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        modify_timePicker_text.setText(simpleDateFormat.format(journal.getDate()));
        modify_timePicker_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyJournalActivity.this);
                View view = View.inflate(ModifyJournalActivity.this, R.layout.date_time_dialog, null);
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
                builder.setView(view);

                Calendar cal = Calendar.getInstance();
                cal.setTime(journal.getDate());
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
                            modify_timePicker_text.setText(s);
                            dialog.cancel();
                        }
                    });
                }
                Dialog dialog=builder.create();
                dialog.show();
            }
        });

        modify_info_edit.setText(journal.getInfo());

        final String[] typeList = new String[]{"支出", "收入"};
        typeAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modify_type_spinner.setAdapter(typeAdapter);
        modify_type_spinner.setSelection(journal.getType()-1);
        modify_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modify_type_spinner_result=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        modify_amount_edit.setText(String.valueOf(journal.getAmount()));

        modify_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modify_amount_edit.getText().equals("")){
                    Toast.makeText(ModifyJournalActivity.this,"账单金额未输入",Toast.LENGTH_SHORT).show();
                }else {
                    Journal journal=new Journal();
                    journal.setCategoryId(DatabaseTools.findCategoryIdByName(modify_category_spinner_result));
                    try {
                        journal.setDate(simpleDateFormat.parse(modify_timePicker_text.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    journal.setInfo(modify_info_edit.getText().toString());
                    if (modify_type_spinner_result.equals("支出")){
                        journal.setType(1);
                    }else {
                        journal.setType(2);}
                    journal.setAmount(Double.parseDouble(modify_amount_edit.getText().toString()));
                    journal.updateAll("id=?", String.valueOf(journalId));
                    Intent intent =new Intent(ModifyJournalActivity.this, BookActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    //获取journal的信息
   private Journal getJournal(int journalId){
        Journal journal= DataSupport.where("journalId=?", String.valueOf(journalId)).find(Journal.class).get(0);
       return journal;
    }

    //获取categoryName列表
    private List<String> getCategoryNameList(){
        List<String> stringList=new ArrayList<>();
        List<Category> categoryList=DataSupport.findAll(Category.class);
        for (Category category:categoryList){
            stringList.add(category.getName());
        }
        return  stringList;
    }

    private void showWarnDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("确定删除吗？").setMessage("删除之后不可恢复哦")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataSupport.delete(Journal.class,journalId);
                        Intent intent=new Intent(ModifyJournalActivity.this, BookActivity.class);
                        startActivity(intent);
                        Toast.makeText(ModifyJournalActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
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
