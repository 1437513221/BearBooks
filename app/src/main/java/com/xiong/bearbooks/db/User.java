package com.xiong.bearbooks.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class User extends DataSupport {

    @Column(defaultValue = "0")
    private int userId;

    private String userName;

    private String password;

    @Column(defaultValue = "1")
    private int sex;//1为男，2为女
}
