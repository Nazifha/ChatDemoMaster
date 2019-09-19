package com.example.mypracticedemo.Activity;


import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;

import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mypracticedemo.Adapter.UserListAdapter;
import com.example.mypracticedemo.Common.common;
import com.example.mypracticedemo.Holder.QBUsersHolder;
import com.example.mypracticedemo.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    ListView list;
    Button createChat;

    String mode = "";
    QBChatDialog qbChatDialog;
    List<QBUser> usersAdd = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mode = getIntent().getStringExtra(common.UPDATE_MODE);
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(common.UPDATE_DIALOG_EXTRA);


        list = findViewById(R.id.list);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        createChat = findViewById(R.id.crateChat);

        retrieveAllUsers();

        createChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mode == null)
                {
                    int count = list.getCount();

                    if (list.getCheckedItemPositions().size() == 1) {
                        createPrivateChat(list.getCheckedItemPositions());
                    } else if (list.getCheckedItemPositions().size() > 1) {
                        createGroupChat(list.getCheckedItemPositions());
                    } else {
                        Toast.makeText(UserListActivity.this, "Please select a Friend to star chat", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (mode.equals(common.UPDATE_ADD_MODE) && qbChatDialog != null)
                {
                    if (usersAdd.size() > 0)
                    {
                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();

                        int cntChoice = list.getCount();
                        SparseBooleanArray checkItemPosition = list.getCheckedItemPositions();

                        for (int i = 0;i<cntChoice;i++)
                        {
                            if (checkItemPosition.get(i))
                            {
                                QBUser user = (QBUser) list.getItemAtPosition(i);
                                requestBuilder.addUsers(user);
                            }
                        }

                        //Call service
                        QBRestChatService.updateGroupChatDialog(qbChatDialog,requestBuilder)
                                .performAsync(new QBEntityCallback<QBChatDialog>() {
                                    @Override
                                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                        Toast.makeText(UserListActivity.this, "Add user success", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {

                                    }
                                });
                    }
                }
                else if (mode.equals(common.UPDATE_REMOVE_MODE)&& qbChatDialog != null)
                {
                    if (usersAdd.size() > 0)
                    {
                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                        int cntChoice = list.getCount();
                        SparseBooleanArray checkItemPosition = list.getCheckedItemPositions();
                        for (int i = 0;i<cntChoice;i++)
                        {
                            if (checkItemPosition.get(i))
                            {
                                QBUser user = (QBUser) list.getItemAtPosition(i);
                                requestBuilder.removeUsers(user);
                            }
                        }

                        //Call service
                        QBRestChatService.updateGroupChatDialog(qbChatDialog,requestBuilder)
                                .performAsync(new QBEntityCallback<QBChatDialog>() {
                                    @Override
                                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                        Toast.makeText(UserListActivity.this, "Remove user success", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {

                                    }
                                });
                    }
                }

            }
        });

        if (mode == null && qbChatDialog == null)
            retrieveAllUsers();
        else {
            if (mode.equals(common.UPDATE_ADD_MODE))
                loadListAvailableUsers();
            else if (mode.equals(common.UPDATE_REMOVE_MODE))
                loadListUserInGroup();

        }
    }

    private void loadListUserInGroup() {
        createChat.setText("Remove User");

        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId())
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        List<Integer> occupantId = qbChatDialog.getOccupants();
                        List<QBUser> listAllUsersAlreadyInGroup = QBUsersHolder.getInstance().getUserByIds(occupantId);
                        ArrayList<QBUser> users = new ArrayList<QBUser>();
                        users.addAll(listAllUsersAlreadyInGroup);

                        UserListAdapter adapter = new UserListAdapter(users,getBaseContext());
                        list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        usersAdd = users;
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
    }

    private void loadListAvailableUsers() {
        createChat.setText("Add User");

        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId())
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        ArrayList<QBUser> listUsers = QBUsersHolder.getInstance().getAllUsers();
                        List<Integer> occupantId = qbChatDialog.getOccupants();
                        List<QBUser> listUserAlreadyInGroup = QBUsersHolder.getInstance().getUserByIds(occupantId);

                        //remove all user already in chat group
                        for (QBUser user:listUserAlreadyInGroup)
                            listUsers.remove(user);
                        if (listUsers.size() > 0)
                        {
                            UserListAdapter adapter = new UserListAdapter(listUsers,getBaseContext());
                            list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            usersAdd = listUsers;

                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialog = new ProgressDialog(UserListActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        int countChoice = list.getCount();
        ArrayList<Integer> occupantList = new ArrayList<>();
        for (int i = 0; i < countChoice; i++) {
            if (checkedItemPositions.get(i)) {
                QBUser user = (QBUser) list.getItemAtPosition(i);
                occupantList.add(user.getId());
            }
        }

        //create chat dialog
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(common.createChatDialogName(occupantList));
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mDialog.dismiss();
                Toast.makeText(UserListActivity.this, "Created Group chat successfully", Toast.LENGTH_SHORT).show();
                // send system message to recipient Id user
                QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setBody(qbChatDialog.getDialogId());
                for (int i = 0; i < qbChatDialog.getOccupants().size(); i++) {
                    qbChatMessage.setRecipientId(qbChatDialog.getOccupants().get(i));
                    try {
                        qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }


                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error", e.getMessage());
            }
        });
    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialog = new ProgressDialog(UserListActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        int countChoice = list.getCount();
        for (int i = 0; i < countChoice; i++) {
            if (checkedItemPositions.get(i)) {
                final QBUser user = (QBUser) list.getItemAtPosition(i);
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());

                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        mDialog.dismiss();
                        Toast.makeText(UserListActivity.this, "Created Private chat successfully", Toast.LENGTH_SHORT).show();

                        // send system message to recipient Id user
                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        QBChatMessage qbChatMessage = new QBChatMessage();
                        qbChatMessage.setRecipientId(user.getId());
                        qbChatMessage.setBody(qbChatDialog.getDialogId());

                        try {
                            qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }
        }
    }


    private void retrieveAllUsers() {
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();

                //Add to cache

                QBUsersHolder.getInstance().putUsers(qbUsers);
                for (QBUser user : qbUsers) {
                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())) {
                        qbUserWithoutCurrent.add(user);
                    }
                    UserListAdapter adapter = new UserListAdapter(qbUsers, getBaseContext());
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error", e.getMessage());

            }
        });
    }
}
