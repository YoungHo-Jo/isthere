package com.apptive.joDuo.isthere;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

import java.io.IOException;

/**
 * Created by joseong-yun on 2017. 5. 22..
 */

public class LoginPage extends Dialog {

    private EditText userID;
    private EditText userPassword;
    private Button loginButton;
    private Button registerUser;
    private Button close;

    private LinearLayout autoLoginLine;
    private LinearLayout saveIDLine;

    private AnimCheckBox autoLoginBox;
    private AnimCheckBox saveIDBox;

    private IsThereHttpHelper httpHelper;
    private Context context;

    private OnLoginListener onLoginListener;

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public LoginPage(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
    }

    private boolean loginEvent(String id, String password) {
        return true;
    }

    public LoginPage() {
        super(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.login_page);

        userID = (EditText) findViewById(R.id.userID);
        userPassword = (EditText) findViewById(R.id.userPassword);
        loginButton = (Button) findViewById(R.id.login_button);
        close = (Button) findViewById(R.id.login_dismiss_button);

        autoLoginLine = (LinearLayout) findViewById(R.id.autoLogin_line);
        autoLoginBox = (AnimCheckBox) findViewById(R.id.autoLogin_button);
        saveIDLine = (LinearLayout) findViewById(R.id.saveID_line);
        saveIDBox = (AnimCheckBox) findViewById(R.id.saveID_button);
        registerUser = (Button) findViewById(R.id.register_service);


        // login dialog 닫기
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // checkbox 와 text를 한꺼번에 묶기위해서 clickListener 설정
        autoLoginLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoLoginBox.setChecked(!autoLoginBox.isChecked());
            }
        });
        saveIDLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIDBox.setChecked(!saveIDBox.isChecked());
            }
        });

        // Set loginButton clickListener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin(v, userID.getText().toString(), userPassword.getText().toString(), onLoginListener);
            }
        });

        // Set RegisterUser clickListener
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RegisterLogin.class);
                getContext().startActivity(intent);
            }
        });

    }

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }


    private void doLogin(View v, String id, String password, final OnLoginListener onLoginListener) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                LogDebuger.debugPrinter(LogDebuger.TagType.LOGIN_PAGE, "Try Login");
                httpHelper = MainActivity.GetHttpHelper();
                boolean isLoginListener = (onLoginListener != null);
                try {
                    boolean loginResult = httpHelper.postLogin(userID.getText().toString(), userPassword.getText().toString());


                    if (loginResult) {
                        LogDebuger.debugPrinter(LogDebuger.TagType.LOGIN_PAGE, "Login Succeed");
                        if (isLoginListener) {
                            onLoginListener.onLoginSucceed();
                        }
                    } else {
                        LogDebuger.debugPrinter(LogDebuger.TagType.LOGIN_PAGE, "Login Failed by incorrect value");
                        if (isLoginListener) {
                            onLoginListener.onLoginFailed(true, false);
                        }
                    }
                } catch (IOException e) {
                    LogDebuger.debugPrinter(LogDebuger.TagType.LOGIN_PAGE, "Login Failed by exception");
                    if (isLoginListener) {
                        onLoginListener.onLoginFailed(false, true);
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    //// Callback interface ////
    public interface OnLoginListener {
        void onLoginSucceed();
        void onLoginFailed(boolean notMatched, boolean isException);
    }
}
