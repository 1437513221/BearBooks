package com.xiong.bearbooks.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xiong.bearbooks.R;
import com.xiong.bearbooks.db.Category;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private  int resourceId;

    public CategoryAdapter(Context context, int textViewResourceId, List<Category> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView categoryName_text= (TextView)view.findViewById(R.id.categoryName_text);
        categoryName_text.setText(category.getName());
        return view;
    }
}
