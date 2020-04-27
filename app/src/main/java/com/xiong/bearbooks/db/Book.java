package com.xiong.bearbooks.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class Book extends DataSupport {
    @Column(defaultValue = "0")
    private int bookId;

    @Column(unique = true)
    private String name;

    private double amount;

    private boolean canDel;
    private int cycle;//计算周期 从1开始 1是月 2是年 3是一次性

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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
