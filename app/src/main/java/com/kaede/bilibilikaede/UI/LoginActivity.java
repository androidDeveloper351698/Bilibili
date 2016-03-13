package com.kaede.bilibilikaede.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaede.bilibilikaede.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    @InjectView(R.id.login_left) ImageView login_left;
    @InjectView(R.id.login_right) ImageView login_right;
    @InjectView(R.id.toolbar_back) ImageView back;
    @InjectView(R.id.username) EditText et_username;
    @InjectView(R.id.password) EditText et_password;
    @InjectView(R.id.register) Button register;
    @InjectView(R.id.login) Button login;
    @InjectView(R.id.password_forget) TextView forget_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);
        initListener();
    }

    private void initListener(){
        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    login_left.setImageResource(R.drawable.ic_22_hide);
                    login_right.setImageResource(R.drawable.ic_33_hide);
                }else {
                    login_left.setImageResource(R.drawable.ic_22);
                    login_right.setImageResource(R.drawable.ic_33);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"敬请期待",Toast.LENGTH_SHORT).show();
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"敬请期待",Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }
}
