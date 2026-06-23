package com.language_center;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherActivity extends AppCompatActivity {
    ListView lvClasses;
    ListView lvScores;
    Spinner spnClassroom;
    Button btnTabProfile, btnTabClasses, btnTabScores, btnRefreshClasses, btnLoadScores;
    Button btnLogout;
    TextView tvTitle;
    TextView tvTeacherId, tvName, tvBirthDate, tvAddress, tvPhone, tvEmail;
    LinearLayout sectionProfile, sectionClasses, sectionScores;
    List<String> classList = new ArrayList<>();
    List<String> scoreList = new ArrayList<>();
    List<JSONObject> classesRaw = new ArrayList<>();
    List<ScoreRow> scoreRows = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> scoreAdapter;
    ArrayAdapter<String> classSpinnerAdapter;
    OkHttpClient client = new OkHttpClient();
    String token = "";

    private static class ScoreRow {
        long classStudentId;
        long resultId;
        String studentName;
        String studentCode;
        Double score;
        String comment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        tvTitle = findViewById(R.id.tvTeacherTitle);
        btnTabProfile = findViewById(R.id.btnTabTeacherProfile);
        btnTabClasses = findViewById(R.id.btnTabTeacherClasses);
        btnTabScores = findViewById(R.id.btnTabTeacherScores);
        btnRefreshClasses = findViewById(R.id.btnRefreshClasses);
        btnLoadScores = findViewById(R.id.btnLoadScores);
        lvClasses = findViewById(R.id.lvClasses);
        lvScores = findViewById(R.id.lvScores);
        spnClassroom = findViewById(R.id.spnTeacherClassroom);
        btnLogout = findViewById(R.id.btnLogoutTeacher);
        sectionProfile = findViewById(R.id.sectionTeacherProfile);
        sectionClasses = findViewById(R.id.sectionTeacherClasses);
        sectionScores = findViewById(R.id.sectionTeacherScores);
        tvTeacherId = findViewById(R.id.tvProfileTeacherId);
        tvName = findViewById(R.id.tvProfileTeacherName);
        tvBirthDate = findViewById(R.id.tvProfileTeacherBirthDate);
        tvAddress = findViewById(R.id.tvProfileTeacherAddress);
        tvPhone = findViewById(R.id.tvProfileTeacherPhone);
        tvEmail = findViewById(R.id.tvProfileTeacherEmail);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classList);
        lvClasses.setAdapter(adapter);
        scoreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scoreList);
        lvScores.setAdapter(scoreAdapter);
        classSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        classSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnClassroom.setAdapter(classSpinnerAdapter);

        SharedPreferences pref = getSharedPreferences("AUTH", MODE_PRIVATE);
        token = pref.getString("token", "");

        loadProfile(pref);
        showSection("profile");
        loadClasses(token);

        btnTabProfile.setOnClickListener(v -> showSection("profile"));
        btnTabClasses.setOnClickListener(v -> showSection("classes"));
        btnTabScores.setOnClickListener(v -> showSection("scores"));
        btnRefreshClasses.setOnClickListener(v -> loadClasses(token));
        btnLoadScores.setOnClickListener(v -> {
            long classId = getSelectedClassId();
            if (classId <= 0) {
                Toast.makeText(this, "Vui lòng chọn lớp.", Toast.LENGTH_SHORT).show();
                return;
            }
            loadScoresByClass(token, classId);
        });

        lvScores.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= scoreRows.size())
                return;
            openScoreDialog(scoreRows.get(position));
        });

        btnLogout.setOnClickListener(v -> {
            pref.edit().clear().apply();
            startActivity(new Intent(TeacherActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void showSection(String tab) {
        boolean profile = "profile".equals(tab);
        boolean classes = "classes".equals(tab);

        sectionProfile.setVisibility(profile ? View.VISIBLE : View.GONE);
        sectionClasses.setVisibility(classes ? View.VISIBLE : View.GONE);
        sectionScores.setVisibility((!profile && !classes) ? View.VISIBLE : View.GONE);

        tvTitle.setText(profile ? "Thông tin cá nhân" : classes ? "Lớp học" : "Nhập điểm");

        btnTabProfile.setEnabled(!profile);
        btnTabClasses.setEnabled(!classes);
        btnTabScores.setEnabled(profile || classes);
    }

    private void loadProfile(SharedPreferences pref) {
        tvTeacherId.setText(emptyFallback(pref.getString("teacher_teacherId", "")));
        tvName.setText(emptyFallback(pref.getString("teacher_name", "")));
        tvBirthDate.setText(emptyFallback(pref.getString("teacher_birthDate", "")));
        tvAddress.setText(emptyFallback(pref.getString("teacher_address", "")));
        tvPhone.setText(emptyFallback(pref.getString("teacher_phone", "")));
        tvEmail.setText(emptyFallback(pref.getString("teacher_email", "")));
    }

    private String emptyFallback(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
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

                        classesRaw.clear();
                        classList.clear();
                        final List<String> classNames = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject classroom = data.getJSONObject(i);
                            classesRaw.add(classroom);
                            classNames.add(classroom.optString("name", "Lớp " + classroom.optLong("id")));
                            String info = "Lớp: " + classroom.optString("name", "N/A");
                            classList.add(info);
                        }

                        if (classList.isEmpty()) {
                            classList.add("Bạn chưa được phân công lớp nào.");
                        }

                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            classSpinnerAdapter.clear();
                            classSpinnerAdapter.addAll(classNames);
                            classSpinnerAdapter.notifyDataSetChanged();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(TeacherActivity.this, "Lỗi tải danh sách lớp!", Toast.LENGTH_SHORT)
                        .show());
            }
        });
    }

    private long getSelectedClassId() {
        int selected = spnClassroom.getSelectedItemPosition();
        if (selected < 0 || selected >= classesRaw.size())
            return -1;
        return classesRaw.get(selected).optLong("id", -1);
    }

    private void loadScoresByClass(String token, long classId) {
        Request studentsReq = new Request.Builder()
                .url("http://10.0.2.2:8080/api/teacher/class-students?classroomId=" + classId)
                .addHeader("Authorization", token)
                .build();

        client.newCall(studentsReq).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response studentsRes) throws IOException {
                if (!studentsRes.isSuccessful()) {
                    runOnUiThread(() -> Toast
                            .makeText(TeacherActivity.this, "Không tải được học viên trong lớp.", Toast.LENGTH_SHORT)
                            .show());
                    return;
                }

                try {
                    JSONObject jsonStudents = new JSONObject(studentsRes.body().string());
                    JSONArray studentsData = jsonStudents.getJSONArray("data");

                    Request resultsReq = new Request.Builder()
                            .url("http://10.0.2.2:8080/api/teacher/results?classroomId=" + classId)
                            .addHeader("Authorization", token)
                            .build();

                    client.newCall(resultsReq).enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response resultsRes) throws IOException {
                            if (!resultsRes.isSuccessful()) {
                                runOnUiThread(() -> Toast
                                        .makeText(TeacherActivity.this, "Không tải được kết quả.", Toast.LENGTH_SHORT)
                                        .show());
                                return;
                            }

                            try {
                                JSONObject jsonResults = new JSONObject(resultsRes.body().string());
                                JSONArray resultData = jsonResults.getJSONArray("data");

                                Map<Long, JSONObject> resultByClassStudent = new HashMap<>();
                                for (int i = 0; i < resultData.length(); i++) {
                                    JSONObject item = resultData.getJSONObject(i);
                                    JSONObject classStudent = item.optJSONObject("classStudent");
                                    if (classStudent != null) {
                                        resultByClassStudent.put(classStudent.optLong("id", -1), item);
                                    }
                                }

                                scoreRows.clear();
                                scoreList.clear();

                                for (int i = 0; i < studentsData.length(); i++) {
                                    JSONObject cs = studentsData.getJSONObject(i);
                                    long classStudentId = cs.optLong("id", -1);
                                    JSONObject student = cs.optJSONObject("student");
                                    String studentName = student != null ? student.optString("name", "-") : "-";
                                    String studentCode = student != null ? student.optString("studentId", "-") : "-";

                                    JSONObject result = resultByClassStudent.get(classStudentId);
                                    ScoreRow row = new ScoreRow();
                                    row.classStudentId = classStudentId;
                                    row.studentName = studentName;
                                    row.studentCode = studentCode;
                                    row.resultId = result == null ? -1 : result.optLong("id", -1);
                                    row.score = (result == null || result.isNull("score")) ? null
                                            : result.optDouble("score", 0);
                                    row.comment = result == null ? "" : result.optString("comment", "");
                                    scoreRows.add(row);

                                    String scoreText = row.score == null ? "Chưa nhập điểm" : String.valueOf(row.score);
                                    String line = "Mã số: " + studentCode
                                            + "\nHọc viên: " + studentName
                                            + "\nĐiểm: " + scoreText
                                            + (row.comment.isEmpty() ? "" : "\nNhận xét: " + row.comment);
                                    scoreList.add(line);
                                }

                                if (scoreList.isEmpty()) {
                                    scoreList.add("Lớp này chưa có học viên nào.");
                                }

                                runOnUiThread(() -> scoreAdapter.notifyDataSetChanged());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> Toast
                                    .makeText(TeacherActivity.this, "Lỗi tải kết quả.", Toast.LENGTH_SHORT).show());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast
                        .makeText(TeacherActivity.this, "Lỗi tải danh sách học viên.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void openScoreDialog(ScoreRow row) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(12);
        container.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(this);
        title.setText("Học viên: " + row.studentCode + " - " + row.studentName);
        container.addView(title);

        EditText scoreInput = new EditText(this);
        scoreInput.setHint("Điểm (0-10)");
        scoreInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        scoreInput.setText(row.score == null ? "" : String.valueOf(row.score));
        container.addView(scoreInput);

        EditText commentInput = new EditText(this);
        commentInput.setHint("Nhận xét");
        commentInput.setText(row.comment == null ? "" : row.comment);
        container.addView(commentInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(row.resultId > 0 ? "Sửa điểm" : "Nhập điểm")
                .setView(container)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String scoreText = scoreInput.getText().toString().trim();
                    if (scoreText.isEmpty()) {
                        Toast.makeText(this, "Điểm không được để trống.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double score;
                    try {
                        score = Double.parseDouble(scoreText);
                    } catch (Exception ex) {
                        Toast.makeText(this, "Điểm không hợp lệ.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (score < 0 || score > 10) {
                        Toast.makeText(this, "Điểm phải từ 0 đến 10.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveScore(row, score, commentInput.getText().toString().trim());
                });

        if (row.resultId > 0) {
            builder.setNeutralButton("Xóa", (dialog, which) -> deleteScore(row.resultId));
        }
        builder.show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private void saveScore(ScoreRow row, double score, String comment) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("score", score);
            payload.put("comment", comment);

            String url;
            String method;
            if (row.resultId > 0) {
                url = "http://10.0.2.2:8080/api/teacher/results/" + row.resultId;
                method = "PUT";
            } else {
                url = "http://10.0.2.2:8080/api/teacher/results?classStudentId=" + row.classStudentId;
                method = "POST";
            }

            RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", token)
                    .method(method, body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(TeacherActivity.this, "Lưu điểm thành công.", Toast.LENGTH_SHORT).show();
                            long classId = getSelectedClassId();
                            if (classId > 0) {
                                loadScoresByClass(token, classId);
                            }
                        } else {
                            Toast.makeText(TeacherActivity.this, "Không thể lưu điểm.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast
                            .makeText(TeacherActivity.this, "Lỗi kết nối khi lưu điểm.", Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Dữ liệu điểm không hợp lệ.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteScore(long resultId) {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/teacher/results/" + resultId)
                .addHeader("Authorization", token)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(TeacherActivity.this, "Đã xóa điểm.", Toast.LENGTH_SHORT).show();
                        long classId = getSelectedClassId();
                        if (classId > 0) {
                            loadScoresByClass(token, classId);
                        }
                    } else {
                        Toast.makeText(TeacherActivity.this, "Xóa điểm thất bại.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast
                        .makeText(TeacherActivity.this, "Lỗi kết nối khi xóa điểm.", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
