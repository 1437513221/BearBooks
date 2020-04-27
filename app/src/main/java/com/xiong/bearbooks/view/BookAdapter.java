package com.xiong.bearbooks.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xiong.bearbooks.R;
import com.xiong.bearbooks.db.Book;
import com.xiong.bearbooks.util.Tools;

import java.util.List;

/**
 * Created by ThinkPad E450 on 2020/4/9.
 */

public class BookAdapter extends ArrayAdapter<Book> {
    private  int resourceId;

    public BookAdapter(Context context, int textViewResourceId, List<Book> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position,  View convertView,ViewGroup parent) {
        Book book = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView book_name_text= (TextView)view.findViewById(R.id.book_name_text);
        TextView book_amount_text=(TextView)view.findViewById(R.id.book_amount_text);
        TextView book_remaining_text=(TextView)view.findViewById(R.id.book_remaining_text);
        book_name_text.setText(book.getName());
        book_amount_text.setText(String.valueOf((int)book.getAmount()));
        book_remaining_text.setText(Tools.findRemainingAmount(book.getBookId()));
        return view;
    }

}
