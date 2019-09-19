package com.example.mypracticedemo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mypracticedemo.R;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import static com.example.mypracticedemo.Activity.LoginActivity.ACCOUNT_KEY;
import static com.example.mypracticedemo.Activity.LoginActivity.APP_ID;
import static com.example.mypracticedemo.Activity.LoginActivity.AUTH_KEY;
import static com.example.mypracticedemo.Activity.LoginActivity.AUTH_SECRET_KEY;

public class SignUpActivity extends AppCompatActivity {
    EditText userEd, pwdEd;
    Button register;

    LinearLayout loginOpt, registerOpt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        userEd = findViewById(R.id.user);
        pwdEd = findViewById(R.id.pwd);
        register = findViewById(R.id.register);

        registerOpt =findViewById(R.id.registerOpt);
        loginOpt =findViewById(R.id.loginOpt);

        loginOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);

                startActivity(i);
                overridePendingTransition( R.anim.slide_out_up, R.anim.slide_in_up );
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userEd.getText().toString();
                String pwd = pwdEd.getText().toString();

                QBUser qbUser = new QBUser(user,pwd);
                initializingQB();

                QBUsers.signUpSignInTask(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Intent i = new Intent(SignUpActivity.this, MainActivity.class);

                        startActivity(i);
                        Toast.makeText(SignUpActivity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initializingQB() {
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET_KEY);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
