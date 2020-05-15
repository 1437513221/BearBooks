package com.xiong.bearbooks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xiong.bearbooks.db.Category;
import com.xiong.bearbooks.util.DatabaseTools;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatementActivity extends AppCompatActivity {
    private TextView bookNameOfStatement;
    private TextView startTimeOfPieChart_text;
    private TextView endTimeOfPieChart_text;
    private TextView yearOfLineChart_text;
    private TextView monthOfLineChart_text;
    private TextView categoryOfLineChart_text;
    private Button drawPieChart_button;
    private Button drawLineChart_button;

    private ArrayAdapter<String> yearOfLineChartAdapter;
    private ArrayAdapter<String> monthOfLineChartAdapter;
    private ArrayAdapter<String> categoryOfLineChartAdapter;

    private List<String> yearList= new ArrayList<>();
    private List<String> monthList= new ArrayList<>();
    private List<String> categoryList= new ArrayList<>();



    private int bookId;
    private int mYear,mMonth,mDay;
    private String spinner_result;

    public TextView getStartTimeOfPieChart_text() {
        return startTimeOfPieChart_text;
    }

    public TextView getEndTimeOfPieChart_text() {
        return endTimeOfPieChart_text;
    }

    public TextView getYearOfLineChart_text() {
        return yearOfLineChart_text;
    }

    public TextView getMonthOfLineChart_text() {
        return monthOfLineChart_text;
    }

    public TextView getCategoryOfLineChart_text() {
        return categoryOfLineChart_text;
    }

    public int getBookId() {
        return bookId;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        startTimeOfPieChart_text=findViewById(R.id.startTimeOfPieChart_text);
        endTimeOfPieChart_text=findViewById(R.id.endTimeOfPieChart_text);
        drawPieChart_button=findViewById(R.id.drawPieChart_button);

        yearOfLineChart_text=findViewById(R.id.yearOfLineChart_text);
        monthOfLineChart_text=findViewById(R.id.monthOfLineChart_text);
        categoryOfLineChart_text=findViewById(R.id.categoryOfLineChart_text);
        drawLineChart_button=findViewById(R.id.drawLineChart_button);

        initYearList(yearList);
        yearOfLineChartAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearList);
        initMonthList(monthList);
        monthOfLineChartAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthList);
        getCategoryList(categoryList);
        categoryOfLineChartAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryList);


        final Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());

        startTimeOfPieChart_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StatementActivity.this);
                View view = View.inflate(StatementActivity.this, R.layout.date_dialog, null);
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_pieChart_picker);
                builder.setView(view);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

                if (v.getId()== R.id.startTimeOfPieChart_text){
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mYear=datePicker.getYear();
                            mMonth=datePicker.getMonth()+1;
                            mDay=datePicker.getDayOfMonth();
                            String s=mYear+"-"+mMonth+"-"+mDay;
                            startTimeOfPieChart_text.setText(s);
                            dialog.cancel();
                        }
                    });
                }
                Dialog dialog=builder.create();
                dialog.show();
            }
        });

        endTimeOfPieChart_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StatementActivity.this);
                View view = View.inflate(StatementActivity.this, R.layout.date_dialog, null);
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_pieChart_picker);
                builder.setView(view);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

                if (v.getId()== R.id.endTimeOfPieChart_text){
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mYear=datePicker.getYear();
                            mMonth=datePicker.getMonth()+1;
                            mDay=datePicker.getDayOfMonth();
                            String s=mYear+"-"+mMonth+"-"+mDay;
                            endTimeOfPieChart_text.setText(s);
                            dialog.cancel();
                        }
                    });
                }
                Dialog dialog=builder.create();
                dialog.show();
            }
        });

        drawPieChart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replacePieChartFragment(new PieChart_fragment());
            }
        });
//折线图获取年份
        yearOfLineChart_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StatementActivity.this);
                View view = View.inflate(StatementActivity.this, R.layout.spinner_dialog, null);
                final Spinner spinner = view.findViewById(R.id.dialog_spinner);
                spinner.setAdapter(yearOfLineChartAdapter);
                builder.setView(view);
                spinner.setSelection((calendar.get(Calendar.YEAR)-2020));
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinner_result=parent.getItemAtPosition(position).toString();
                }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if (v.getId()== R.id.yearOfLineChart_text){
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String s=spinner_result;
                            yearOfLineChart_text.setText(s);
                            dialog.cancel();
                        }
                    });
                }
                Dialog dialog=builder.create();
                dialog.show();
                dialog.getWindow().setLayout(300, 180);
            }
        });
        //折线图获取月份
        monthOfLineChart_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StatementActivity.this);
                View view = View.inflate(StatementActivity.this, R.layout.spinner_dialog, null);
                final Spinner spinner = view.findViewById(R.id.dialog_spinner);
                spinner.setAdapter(monthOfLineChartAdapter);
                spinner.setSelection((calendar.get(Calendar.MONTH)));
                builder.setView(view);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinner_result=parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if (v.getId()== R.id.monthOfLineChart_text){
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String s=spinner_result;
                            monthOfLineChart_text.setText(s);
                            dialog.cancel();
                        }
                    });
                }
                Dialog dialog=builder.create();
                dialog.show();
                dialog.getWindow().setLayout(300, 180);
            }
        });

        //折线图获取种类
        categoryOfLineChart_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StatementActivity.this);
                View view = View.inflate(StatementActivity.this, R.layout.spinner_dialog, null);
                final Spinner spinner = view.findViewById(R.id.dialog_spinner);
                spinner.setAdapter(categoryOfLineChartAdapter);
                builder.setView(view);
                spinner.setSelection(0);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinner_result=parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if (v.getId()== R.id.categoryOfLineChart_text){
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String s=spinner_result;
                            categoryOfLineChart_text.setText(s);
                            dialog.cancel();
                        }
                    });
                }
                Dialog dialog=builder.create();
                dialog.show();
                dialog.getWindow().setLayout(300, 180);
            }
        });

        drawLineChart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceLineChartFragment(new LineChart_fragment());
            }
        });

        bookNameOfStatement=findViewById(R.id.bookNameOfStatement);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        bookId = preferences.getInt("bookId", 1);
        String bookName=DatabaseTools.findBookNameById(bookId);
        bookNameOfStatement.setText(bookName+"账本报表");
        replacePieChartFragment(new PieChart_fragment());
        replaceLineChartFragment(new LineChart_fragment());

    }



    private void replacePieChartFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.pieChartFrameLayout,fragment);
        transaction.commit();
    }

    private void replaceLineChartFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.lineChartFrameLayout,fragment);
        transaction.commit();
    }

    private void initYearList(List<String> list){
        for (int i=2020;i<2030;i++){
            list.add(String.valueOf(i));
        }
    }

    private void initMonthList(List<String> monthList){
        for (int i=1;i<13;i++){
            monthList.add(String.valueOf(i));
        }
    }

    private void getCategoryList(List<String> list){
        List<Category> categoryList= DataSupport.findAll(Category.class);
        for (Category category:categoryList){
            list.add(category.getName());
        }
    }


}
