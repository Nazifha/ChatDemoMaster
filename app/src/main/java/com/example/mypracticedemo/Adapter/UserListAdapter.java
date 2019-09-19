package com.example.mypracticedemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mypracticedemo.R;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

    private ArrayList<QBUser> qbUserArrayList;
    private Context context;

    public UserListAdapter(ArrayList<QBUser> qbUserArrayList, Context context) {
        this.qbUserArrayList = qbUserArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return qbUserArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return qbUserArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_multiple_choice,null);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(qbUserArrayList.get(position).getLogin());
        }
        return view;
    }
}
