package com.language_center;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherActivity extends AppCompatActivity {
    ListView lvClasses;
    Button btnLogout;
    List<String> classList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        lvClasses = findViewById(R.id.lvClasses);
        btnLogout = findViewById(R.id.btnLogoutTeacher);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classList);
        lvClasses.setAdapter(adapter);

        SharedPreferences pref = getSharedPreferences("AUTH", MODE_PRIVATE);
        String token = pref.getString("token", "");

        loadClasses(token);

        btnLogout.setOnClickListener(v -> {
            pref.edit().clear().apply();
            startActivity(new Intent(TeacherActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadClasses(String token) {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/teacher/classes")
                .addHeader("Authorization", token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        JSONArray data = json.getJSONArray("data");

                        classList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject classroom = data.getJSONObject(i);
                            String info = "Lớp: " + classroom.getString("name") + 
                                         "\nPhòng: " + classroom.getString("room") +
                                         "\nLịch: " + classroom.getString("schedule");
                            classList.add(info);
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(TeacherActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
