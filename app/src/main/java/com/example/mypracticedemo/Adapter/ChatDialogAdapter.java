package com.example.mypracticedemo.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.mypracticedemo.Holder.QBUnreadMessageHolder;
import com.example.mypracticedemo.R;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;
import java.util.Random;

public class ChatDialogAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;

    public ChatDialogAdapter(Context context, ArrayList<QBChatDialog> qbChatDialogs) {
        this.context = context;
        this.qbChatDialogs = qbChatDialogs;
    }

    @Override
    public int getCount() {
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatDialogs.get(position);
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
            view = inflater.inflate(R.layout.item_chat_dialog,null);

            TextView txtTitle,txtMessage,count;
            ImageView img,unreadCount;

            txtTitle = view.findViewById(R.id.title);
            txtMessage = view.findViewById(R.id.message);

            img = view.findViewById(R.id.img);
            unreadCount = view.findViewById(R.id.unreadCount);

            txtMessage.setText(qbChatDialogs.get(position).getLastMessage());
            txtTitle.setText(qbChatDialogs.get(position).getName());

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomColor = generator.getRandomColor();

            TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();
            TextDrawable drawable = builder.build(txtTitle.getText().toString().substring(0,txtTitle.length()-txtTitle.length()+1).toUpperCase(),randomColor);
            img.setImageDrawable(drawable);

            //set message unread count

            unreadCount.setVisibility(View.GONE);

            int unread_count = QBUnreadMessageHolder.getInstance().getBundle().getInt(qbChatDialogs.get(position).getDialogId());
            if (unread_count > 0)
            {
                unreadCount.setVisibility(View.VISIBLE);

                TextDrawable.IBuilder unreadBuilder = TextDrawable.builder().beginConfig()
                        .withBorder(4)
                        .endConfig()
                        .round();
                TextDrawable unread_drawable = unreadBuilder.build(""+unread_count,Color.RED);
                    unreadCount.setImageDrawable(unread_drawable);
            }
        }
        return view;
    }
}
