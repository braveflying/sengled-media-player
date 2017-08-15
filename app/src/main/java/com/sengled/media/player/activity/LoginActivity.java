package com.sengled.media.player.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.sengled.media.player.BaseApplication;
import com.sengled.media.player.MainActivity;
import com.sengled.media.player.R;
import com.sengled.media.player.common.ConfigUtils;
import com.sengled.media.player.common.Const;
import com.sengled.media.player.entity.UserInfoDto;
import com.sengled.media.player.http.HttpCameraInvoker;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 2017/7/21.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameEdit;
    private EditText pwdEdit;
    private Button loginBtn;
    private View loginCover;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(IconicsContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasLogin()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        };

        setContentView(R.layout.media_login_layout);
        initLayout();
        initData();
        initEvent();
    }

    private boolean hasLogin() {
        String session = ConfigUtils.getInstance(this).getConfig(Const.SESSION_ID);
        if (session != null && !session.equals("")) {
            String jsonUser = ConfigUtils.getInstance(this).getConfig(Const.USER_INFO);
            UserInfoDto userInfo = new Gson().fromJson(jsonUser, UserInfoDto.class);
            ((BaseApplication)getApplication()).setUserInfo(userInfo);
            return true;
        }
        return false;
    }

    private void initLayout() {
        nameEdit = (EditText) findViewById(R.id.media_edit_username);
        pwdEdit = (EditText) findViewById(R.id.media_edit_password);
        loginBtn = (Button) findViewById(R.id.media_btn_login);
        loginCover = findViewById(R.id.media_login_cover);
    }

    private void initData(){
        String userJson = ConfigUtils.getInstance(this).getConfig(Const.USER_INFO);
        if (userJson != null && !userJson.equals("")){
            UserInfoDto userInfo = new Gson().fromJson(userJson, UserInfoDto.class);
            nameEdit.setText(userInfo.getUsername());
        }
    }

    private void initEvent() {
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String username = nameEdit.getText().toString();
        String password = pwdEdit.getText().toString();


        Map<String, Object> loginMap = new HashMap<String, Object>();
        loginMap.put("uuid", "3fb5e2c7142de21c");
        loginMap.put("os_type", "ios");
        loginMap.put("user", username);
        loginMap.put("pwd", password);

        loginCover.setVisibility(View.VISIBLE);
        Call<UserInfoDto> caller = HttpCameraInvoker.getInvoker(this).doLogin(loginMap);
        caller.enqueue(new Callback<UserInfoDto>() {
            @Override
            public void onResponse(Call<UserInfoDto> call, Response<UserInfoDto> response) {
                UserInfoDto userInfo = response.body();
                if ("200".equals(userInfo.getMessageCode())){
                    String sessionId = userInfo.getJsessionid();
                    userInfo.setUsername(username);
                    ConfigUtils.getInstance(LoginActivity.this).saveConfig(Const.SESSION_ID, sessionId);
                    ConfigUtils.getInstance(LoginActivity.this).saveConfig(Const.USER_INFO, new Gson().toJson(userInfo));
                    ((BaseApplication)getApplication()).setUserInfo(userInfo);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }else {
                    Toast.makeText(LoginActivity.this, userInfo.getDescription(), Toast.LENGTH_SHORT).show();
                    loginCover.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<UserInfoDto> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "login fail!, cause:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                loginCover.setVisibility(View.GONE);
            }
        });
    }
}
