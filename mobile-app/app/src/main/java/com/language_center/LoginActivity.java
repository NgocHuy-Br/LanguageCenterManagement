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

        // Kiểm tra nếu đã đăng nhập trước đó
        SharedPreferences pref = getSharedPreferences("AUTH", MODE_PRIVATE);
        String savedRole = pref.getString("role", "");
        if (!savedRole.isEmpty()) {
            redirectByRole(savedRole);
        }

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
                        String role = data.optString("role", "");

                        SharedPreferences pref = getSharedPreferences("AUTH", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("token", authHeader);
                        editor.putString("role", role);
                        editor.putString("username", user);

                        editor.putLong("teacherId", data.optLong("teacherId", -1));
                        editor.putLong("studentId", data.optLong("studentId", -1));

                        JSONObject teacher = data.optJSONObject("teacher");
                        if (teacher != null) {
                            editor.putString("teacher_teacherId", teacher.optString("teacherId", ""));
                            editor.putString("teacher_name", teacher.optString("name", ""));
                            editor.putString("teacher_birthDate", teacher.optString("birthDate", ""));
                            editor.putString("teacher_address", teacher.optString("address", ""));
                            editor.putString("teacher_phone", teacher.optString("phone", ""));
                            editor.putString("teacher_email", teacher.optString("email", ""));
                        }

                        JSONObject student = data.optJSONObject("student");
                        if (student != null) {
                            editor.putString("student_studentId", student.optString("studentId", ""));
                            editor.putString("student_name", student.optString("name", ""));
                            editor.putString("student_birthDate", student.optString("birthDate", ""));
                            editor.putString("student_address", student.optString("address", ""));
                            editor.putString("student_phone", student.optString("phone", ""));
                            editor.putString("student_email", student.optString("email", ""));
                        }
                        editor.apply();

                        runOnUiThread(() -> {
                            if (role.equals("TEACHER") || role.equals("STUDENT")) {
                                redirectByRole(role);
                            } else {
                                Toast.makeText(LoginActivity.this, "Quyền Admin không hỗ trợ trên Mobile!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast
                            .makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT)
                            .show());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast
                        .makeText(LoginActivity.this, "Lỗi kết nối server (10.0.2.2)!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void redirectByRole(String role) {
        if (role.equals("TEACHER")) {
            startActivity(new Intent(this, TeacherActivity.class));
            finish();
        } else if (role.equals("STUDENT")) {
            startActivity(new Intent(this, StudentActivity.class));
            finish();
        }
    }
}
