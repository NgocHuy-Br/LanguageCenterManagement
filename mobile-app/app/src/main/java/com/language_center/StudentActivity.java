package com.language_center;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
    Button btnTabProfile, btnTabResults, btnRefreshResults;
    Button btnLogout;
    TextView tvTitle;
    LinearLayout sectionProfile, sectionResults;
    TextView tvStudentId, tvName, tvBirthDate, tvAddress, tvPhone, tvEmail;
    List<String> resultList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    OkHttpClient client = new OkHttpClient();
    String token = "";
    long studentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        tvTitle = findViewById(R.id.tvStudentTitle);
        btnTabProfile = findViewById(R.id.btnTabStudentProfile);
        btnTabResults = findViewById(R.id.btnTabStudentResults);
        btnRefreshResults = findViewById(R.id.btnRefreshStudentResults);
        lvResults = findViewById(R.id.lvResults);
        btnLogout = findViewById(R.id.btnLogoutStudent);
        sectionProfile = findViewById(R.id.sectionStudentProfile);
        sectionResults = findViewById(R.id.sectionStudentResults);
        tvStudentId = findViewById(R.id.tvProfileStudentId);
        tvName = findViewById(R.id.tvProfileStudentName);
        tvBirthDate = findViewById(R.id.tvProfileStudentBirthDate);
        tvAddress = findViewById(R.id.tvProfileStudentAddress);
        tvPhone = findViewById(R.id.tvProfileStudentPhone);
        tvEmail = findViewById(R.id.tvProfileStudentEmail);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultList);
        lvResults.setAdapter(adapter);

        SharedPreferences pref = getSharedPreferences("AUTH", MODE_PRIVATE);
        token = pref.getString("token", "");
        studentId = pref.getLong("studentId", -1);

        loadProfile(pref);
        showSection("profile");

        btnTabProfile.setOnClickListener(v -> showSection("profile"));
        btnTabResults.setOnClickListener(v -> {
            showSection("results");
            if (studentId != -1) {
                loadResults(token, studentId);
            }
        });

        btnRefreshResults.setOnClickListener(v -> {
            if (studentId != -1) {
                loadResults(token, studentId);
            }
        });

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

    private void showSection(String tab) {
        boolean isProfile = "profile".equals(tab);
        sectionProfile.setVisibility(isProfile ? View.VISIBLE : View.GONE);
        sectionResults.setVisibility(isProfile ? View.GONE : View.VISIBLE);
        tvTitle.setText(isProfile ? "Thông tin cá nhân" : "Kết quả học tập");
        btnTabProfile.setEnabled(!isProfile);
        btnTabResults.setEnabled(isProfile);
    }

    private void loadProfile(SharedPreferences pref) {
        tvStudentId.setText(emptyFallback(pref.getString("student_studentId", "")));
        tvName.setText(emptyFallback(pref.getString("student_name", "")));
        tvBirthDate.setText(emptyFallback(pref.getString("student_birthDate", "")));
        tvAddress.setText(emptyFallback(pref.getString("student_address", "")));
        tvPhone.setText(emptyFallback(pref.getString("student_phone", "")));
        tvEmail.setText(emptyFallback(pref.getString("student_email", "")));
    }

    private String emptyFallback(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
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
                            JSONObject classStudent = res.optJSONObject("classStudent");
                            JSONObject classroom = classStudent != null ? classStudent.optJSONObject("classroom")
                                    : null;

                            String className = classroom != null ? classroom.optString("name", "N/A") : "N/A";
                            String score = res.isNull("score") ? "Chưa nhập điểm"
                                    : String.valueOf(res.optDouble("score", 0.0));
                            String comment = res.optString("comment", "");

                            String info = "Lớp: " + className
                                    + "\nĐiểm: " + score
                                    + (comment.isEmpty() ? "" : "\nNhận xét: " + comment);
                            resultList.add(info);
                        }

                        if (resultList.isEmpty()) {
                            resultList.add("Bạn chưa có kết quả học tập nào.");
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(
                        () -> Toast.makeText(StudentActivity.this, "Lỗi tải kết quả!", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
