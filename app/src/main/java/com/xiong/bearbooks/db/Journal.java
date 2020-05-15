package com.xiong.bearbooks.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.Date;

public class Journal extends DataSupport {
    @Column(defaultValue = "0")
    private int journalId;

    @Column(nullable = false)
    private int  categoryId;

    private int amount;

    @Column(nullable = false)
    private int bookId;//不同的id不同的账本 从 1 开始

    private String info;//备注信息

    private Date date;

    private int type;//1是支出，2表示收入

    public int getJournalId() {
        return journalId;
    }

    public void setJournalId(int journalId) {
        this.journalId = journalId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
