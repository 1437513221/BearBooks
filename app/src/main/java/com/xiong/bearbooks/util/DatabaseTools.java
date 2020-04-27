package com.xiong.bearbooks.util;

import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.db.Category;

import org.litepal.crud.DataSupport;

/**
 * Created by ThinkPad E450 on 2020/4/20.
 */

public class DatabaseTools {
    public static String findBookNameById(int bookId){
        Book book;
        book=DataSupport.find(Book.class,bookId);
        return  book.getName();
    }

    public static String findCategoryNameById(int categoryId){
        Category category;
        category=DataSupport.find(Category.class,categoryId);
        return  category.getName();
    }

    public static int findCategoryIdByName(String categoryName){
        Category category;
        category=DataSupport.where("name=?",categoryName).find(Category.class).get(0);
        return  category.getCategoryId();
    }
}
