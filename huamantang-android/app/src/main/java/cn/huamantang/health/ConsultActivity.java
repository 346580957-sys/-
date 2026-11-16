package cn.huamantang.health;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public final class ConsultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult);
        SystemBars.apply(this, findViewById(R.id.consult_root_view), null);
        findViewById(R.id.consult_back).setOnClickListener(view -> finish());
        findViewById(R.id.consult_done).setOnClickListener(view -> finish());

        Spinner topic = findViewById(R.id.consult_topic);
        setSpinnerItems(topic, new String[]{"综合慢病管理", "糖尿病", "高血压", "血脂异常", "高尿酸与痛风", "睡眠与情绪"});
        Spinner time = findViewById(R.id.consult_time);
        setSpinnerItems(time, new String[]{"工作日 09:00–12:00", "工作日 14:00–18:00", "周末 09:00–12:00", "周末 14:00–18:00"});

        EditText name = findViewById(R.id.consult_name);
        EditText phone = findViewById(R.id.consult_phone);
        EditText note = findViewById(R.id.consult_note);
        findViewById(R.id.consult_submit).setOnClickListener(view -> {
            name.setError(null);
            phone.setError(null);
            String nameValue = name.getText().toString().trim();
            String phoneValue = phone.getText().toString().trim();
            if (nameValue.isEmpty()) {
                name.setError("请留下你的称呼");
                name.requestFocus();
                return;
            }
            if (!phoneValue.matches("^1\\d{10}$")) {
                phone.setError("请填写 11 位手机号码，例如 13800000000");
                phone.requestFocus();
                return;
            }

            long timestamp = System.currentTimeMillis();
            JSONObject request = new JSONObject();
            try {
                request.put("timestamp", timestamp);
                request.put("name", nameValue);
                request.put("phone", phoneValue);
                request.put("topic", topic.getSelectedItem().toString());
                request.put("time", time.getSelectedItem().toString());
                request.put("note", note.getText().toString().trim());
                request.put("demo", true);
            } catch (JSONException exception) {
                Toast.makeText(this, "暂时无法保存，请重试", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences preferences = getSharedPreferences("consultations", MODE_PRIVATE);
            preferences.edit().putString("last_request", request.toString()).apply();

            findViewById(R.id.consult_form).setVisibility(View.GONE);
            findViewById(R.id.consult_success).setVisibility(View.VISIBLE);
            String reference = String.format(Locale.CHINA, "本地编号 HMT%06d", timestamp % 1000000);
            ((TextView) findViewById(R.id.consult_reference)).setText(reference);
        });
    }

    private void setSpinnerItems(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
