package com.xiong.bearbooks.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class Category extends DataSupport {
    @Column(defaultValue = "0")
    private int categoryId;

    private String name;

    private int icon;

    private boolean canDel;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isCanDel() {
        return canDel;
    }

    public void setCanDel(boolean canDel) {
        this.canDel = canDel;
    }
}
