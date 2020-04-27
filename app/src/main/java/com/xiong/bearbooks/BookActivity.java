package com.xiong.bearbooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.db.Journal;
import com.xiong.bearbooks.util.Util;
import com.xiong.bearbooks.view.BookAdapter;
import com.xiong.bearbooks.view.JournalAdapter;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity {
    private List<Book> bookList = new ArrayList<>();
    private List<Journal> journalList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Book book;
    private double spending_result;
    private LinearLayout total_linearLayout;
    private com.getbase.floatingactionbutton.FloatingActionButton fab_1,fab_2,fab_3;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        try {
            Util.initialize();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        fab_1=(com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.fab_1);
        fab_2=(com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.fab_2);
        fab_3=(com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.fab_3);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        fab_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BookActivity.this,AddBookActivity.class);
                startActivity(intent);
            }
        });
        fab_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BookActivity.this,AddJournalActivity.class);
                startActivity(intent);
            }
        });

        getBookList();
        getJournalList();
        getBook();
        BookAdapter adapter = new BookAdapter(BookActivity.this, R.layout.book_item, bookList);
        ListView book_listview = (ListView) findViewById(R.id.book_listView);
        book_listview.setAdapter(adapter);
        TextView book_name_text2 = (TextView) findViewById(R.id.book_name_text2);
        TextView total_text = (TextView) findViewById(R.id.total_text);

        TextView spending_text = (TextView) findViewById(R.id.spending_text);
        total_linearLayout= (LinearLayout) findViewById(R.id.total_linearLayout);
        book_name_text2.setText(book.getName() + "预算");
        total_text.setText(String.valueOf((int) book.getAmount()));
        spending_text.setText(String.valueOf(spending_result));
        JournalAdapter journalAdapter=new JournalAdapter(BookActivity.this, R.layout.journal_item, journalList);
        ListView journal_listView = (ListView) findViewById(R.id.journal_listView);
        journal_listView.setAdapter(journalAdapter);
        book_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book=bookList.get(position);
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(BookActivity.this).edit();
                editor.putInt("bookId",book.getBookId());
                editor.apply();
                Intent intent=new Intent(MyApplication.getContext(), BookActivity.class);
                startActivity(intent);
                finish();
            }
        });
        total_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BookActivity.this, ModifyBookActivity.class);
                startActivity(intent);
            }
        });
        journal_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Journal journal=journalList.get(position);
                Bundle bundle=new Bundle();
                bundle.putInt("journalId", journal.getJournalId());
                Intent intent=new Intent(BookActivity.this,ModifyJournalActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    //
    private void getBook() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int bookId = preferences.getInt("bookId", 0);
        book = DataSupport.where("id=?", String.valueOf(bookId)).find(Book.class).get(0);
        spending_result = DataSupport.where("bookId=? and type=?", String.valueOf(bookId), "1").sum(Journal.class, "amount", double.class);
    }
    //获取Book列表充适配器
    private void getBookList() {
        bookList = (List<Book>) DataSupport.findAll(Book.class);
    }
    //获取Journal列表充适配器
    private void getJournalList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int bookId = preferences.getInt("bookId", 0);
        journalList = (List<Journal>) DataSupport.where("bookId=?", String.valueOf(bookId)).order("date desc").find(Journal.class);
    }

}