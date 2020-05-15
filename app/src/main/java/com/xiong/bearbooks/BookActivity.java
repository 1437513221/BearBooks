package com.xiong.bearbooks;

import android.content.Context;
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


import com.google.android.material.navigation.NavigationView;
import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.db.Journal;
import com.xiong.bearbooks.util.Util;
import com.xiong.bearbooks.view.BookAdapter;
import com.xiong.bearbooks.view.JournalAdapter;

import org.litepal.crud.DataSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookActivity extends AppCompatActivity {
    private List<Book> bookList = new ArrayList<>();
    private List<Journal> journalList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Book book;
    private int spending_result;
    private LinearLayout total_linearLayout;
    private com.getbase.floatingactionbutton.FloatingActionButton fab_1, fab_2, fab_3;
    private TextView remaining_days;
    private NavigationView nav_view;
    private LinearLayout moreJournals_layout;
    private LinearLayout welcome_layout;
    private TextView welcome_text;
    private int loginState;

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
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        fab_1 = findViewById(R.id.fab_1);
        fab_2 = findViewById(R.id.fab_2);
        fab_3 = findViewById(R.id.fab_3);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
        fab_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });
        fab_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookActivity.this, AddJournalActivity.class);
                startActivity(intent);
            }
        });
        nav_view=findViewById(R.id.nav_view);
        nav_view.setCheckedItem(R.id.nav_addCategory);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.nav_addCategory:
                        Intent intent1=new Intent(BookActivity.this,AddCategoryActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_statements:
                        Intent intent2=new Intent(BookActivity.this,StatementActivity.class);
                        startActivity(intent2);
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        getBookList();
        getJournalList();
        getBook();
        BookAdapter adapter = new BookAdapter(BookActivity.this, R.layout.book_item, bookList);
        ListView book_listview = findViewById(R.id.book_listView);
        book_listview.setAdapter(adapter);
        TextView book_name_text2 = findViewById(R.id.book_name_text2);
        TextView total_text = findViewById(R.id.total_text);
        TextView spending_text = findViewById(R.id.spending_text);
        total_linearLayout = findViewById(R.id.total_linearLayout);
        remaining_days = findViewById(R.id.remaining_days);

        book_name_text2.setText(book.getName() + "预算");
        total_text.setText(String.valueOf((int) book.getAmount()));
        spending_text.setText(String.valueOf(spending_result));
        try {
            remaining_days.setText(String.valueOf(getRemainingDays(book.getCycle())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JournalAdapter journalAdapter = new JournalAdapter(BookActivity.this, R.layout.journal_item, journalList);
        ListView journal_listView = findViewById(R.id.journal_listView);
        journal_listView.setAdapter(journalAdapter);
        book_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = bookList.get(position);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(BookActivity.this).edit();
                editor.putInt("bookId", book.getBookId());
                editor.apply();
                Intent intent = new Intent(MyApplication.getContext(), BookActivity.class);
                startActivity(intent);
                finish();
            }
        });
        total_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookActivity.this, ModifyBookActivity.class);
                startActivity(intent);
            }
        });
        journal_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Journal journal = journalList.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("journalId", journal.getJournalId());
                Intent intent = new Intent(BookActivity.this, ModifyJournalActivity.class);
                intent.putExtras(bundle);
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(BookActivity.this).edit();
                editor.putInt("updateJournalId",1);
                editor.apply();
                startActivity(intent);
            }
        });

        moreJournals_layout=findViewById(R.id.moreJournals_layout);
        moreJournals_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BookActivity.this,MoreJournalsActivity.class);
                startActivity(intent);
            }
        });

        welcome_layout=findViewById(R.id.welcome_layout);
        welcome_text=findViewById(R.id.welcome_text);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BookActivity.this);
        loginState=preferences.getInt("loginState",0);
        if (loginState==0){
            welcome_text.setText("用户未登录，点击登陆");
        }
        welcome_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //获取Book列表充适配器
    public void getBookList() {
        bookList = DataSupport.findAll(Book.class);
    }

    //获取Journal列表充适配器
    public void getJournalList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        int bookId = preferences.getInt("bookId", 0);
        List<Journal> list = DataSupport.where("bookId=?", String.valueOf(bookId)).order("date desc").find(Journal.class);
        if (list.size()>=10){
            for(int i = 0; i < 10; i++){
                journalList.add(list.get(i));
            }
        }else {
            for (Journal journal:list){
                journalList.add(journal);
            }
        }

    }

    //
    private void getBook() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int bookId = preferences.getInt("bookId", 1);
        book = DataSupport.find(Book.class,bookId);
        spending_result = DataSupport.where("bookId=? and type=?", String.valueOf(bookId), "1").sum(Journal.class, "amount", Integer.class);
    }





    //计算周期结束日期
    private long getRemainingDays(int cycle) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (cycle==1){
            Calendar calendar1=Calendar.getInstance();
            calendar1.setTime(new Date());
            int day=calendar1.get(Calendar.DATE);
            int daysOfMonth=calendar1.getActualMaximum(Calendar.DAY_OF_MONTH);
            return daysOfMonth-day;
        }else if (cycle==2){
            Calendar calendar1=Calendar.getInstance();
            calendar1.setTime(new Date());
            int day=calendar1.get(Calendar.DAY_OF_YEAR);
            int daysOfMonth=calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
            return daysOfMonth-day;
        }else {
            return 0;
        }

    }
}