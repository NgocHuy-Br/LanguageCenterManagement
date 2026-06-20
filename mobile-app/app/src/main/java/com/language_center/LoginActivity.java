package com.language_center;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    EditText edtUser, edtPass;
    Button btnLogin;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUser = findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String user = edtUser.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
                return;
            }
            performLogin(user, pass);
        });
    }

    private void performLogin(String user, String pass) {
        String authHeader = "Basic " + Base64.encodeToString((user + ":" + pass).getBytes(), Base64.NO_WRAP);

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/me")
                .addHeader("Authorization", authHeader)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        JSONObject data = json.getJSONObject("data");
                        String role = data.getString("role");

                        SharedPreferences pref = getSharedPreferences("AUTH", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("token", authHeader);
                        editor.putString("role", role);
                        editor.putString("username", user);

                        runOnUiThread(() -> {
                            if (role.equals("TEACHER")) {
                                editor.apply();
                                startActivity(new Intent(LoginActivity.this, TeacherActivity.class));
                                finish();
                            } else if (role.equals("STUDENT")) {
                                try {
                                    editor.putLong("studentId", data.getLong("studentId"));
                                } catch (Exception e) {}
                                editor.apply();
                                startActivity(new Intent(LoginActivity.this, StudentActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Ứng dụng không hỗ trợ Admin!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Lỗi kết nối Server!", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
