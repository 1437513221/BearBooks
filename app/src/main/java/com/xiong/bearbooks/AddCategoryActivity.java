package com.xiong.bearbooks;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xiong.bearbooks.db.Category;
import com.xiong.bearbooks.db.Journal;
import com.xiong.bearbooks.util.Util;
import com.xiong.bearbooks.view.CategoryAdapter;

import org.litepal.crud.DataSupport;

import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {

    private List<Category> categoryList;
    String categoryName;
    private FloatingActionButton fab_addCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        fab_addCategory=findViewById(R.id.fab_addCategory);

        getCategoryList();
        final CategoryAdapter adapter = new CategoryAdapter(AddCategoryActivity.this, R.layout.category_item, categoryList);
        ListView category_ListView = findViewById(R.id.category_ListView);
        category_ListView.setAdapter(adapter);

        category_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder=new AlertDialog.Builder(AddCategoryActivity.this);
                builder.setIcon(R.drawable.ic_delete_black_24dp).setTitle("删除种类").setMessage("你确定要删除吗？")
                        .setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int id=categoryList.get(position).getCategoryId();
                                boolean b=categoryList.get(position).isCanDel();
                                if (b==false){
                                    Toast.makeText(AddCategoryActivity.this,"默认的不能删除哦",Toast.LENGTH_SHORT).show();
                                }else {
                                    DataSupport.deleteAll(Journal.class,"categoryId=?",String.valueOf(id));
                                    DataSupport.delete(Category.class,id);
                                    Intent intent=new Intent(AddCategoryActivity.this,AddCategoryActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.cancel();
                                }
                            }
                        });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });

        fab_addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(AddCategoryActivity.this).setTitle("输入你要添加的类型名");
                View view = View.inflate(AddCategoryActivity.this, R.layout.add_category_dialog, null);
                final EditText newCategoryName_edit=view.findViewById(R.id.newCategoryName_edit);
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCategoryActivity.this).setTitle("输入你要添加的类型名").setView(view)
                        .setPositiveButton("确定添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (newCategoryName_edit.getText().toString().equals("")){
                                    Toast.makeText(MyApplication.getContext(),"未输入，请重新输入",Toast.LENGTH_SHORT).show();
                                }else if (RecheckCategoryName(newCategoryName_edit.getText().toString())==false){
                                    Toast.makeText(MyApplication.getContext(),"已存在，请重新输入",Toast.LENGTH_SHORT).show();
                                }else {
                                    Category category=new Category();
                                    category.setName(newCategoryName_edit.getText().toString());
                                    category.setCanDel(true);
                                    category.setIcon(1);
                                    category.save();
                                    Util.sysCategory();
                                    Intent intent=new Intent(MyApplication.getContext(),AddCategoryActivity.class);
                                    startActivity(intent);
                                    dialog.cancel();
                                }
                            }
                        });

                builder.create().show();
            }
        });
    }

    private void getCategoryList(){
        categoryList= DataSupport.findAll(Category.class);
    }

    //查重类型名
    private boolean RecheckCategoryName(String categoryName){
        List<Category> categoryList = DataSupport.findAll(Category.class);
        for (Category category : categoryList) {
            if (category.getName().equals(categoryName)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(this, BookActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
