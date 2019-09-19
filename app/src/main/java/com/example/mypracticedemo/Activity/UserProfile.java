package com.example.mypracticedemo.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mypracticedemo.Common.common;
import com.example.mypracticedemo.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class UserProfile extends AppCompatActivity {
    ImageView update_profile, logout;
    EditText edfullName, ednewPwd, edoldPwd, edpno, edemail;
    Button update, cancel;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        edfullName = findViewById(R.id.fullName);
        ednewPwd = findViewById(R.id.newPwd);
        edoldPwd = findViewById(R.id.oldPwd);
        edpno = findViewById(R.id.pno);
        edemail = findViewById(R.id.email);

        update = findViewById(R.id.update);
        cancel = findViewById(R.id.cancel);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = edfullName.getText().toString();
                String newPwd = ednewPwd.getText().toString();
                String oldPwd = edoldPwd.getText().toString();
                String email = edemail.getText().toString();
                String pno = edpno.getText().toString();

                QBUser user = new QBUser();
                user.setId(QBChatService.getInstance().getUser().getId());
                if (!common.isNullOrEmptyString(oldPwd)) ;
                user.setOldPassword(oldPwd);

                if (!common.isNullOrEmptyString(newPwd)) ;
                user.setPassword(newPwd);

                if (!common.isNullOrEmptyString(fullName)) ;
                user.setFullName(fullName);

                if (!common.isNullOrEmptyString(email)) ;
                user.setEmail(email);

                if (!common.isNullOrEmptyString(pno)) ;
                user.setPhone(pno);
                mDialog = new ProgressDialog(UserProfile.this);
                mDialog.setMessage("Please wait ....");
                mDialog.setIndeterminate(false);

                mDialog.setCancelable(false);
                mDialog.show();

                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(UserProfile.this, "User: "+qbUser.getLogin()+ "Updated", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }
        });


        update_profile = findViewById(R.id.update_profile);
        logout = findViewById(R.id.logout);

        update_profile.setVisibility(View.GONE);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                Toast.makeText(UserProfile.this, "You are logged out !!!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(UserProfile.this,LaunchActivity.class);
                                startActivity(i);
                                finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }
        });

    }
}
