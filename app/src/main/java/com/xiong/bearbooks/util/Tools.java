package com.xiong.bearbooks.util;

import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.db.Category;
import com.xiong.bearbooks.db.Journal;


import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * Created by ThinkPad E450 on 2020/4/9.
 */

public class Tools {
    //添加账本数据
    public static void addBook(String name,int amount,boolean canDel,int cycle,String date){
        Book book=new Book();
        book.setName(name);
        book.setAmount(amount);
        book.setCanDel(canDel);
        book.setCycle(cycle);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try {
            book.setSetCycleDate(simpleDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        book.save();
    }

    //添加分类数据
    public static void addCategory(String name,int icon,boolean canDel){
        Category category=new Category();
        category.setName(name);
        category.setIcon(icon);
        category.setCanDel(canDel);
        category.save();
    }

    //添加流水账数据
    public static void addJournal(int categoryId, int amount, int bookId, String info, String date,int type) throws ParseException {
        Journal journal=new Journal();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");//按照格式传入String类型数据，不然添加不进去
        journal.setCategoryId(categoryId);
        journal.setAmount(amount);
        journal.setBookId(bookId);
        journal.setInfo(info);
        journal.setDate(simpleDateFormat.parse(date));
        journal.setType(type);
        journal.save();
    }

    //根据bookId查找剩余额度
    public static String findRemainingAmount(int bookId){
        int result= DataSupport.where("bookId=? and type=?", String.valueOf(bookId), "1").sum(Journal.class,"amount",Integer.class);
        int result2=DataSupport.where("bookId=?", String.valueOf(bookId)).sum(Book.class,"amount",Integer.class);
        return String.valueOf(result2-result);
    }
}
