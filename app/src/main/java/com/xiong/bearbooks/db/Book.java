package com.xiong.bearbooks.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.Date;

public class Book extends DataSupport {
    @Column(defaultValue = "0")
    private int bookId;

    @Column(unique = true)
    private String name;

    private int amount;

    private boolean canDel;

    private Date setCycleDate;

    private int cycle;//计算周期 从1开始 1是月 2是年 3是一次性

    public Date getSetCycleDate() {
        return setCycleDate;
    }

    public void setSetCycleDate(Date setCycleDate) {
        this.setCycleDate = setCycleDate;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isCanDel() {
        return canDel;
    }

    public void setCanDel(boolean canDel) {
        this.canDel = canDel;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }
}
