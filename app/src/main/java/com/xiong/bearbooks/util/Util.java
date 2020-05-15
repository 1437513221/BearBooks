package com.xiong.bearbooks.util;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.xiong.bearbooks.MyApplication;
import com.xiong.bearbooks.db.MyDatabaseHelper;

import java.text.ParseException;

/**
 * Created by ThinkPad E450 on 2020/4/11.
 */

public class Util {
    //初始化数据
    public static void initialize() throws ParseException {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        boolean initialize=preferences.getBoolean("initialize",false);
        if (initialize==false){
            Tools.addBook("生活", 1000, false, 1,"2020-03-27");
            Tools.addBook("旅游", 10000, false, 2,"2020-04-15");
            Tools.addBook("结婚", 30000, false, 3,"2020-04-20");

            Tools.addCategory("衣服", 1, false);
            Tools.addCategory("餐饮", 2, false);
            Tools.addCategory("住宿", 3, false);
            Tools.addCategory("交通", 4, false);
            Tools.addCategory("娱乐", 5, false);
            Tools.addCategory("购物", 6, false);

            try {
                Tools.addJournal(1, 200, 1, "买鞋子", "2020-3-10 09:20", 1);
                Tools.addJournal( 2,50, 2, "吃海鲜", "2020-3-20 10:20", 1);
                Tools.addJournal(3, 10, 2, "坐火车", "2020-3-21 13:10", 1);
                Tools.addJournal(6, 120, 3, "买衣服", "2020-4-10 09:20", 1);
                Tools.addJournal(5, 2000, 1, "换手机", "2020-4-10 08:20", 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
            editor.putBoolean("initialize",true);
            editor.apply();
        }
    }
    //同步数据库信息
    public static void synBook(){
         MyDatabaseHelper dbHelper;
        dbHelper=new MyDatabaseHelper(MyApplication.getContext(),"BearBooks.db",null,2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update book set bookid=id");

    }
    public static void sysJournal(){
        MyDatabaseHelper dbHelper;
        dbHelper=new MyDatabaseHelper(MyApplication.getContext(),"BearBooks.db",null,2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update journal set journalid=id");

    }
    public static void sysCategory(){
        MyDatabaseHelper dbHelper;
        dbHelper=new MyDatabaseHelper(MyApplication.getContext(),"BearBooks.db",null,2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update category set categoryid=id");
    }

}
