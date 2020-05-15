package com.xiong.bearbooks.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xiong.bearbooks.MyApplication;
import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.db.Category;
import com.xiong.bearbooks.db.Journal;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad E450 on 2020/4/20.
 */

public class DatabaseTools {

    public static String findBookNameById(int bookId){
        Book book;
        book=DataSupport.find(Book.class,bookId);
        return  book.getName();
    }

    public static int findBookIdByName(String bookName){
        Book book;
        book=DataSupport.where("name=?",bookName).findFirst(Book.class);
        return  book.getBookId();
    }

    public static String findCategoryNameById(int categoryId){
        Category category;
        category=DataSupport.find(Category.class,categoryId);
        return  category.getName();
    }

    public static int findCategoryIdByName(String categoryName){
        Category category;
        category=DataSupport.where("name=?",categoryName).findFirst(Category.class);
        return  category.getCategoryId();
    }

    //计算该周期所花费

}
