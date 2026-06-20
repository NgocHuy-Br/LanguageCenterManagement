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

public class StudentActivity extends AppCompatActivity {
    ListView lvResults;
    Button btnLogout;
    List<String> resultList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        lvResults = findViewById(R.id.lvResults);
        btnLogout = findViewById(R.id.btnLogoutStudent);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultList);
        lvResults.setAdapter(adapter);

        SharedPreferences pref = getSharedPreferences("AUTH", MODE_PRIVATE);
        String token = pref.getString("token", "");
        long studentId = pref.getLong("studentId", -1);

        if (studentId != -1) {
            loadResults(token, studentId);
        } else {
            Toast.makeText(this, "Không tìm thấy ID học viên!", Toast.LENGTH_SHORT).show();
        }

        btnLogout.setOnClickListener(v -> {
            pref.edit().clear().apply();
            startActivity(new Intent(StudentActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadResults(String token, long studentId) {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/student/results/" + studentId)
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

                        resultList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject res = data.getJSONObject(i);
                            JSONObject classroom = res.getJSONObject("classroom");
                            
                            String info = "Lớp: " + classroom.getString("name") +
                                         "\nĐiểm: " + res.getDouble("score") +
                                         "\nGhi chú: " + res.getString("note");
                            resultList.add(info);
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(StudentActivity.this, "Lỗi tải kết quả!", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
