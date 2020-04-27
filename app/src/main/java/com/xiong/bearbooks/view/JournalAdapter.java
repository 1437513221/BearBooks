package com.xiong.bearbooks.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xiong.bearbooks.R;
import com.xiong.bearbooks.db.Journal;
import com.xiong.bearbooks.util.DatabaseTools;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by ThinkPad E450 on 2020/4/17.
 */

public class JournalAdapter extends ArrayAdapter<Journal> {
    private int resourceId;

    public JournalAdapter(Context context, int textViewResourceId, List<Journal> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position,  View convertView,ViewGroup parent) {
        Journal journal=getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView category_text=(TextView) view.findViewById(R.id.category_text);
        TextView journal_date_text=(TextView)view.findViewById(R.id.journal_date_text);
        TextView journal_info_text=(TextView)view.findViewById(R.id.journal_info_text);
        TextView amount_text=(TextView)view.findViewById(R.id.amount_text);
        TextView type_text=(TextView)view.findViewById(R.id.type_text);
        category_text.setText(DatabaseTools.findCategoryNameById(journal.getCategoryId()));
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");
        journal_date_text.setText(simpleDateFormat.format(journal.getDate()));
        journal_info_text.setText(journal.getInfo());
        if (journal.getType()==1){
            type_text.setText("-");
        }else {
            type_text.setText("+");
        }
        amount_text.setText(String.valueOf(journal.getAmount()));
        return view;
    }
}
