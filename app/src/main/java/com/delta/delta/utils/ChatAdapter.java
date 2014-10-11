package com.delta.delta.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.delta.delta.R;

import java.util.ArrayList;

/**
 * Created by sathyam on 10/11/14.
 */
public class ChatAdapter extends ArrayAdapter<ChatMessage> {
    private final Context context;
    private ArrayList<ChatMessage> values;

    public ChatAdapter(Context context, ArrayList<ChatMessage> values) {
        super(context, R.layout.row_layout_left, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if (position % 2 == 0) {
            rowView = inflater.inflate(R.layout.row_layout_left, parent, false);
        } else {
            rowView = inflater.inflate(R.layout.row_layout_right, parent, false);
        }
        TextView textView = (TextView) rowView.findViewById(R.id.chat_row);
        textView.setText(values.get(position).message);
        return rowView;
    }

    @Override
    public void add(ChatMessage value) {
        values.add(value);
    }
}
