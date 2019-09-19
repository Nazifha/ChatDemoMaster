package com.example.mypracticedemo.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypracticedemo.Adapter.MessageAdapter;
import com.example.mypracticedemo.Common.common;
import com.example.mypracticedemo.Holder.QBChatMessagesHolder;
import com.example.mypracticedemo.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity implements QBChatDialogMessageListener {

    QBChatDialog qbChatDialog;
    ListView listView;
    EditText ed;
    ImageView sendImg, emoImg;
    MessageAdapter adapter;


    //variable for edit/delete message
    int contextMenuIndexClicked = -1;
    boolean isEditMode = false;
    QBChatMessage editMessage;


    Toolbar toolbar;

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //get index context menu click
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        contextMenuIndexClicked = info.position;
        switch (item.getItemId()) {
            case R.id.updateChat:
                updateMessage();
                break;
            case R.id.deleteChat:
                deleteChat();
                break;
            default:
                break;
        }
        return true;
    }

    private void deleteChat() {
        editMessage = QBChatMessagesHolder.getInstance().getChatMessageDialogId(qbChatDialog.getDialogId())
                .get(contextMenuIndexClicked);

        QBRestChatService.deleteMessage(editMessage.getId(), false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                retrieveMessage();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void updateMessage() {
        // set message for edit
        editMessage = QBChatMessagesHolder.getInstance().getChatMessageDialogId(qbChatDialog.getDialogId())
                .get(contextMenuIndexClicked);
        ed.setText(editMessage.getBody());
        isEditMode = true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_update, menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        listView = findViewById(R.id.listView);
        ed = findViewById(R.id.ed);
        sendImg = findViewById(R.id.sendImg);
        emoImg = findViewById(R.id.emoImg);


        initViews();
        iniChatDialog();

        retrieveMessage();
        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Message", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isEditMode) {
                        QBChatMessage chatMessage = new QBChatMessage();
                        chatMessage.setBody(ed.getText().toString());
                        chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                        chatMessage.setSaveToHistory(true);
                        try {
                            qbChatDialog.sendMessage(chatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                        //Fix private chat don't  show message
                        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                            //cache Message
                            QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(), chatMessage);
                            ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessageDialogId(chatMessage.getDialogId());
                            adapter = new MessageAdapter(getBaseContext(), messages);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }

                   /* // put message to cache

                    QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(),chatMessage);
                    ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessageDialogId(qbChatDialog.getDialogId());
                    adapter = new MessageAdapter(getBaseContext(),messages);
                    listView.setAdapter(adapter);*/


                        ed.setText("");
                        ed.setFocusable(true);
                    } else {
                        QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
                        messageUpdateBuilder.updateText(ed.getText().toString()).markDelivered().markRead();

                        QBRestChatService.updateMessage(editMessage.getId(), qbChatDialog.getDialogId(), messageUpdateBuilder)
                                .performAsync(new QBEntityCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid, Bundle bundle) {
                                        //refresh data
                                        retrieveMessage();
                                        isEditMode = false;

                                        ed.setText("");
                                        ed.setFocusable(true);
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {
                                        Toast.makeText(MessageActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }
            }


        });


    }

    private void initViews() {

        //Add context menu
        registerForContextMenu(listView);

        // add toolbar
        toolbar = findViewById(R.id.chat_dialog_toolbar);


    }

    private void retrieveMessage() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500);

        if (qbChatDialog != null) {
            QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    // put message to cache
                    QBChatMessagesHolder.getInstance().putMessages(qbChatDialog.getDialogId(), qbChatMessages);
                    adapter = new MessageAdapter(getBaseContext(), qbChatMessages);
                    listView.setAdapter(adapter);

                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editGroupName:
                editGroupName();
                break;
            case R.id.addUser:
                addUserToGroup();
                break;
            case R.id.removeUser:
                removeUserFromGroup();
                break;

        }

        return true;
    }

    private void removeUserFromGroup() {
        Intent i = new Intent(this,UserListActivity.class);
        i.putExtra(common.UPDATE_DIALOG_EXTRA,qbChatDialog);
        i.putExtra(common.UPDATE_MODE,common.UPDATE_REMOVE_MODE);
        startActivity(i);
    }

    private void addUserToGroup() {
        Intent i = new Intent(this,UserListActivity.class);
        i.putExtra(common.UPDATE_DIALOG_EXTRA,qbChatDialog);
        i.putExtra(common.UPDATE_MODE,common.UPDATE_ADD_MODE);
        startActivity(i);
    }

    private void editGroupName() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.edit_group_name_dilaog, null);

        final AlertDialog.Builder editNameDialog = new AlertDialog.Builder(this);

        editNameDialog.setView(view);
        final EditText edGrpName = view.findViewById(R.id.edited_group_name);

        editNameDialog.setCancelable(false);

        editNameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                qbChatDialog.setName(edGrpName.getText().toString());

                QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                QBRestChatService.updateGroupChatDialog(qbChatDialog,requestBuilder)
                        .performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                Toast.makeText(MessageActivity.this, "Group Name Edited", Toast.LENGTH_SHORT).show();
                                toolbar.setTitle(qbChatDialog.getName());
                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = editNameDialog.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (qbChatDialog.getType() == QBDialogType.GROUP
                || qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP)
            getMenuInflater().inflate(R.menu.group_chat_update_menu, menu);

        return true;
    }

    private void iniChatDialog() {
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(common.DIALOG_EXTRA);
        qbChatDialog.initForChat(QBChatService.getInstance());

        //Register listener Incoming Message

        QBIncomingMessagesManager incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });


        //Add groupChat code here

        if (qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP || qbChatDialog.getType() == QBDialogType.GROUP) {
            DiscussionHistory discussionHistory = new DiscussionHistory();
            discussionHistory.setMaxStanzas(0);

            qbChatDialog.join(discussionHistory, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {

                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("Error", e.getMessage());
                }
            });


        }
        qbChatDialog.addMessageListener(this);

        // SET TOOLBAR TITLE
        toolbar.setTitle(qbChatDialog.getName());
        setSupportActionBar(toolbar);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        QBChatMessagesHolder.getInstance().putMessage(qbChatMessage.getDialogId(), qbChatMessage);
        ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessageDialogId(qbChatMessage.getDialogId());
        adapter = new MessageAdapter(getBaseContext(), messages);
        listView.setAdapter(adapter);
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        Log.e("Error", e.getMessage());
    }
}
