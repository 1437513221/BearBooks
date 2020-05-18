package com.xiong.bearbooks.util;

import android.util.Log;

import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.db.Category;
import com.xiong.bearbooks.db.Journal;


import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


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
        Book book;
        String beginTime;
        String endTime;
        long beginTimeInMillis;
        long endTimeInMillis;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar1=Calendar.getInstance();
        Calendar calendar2=Calendar.getInstance();
        Calendar calendar3=Calendar.getInstance();
        book = DataSupport.find(Book.class,bookId);
        if(book.getCycle()==1){
            calendar1.setTime(new Date());
            beginTime=calendar1.get(Calendar.YEAR)+"-"+(calendar1.get(Calendar.MONTH)+1)+"-"+"1"+" "+"00"+":"+"00";
            if (calendar1.get(Calendar.MONTH)==11){
                endTime=(calendar1.get(Calendar.YEAR)+1)+"-"+"1"+"-"+"1"+" "+"00"+":"+"00";
            }else {endTime=calendar1.get(Calendar.YEAR)+"-"+(calendar1.get(Calendar.MONTH)+2)+"-"+"1"+" "+"00"+":"+"00";}
            try {
                calendar2.setTime(simpleDateFormat.parse(beginTime));
                calendar3.setTime(simpleDateFormat.parse(endTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if(book.getCycle()==2){
            calendar1.setTime(new Date());
            beginTime=calendar1.get(Calendar.YEAR)+"-"+"1"+"-"+"1"+" "+"00"+":"+"00";
            endTime=(calendar1.get(Calendar.YEAR)+1)+"-"+"1"+"-"+"1"+" "+"00"+":"+"00";
            try {
                calendar2.setTime(simpleDateFormat.parse(beginTime));
                calendar3.setTime(simpleDateFormat.parse(endTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            beginTime="2020-1-1 00:00";
            endTime="2029-1-1 00:00";
            try {
                calendar2.setTime(simpleDateFormat.parse(beginTime));
                calendar3.setTime(simpleDateFormat.parse(endTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Log.d("beginTime", beginTime);
        Log.d("endTime", endTime);

        beginTimeInMillis=calendar2.getTimeInMillis();
        endTimeInMillis=calendar3.getTimeInMillis();

        int result2 = DataSupport.where("bookId=? and type=? and date>? and date<?", String.valueOf(bookId), "1",String.valueOf(beginTimeInMillis),String.valueOf(endTimeInMillis)).sum(Journal.class, "amount", Integer.class);

        return String.valueOf(book.getAmount()-result2);
    }
}
