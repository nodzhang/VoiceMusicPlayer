package com.hackathon.cmos.www.voicemusicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.cmos.www.voicemusicplayer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity {
    private ImageButton btn_login;
    private TextView tv_regist;
    private EditText edt_username;
    private EditText edt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setViews();
        setListeners();
    }

    private void setListeners() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = edt_username.getText().toString();
                String password = edt_password.getText().toString();
                if ("".equals(username)) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_LONG).show();
                    return;
                } else if ("".equals(password)) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }

                final String httpUrl = "http://120.27.13.191/hackathon/login/?username="+
                        username+"&&password="+password;

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request
                                    .Builder()
                                    .url(httpUrl).build();
                            Response response = client.newCall(request).execute();
                            String reaponseData = response.body().string();

                            JSONObject obj = new JSONObject(reaponseData);
                            if(obj.has("msg")&&obj.has("code")){
                                if("登陆成功".equals(obj.getString("msg"))&&"0".equals("code"));{
                                    Intent intent = new Intent();
                                    intent.putExtra("username",username);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });
    }

    private void setViews() {
        btn_login = findViewById(R.id.btn_login);
        tv_regist = findViewById(R.id.tv_regist);
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
    }
}
