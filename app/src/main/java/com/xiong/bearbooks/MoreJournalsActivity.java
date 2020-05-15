package com.xiong.bearbooks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.db.Category;
import com.xiong.bearbooks.db.Journal;
import com.xiong.bearbooks.util.DatabaseTools;
import com.xiong.bearbooks.view.JournalAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MoreJournalsActivity extends AppCompatActivity {
    private TextView moreJournals_bookName_text;
    private ListView moreJournals_ListView;
    private FloatingActionButton search_journal_fab;
    private List<Journal> journalList = new ArrayList<>();
    private  String bookName;
    private List<String> categoryNameList=new ArrayList<>();
    private ArrayAdapter<String> categoryNameAdapter;
    String Journal_category_spinner_result;
    private int beginAmount;
    private int endAmount;
    private String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_journals);
        moreJournals_bookName_text=findViewById(R.id.moreJournals_bookName_text);
        moreJournals_ListView=findViewById(R.id.moreJournals_ListView);
        search_journal_fab=findViewById(R.id.search_journal_fab);

        getBookName();
        moreJournals_bookName_text.setText("以下为"+bookName+"账本的所有流水账记录");


        getJournalList();
        JournalAdapter journalAdapter = new JournalAdapter(MoreJournalsActivity.this, R.layout.journal_item, journalList);
        moreJournals_ListView.setAdapter(journalAdapter);
        moreJournals_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Journal journal = journalList.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("journalId", journal.getJournalId());
                Intent intent = new Intent(MoreJournalsActivity.this, ModifyJournalActivity.class);
                intent.putExtras(bundle);
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MoreJournalsActivity.this).edit();
                editor.putInt("updateJournalId",2);
                editor.apply();
                startActivity(intent);
            }
        });

        categoryNameList=getCategoryNameList();
        categoryNameAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryNameList);
        categoryNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        search_journal_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MoreJournalsActivity.this);
                View view = View.inflate(MoreJournalsActivity.this, R.layout.search_journal_dialog, null);
                final Spinner search_category_spinner=view.findViewById(R.id.search_category_spinner);
                final EditText search_beginAmount_edit=view.findViewById(R.id.search_beginAmount_edit);
                final EditText search_endAmount_edit=view.findViewById(R.id.search_endAmount_edit);
                final EditText search_keyWord_edit=view.findViewById(R.id.search_keyWord_edit);
                builder.setView(view);

                search_category_spinner.setAdapter(categoryNameAdapter);
                search_category_spinner.setSelection(0);
                search_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Journal_category_spinner_result=parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                if (v.getId()== R.id.search_journal_fab){
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                            int bookId = preferences.getInt("bookId", 0);
                            int categoryId=DatabaseTools.findCategoryIdByName(Journal_category_spinner_result);
                            if (search_beginAmount_edit.getText().toString().equals("")){
                                beginAmount=0;
                            }else {beginAmount= Integer.parseInt(search_beginAmount_edit.getText().toString());}

                            if (search_endAmount_edit.getText().toString().equals("")){
                                endAmount=99999999;
                            }else {endAmount= Integer.parseInt(search_endAmount_edit.getText().toString());}
                            keyWord=search_keyWord_edit.getText().toString();

                            journalList=DataSupport
                                    .where("amount between ? and ? and categoryId=? and bookId=? and info like ?",String.valueOf(beginAmount),String.valueOf(endAmount),String.valueOf(categoryId),String.valueOf(bookId),"%"+keyWord+"%")
                                    .find(Journal.class);
                            JournalAdapter journalAdapter = new JournalAdapter(MoreJournalsActivity.this, R.layout.journal_item, journalList);
                            moreJournals_ListView.setAdapter(journalAdapter);
                            dialog.cancel();
                        }
                    });
                }
                Dialog dialog=builder.create();
                dialog.show();
            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(this, BookActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }
    //得到journal列表
    public void getJournalList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        int bookId = preferences.getInt("bookId", 0);
        journalList = DataSupport.where("bookId=?", String.valueOf(bookId)).order("date desc").find(Journal.class);
    }
    //得到bookName
    public void getBookName() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        int bookId = preferences.getInt("bookId", 0);
        bookName = DataSupport.find(Book.class,bookId).getName();
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
