package com.example.mypracticedemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mypracticedemo.Holder.QBUsersHolder;
import com.example.mypracticedemo.R;
import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<QBChatMessage> qbChatMessages;

    public MessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
        this.context = context;
        this.qbChatMessages = qbChatMessages;
    }

    @Override
    public int getCount() {
        return qbChatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        if(qbChatMessages.size()==0){
            return 1;
        }
        return qbChatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (qbChatMessages.get(position).getSenderId().equals(QBChatService.getInstance().getUser().getId())) {
                view = inflater.inflate(R.layout.item_send_message, null);
                BubbleTextView bubbleTextView = view.findViewById(R.id.messageBox);
                bubbleTextView.setText(qbChatMessages.get(position).getBody());
            } else {
                view = inflater.inflate(R.layout.item_receive_message, null);
                BubbleTextView bubbleTextView = view.findViewById(R.id.messageBox);
                bubbleTextView.setText(qbChatMessages.get(position).getBody());
                TextView user = view.findViewById(R.id.user);
                user.setText(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(position).getSenderId()).getLogin());

            }
        }
        return view;
    }


}
