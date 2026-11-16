package cn.huamantang.health;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class MainActivity extends AppCompatActivity {
    private final View[] tabs = new View[4];
    private FrameLayout container;
    private BottomNavigationView bottomNavigation;
    private HealthRecordStore recordStore;
    private SharedPreferences contentPreferences;
    private SharedPreferences planPreferences;
    private int selectedTab = -1;
    private boolean bookmarksOnly = false;
    private String libraryCategory = "全部";
    private String recordFilter = "全部";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.tab_container);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        SystemBars.apply(this, findViewById(R.id.main_root), bottomNavigation);
        recordStore = new HealthRecordStore(this);
        contentPreferences = getSharedPreferences("content_preferences", MODE_PRIVATE);
        planPreferences = getSharedPreferences("daily_plan", MODE_PRIVATE);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int index = navigationIndex(item.getItemId());
            if (index < 0) return false;
            if (index == 1) bookmarksOnly = false;
            showTab(index);
            return true;
        });
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        if (selectedTab == -1) showTab(0);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (selectedTab > 0) {
                    bottomNavigation.setSelectedItemId(R.id.nav_home);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabs[0] != null) refreshHome();
        if (tabs[1] != null) refreshLibrary();
        if (tabs[2] != null) refreshRecords();
        if (tabs[3] != null) refreshProfile();
    }

    private int navigationIndex(int itemId) {
        if (itemId == R.id.nav_home) return 0;
        if (itemId == R.id.nav_library) return 1;
        if (itemId == R.id.nav_records) return 2;
        if (itemId == R.id.nav_profile) return 3;
        return -1;
    }

    private void showTab(int index) {
        if (tabs[index] == null) {
            int layout = index == 0 ? R.layout.view_home
                    : index == 1 ? R.layout.view_library
                    : index == 2 ? R.layout.view_records : R.layout.view_profile;
            tabs[index] = getLayoutInflater().inflate(layout, container, false);
            if (index == 0) bindHome(tabs[index]);
            if (index == 1) bindLibrary(tabs[index]);
            if (index == 2) bindRecords(tabs[index]);
            if (index == 3) bindProfile(tabs[index]);
        }
        container.removeAllViews();
        container.addView(tabs[index]);
        selectedTab = index;
        if (index == 0) refreshHome();
        if (index == 1) refreshLibrary();
        if (index == 2) refreshRecords();
        if (index == 3) refreshProfile();
    }

    private void bindHome(View root) {
        TextView date = root.findViewById(R.id.home_date);
        date.setText(new SimpleDateFormat("M月d日 EEEE", Locale.CHINA).format(new Date()));
        root.findViewById(R.id.home_add_record).setOnClickListener(view -> showRecordDialog(HealthRecord.TYPE_BP));
        root.findViewById(R.id.home_view_library).setOnClickListener(view ->
                bottomNavigation.setSelectedItemId(R.id.nav_library));
        root.findViewById(R.id.home_consult).setOnClickListener(view -> openConsultation());
        bindDailyTask(root.findViewById(R.id.task_medicine), "medicine");
        bindDailyTask(root.findViewById(R.id.task_walk), "walk");
        bindDailyTask(root.findViewById(R.id.task_measure), "measure");
    }

    private void bindDailyTask(CheckBox checkBox, String task) {
        checkBox.setChecked(planPreferences.getBoolean(dailyTaskKey(task), false));
        checkBox.setOnCheckedChangeListener((button, checked) ->
                planPreferences.edit().putBoolean(dailyTaskKey(task), checked).apply());
    }

    private String dailyTaskKey(String task) {
        String day = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        return day + "_" + task;
    }

    private void markMeasurementTaskComplete() {
        planPreferences.edit().putBoolean(dailyTaskKey("measure"), true).apply();
        if (tabs[0] != null) {
            CheckBox checkBox = tabs[0].findViewById(R.id.task_measure);
            checkBox.setChecked(true);
        }
    }

    private void refreshHome() {
        View root = tabs[0];
        HealthRecord pressure = recordStore.latest(HealthRecord.TYPE_BP);
        HealthRecord glucose = recordStore.latest(HealthRecord.TYPE_GLUCOSE);
        HealthRecord weight = recordStore.latest(HealthRecord.TYPE_WEIGHT);
        ((TextView) root.findViewById(R.id.home_bp)).setText(pressure == null ? "--" : pressure.getDisplayValue());
        ((TextView) root.findViewById(R.id.home_glucose)).setText(glucose == null ? "--" : glucose.getDisplayValue());
        ((TextView) root.findViewById(R.id.home_weight)).setText(weight == null ? "--" : weight.getDisplayValue());

        List<HealthRecord> records = recordStore.getAll();
        TextView latest = root.findViewById(R.id.home_latest);
        if (records.isEmpty()) {
            latest.setText("还没有记录，从第一笔开始了解自己");
        } else {
            String time = new SimpleDateFormat("M月d日 HH:mm", Locale.CHINA)
                    .format(new Date(records.get(0).timestamp));
            latest.setText("最近记录 · " + records.get(0).type + " · " + time);
        }

        LinearLayout articles = root.findViewById(R.id.home_articles);
        articles.removeAllViews();
        List<Article> allArticles = LibraryRepository.getAll();
        for (int index = 0; index < 3; index++) {
            articles.addView(createArticleView(allArticles.get(index), articles));
        }
    }

    private void bindLibrary(View root) {
        EditText search = root.findViewById(R.id.library_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence text, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence text, int start, int before, int count) {
                refreshLibrary();
            }
            @Override public void afterTextChanged(Editable text) {}
        });
        ChipGroup filters = root.findViewById(R.id.library_filters);
        filters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.filter_all) libraryCategory = "全部";
            if (id == R.id.filter_diabetes) libraryCategory = "糖尿病";
            if (id == R.id.filter_bp) libraryCategory = "高血压";
            if (id == R.id.filter_lipid) libraryCategory = "血脂异常";
            if (id == R.id.filter_gout) libraryCategory = "高尿酸与痛风";
            if (id == R.id.filter_sleep) libraryCategory = "睡眠与情绪";
            refreshLibrary();
        });
        root.findViewById(R.id.library_scope).setOnClickListener(view -> {
            bookmarksOnly = false;
            refreshLibrary();
        });
    }

    private void refreshLibrary() {
        View root = tabs[1];
        String query = ((EditText) root.findViewById(R.id.library_search)).getText().toString();
        List<Article> filtered = LibraryRepository.filter(libraryCategory, query);
        Set<String> bookmarks = getBookmarks();
        List<Article> visible = new ArrayList<>();
        for (Article article : filtered) {
            if (!bookmarksOnly || bookmarks.contains(article.getId())) visible.add(article);
        }

        LinearLayout list = root.findViewById(R.id.library_list);
        list.removeAllViews();
        for (Article article : visible) list.addView(createArticleView(article, list));
        TextView empty = root.findViewById(R.id.library_empty);
        empty.setVisibility(visible.isEmpty() ? View.VISIBLE : View.GONE);
        if (bookmarksOnly && visible.isEmpty()) {
            empty.setText("还没有符合条件的收藏\n阅读文章时点击“收藏文章”即可保存");
        } else {
            empty.setText("没有找到相关内容\n换个关键词试试看");
        }
        ((TextView) root.findViewById(R.id.library_result_count))
                .setText(visible.size() + " 篇循证科普");
        root.findViewById(R.id.library_scope).setVisibility(bookmarksOnly ? View.VISIBLE : View.GONE);
    }

    private View createArticleView(Article article, ViewGroup parent) {
        View view = getLayoutInflater().inflate(R.layout.item_article, parent, false);
        ((ImageView) view.findViewById(R.id.article_image)).setImageResource(article.getImageRes());
        ((TextView) view.findViewById(R.id.article_category)).setText(article.getCategory());
        ((TextView) view.findViewById(R.id.article_title)).setText(article.getTitle());
        ((TextView) view.findViewById(R.id.article_meta)).setText(article.getReadTime());
        view.findViewById(R.id.article_saved).setVisibility(
                getBookmarks().contains(article.getId()) ? View.VISIBLE : View.GONE);
        view.setContentDescription(article.getCategory() + "，" + article.getTitle() + "，" + article.getReadTime());
        view.setOnClickListener(clicked -> {
            Intent intent = new Intent(this, ArticleActivity.class);
            intent.putExtra(ArticleActivity.EXTRA_ARTICLE_ID, article.getId());
            startActivity(intent);
        });
        return view;
    }

    private Set<String> getBookmarks() {
        Set<String> stored = contentPreferences.getStringSet("bookmarks", Collections.emptySet());
        return new HashSet<>(stored == null ? Collections.emptySet() : stored);
    }

    private void bindRecords(View root) {
        root.findViewById(R.id.record_add).setOnClickListener(view -> showRecordDialog(initialRecordType()));
        root.findViewById(R.id.record_empty_add).setOnClickListener(view -> showRecordDialog(HealthRecord.TYPE_BP));
        ChipGroup filters = root.findViewById(R.id.record_filters);
        filters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.record_filter_all) recordFilter = "全部";
            if (id == R.id.record_filter_bp) recordFilter = HealthRecord.TYPE_BP;
            if (id == R.id.record_filter_glucose) recordFilter = HealthRecord.TYPE_GLUCOSE;
            if (id == R.id.record_filter_weight) recordFilter = HealthRecord.TYPE_WEIGHT;
            refreshRecords();
        });
    }

    private String initialRecordType() {
        return "全部".equals(recordFilter) ? HealthRecord.TYPE_BP : recordFilter;
    }

    private void refreshRecords() {
        View root = tabs[2];
        List<HealthRecord> all = recordStore.getAll();
        List<HealthRecord> filtered = new ArrayList<>();
        for (HealthRecord record : all) {
            if ("全部".equals(recordFilter) || recordFilter.equals(record.type)) filtered.add(record);
        }

        ((TextView) root.findViewById(R.id.record_count)).setText("共 " + filtered.size() + " 笔");
        LinearLayout list = root.findViewById(R.id.record_list);
        list.removeAllViews();
        for (HealthRecord record : filtered) list.addView(createRecordView(record, list));
        root.findViewById(R.id.record_empty).setVisibility(all.isEmpty() ? View.VISIBLE : View.GONE);
        if (filtered.isEmpty() && !all.isEmpty()) {
            TextView message = new TextView(this);
            message.setText("这个分类还没有记录");
            message.setTextAppearance(R.style.TextAppearance_Huamantang_Caption);
            message.setGravity(android.view.Gravity.CENTER);
            message.setPadding(0, dp(30), 0, dp(12));
            list.addView(message);
        }
        updateTrend(root, all, filtered);
    }

    private View createRecordView(HealthRecord record, ViewGroup parent) {
        View view = getLayoutInflater().inflate(R.layout.item_record, parent, false);
        ((TextView) view.findViewById(R.id.record_icon)).setText(record.type.substring(0, 1));
        ((TextView) view.findViewById(R.id.record_type)).setText(record.type + " · " + record.getUnit());
        String time = new SimpleDateFormat("M月d日 HH:mm", Locale.CHINA).format(new Date(record.timestamp));
        ((TextView) view.findViewById(R.id.record_time)).setText(time);
        ((TextView) view.findViewById(R.id.record_value)).setText(record.getDisplayValue());
        TextView note = view.findViewById(R.id.record_note);
        if (!record.note.trim().isEmpty()) {
            note.setText(record.note);
            note.setVisibility(View.VISIBLE);
        }
        view.setContentDescription(record.type + "，" + record.getDisplayValue() + record.getUnit() + "，" + time);
        view.setOnClickListener(clicked -> showRecordDetails(record));
        return view;
    }

    private void updateTrend(View root, List<HealthRecord> all, List<HealthRecord> filtered) {
        List<HealthRecord> chartRecords = new ArrayList<>();
        String chartType = recordFilter;
        if ("全部".equals(chartType) && !all.isEmpty()) chartType = all.get(0).type;
        if (!"全部".equals(chartType)) {
            for (HealthRecord record : all) {
                if (chartType.equals(record.type) && chartRecords.size() < 7) chartRecords.add(record);
            }
        }
        Collections.reverse(chartRecords);
        List<Float> values = new ArrayList<>();
        for (HealthRecord record : chartRecords) values.add(record.value1);
        String unit = chartRecords.isEmpty() ? "" : chartRecords.get(0).getUnit();
        TrendView trend = root.findViewById(R.id.record_trend);
        trend.setValues(values, unit);
        trend.setContentDescription(chartType + "趋势图，共" + values.size() + "个数据点");
        root.findViewById(R.id.record_trend_empty).setVisibility(values.isEmpty() ? View.VISIBLE : View.GONE);
        TextView label = root.findViewById(R.id.record_trend_label);
        if (chartRecords.isEmpty()) {
            label.setText("添加记录后，这里会呈现变化趋势");
        } else {
            label.setText(chartType + (HealthRecord.TYPE_BP.equals(chartType) ? " · 收缩压" : "")
                    + " · 最近 " + chartRecords.size() + " 笔（按时间先后）");
        }
    }

    private void showRecordDetails(HealthRecord record) {
        String time = new SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.CHINA).format(new Date(record.timestamp));
        StringBuilder message = new StringBuilder(time).append("\n\n数值：")
                .append(record.getDisplayValue()).append(" ").append(record.getUnit());
        if (!record.note.trim().isEmpty()) message.append("\n备注：").append(record.note);
        new AlertDialog.Builder(this)
                .setTitle(record.type + "记录")
                .setMessage(message.toString())
                .setPositiveButton("知道了", null)
                .setNegativeButton("删除", (dialog, which) -> confirmDeleteRecord(record))
                .show();
    }

    private void confirmDeleteRecord(HealthRecord record) {
        new AlertDialog.Builder(this)
                .setTitle("删除这条记录？")
                .setMessage("删除后无法恢复，其他健康记录不受影响。")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认删除", (dialog, which) -> {
                    recordStore.delete(record.id);
                    refreshHome();
                    refreshRecords();
                    Toast.makeText(this, "记录已删除", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showRecordDialog(String initialType) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_record, null);
        dialog.setContentView(view);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        Spinner typeInput = view.findViewById(R.id.record_type_input);
        String[] types = {HealthRecord.TYPE_BP, HealthRecord.TYPE_GLUCOSE, HealthRecord.TYPE_WEIGHT};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeInput.setAdapter(adapter);
        int initialIndex = HealthRecord.TYPE_GLUCOSE.equals(initialType) ? 1
                : HealthRecord.TYPE_WEIGHT.equals(initialType) ? 2 : 0;
        typeInput.setSelection(initialIndex);

        TextInputLayout value1Layout = view.findViewById(R.id.record_value1_layout);
        TextInputLayout value2Layout = view.findViewById(R.id.record_value2_layout);
        EditText value1 = view.findViewById(R.id.record_value1);
        EditText value2 = view.findViewById(R.id.record_value2);
        EditText note = view.findViewById(R.id.record_note_input);
        TextView hint = view.findViewById(R.id.record_input_hint);

        typeInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selected, int position, long id) {
                String type = types[position];
                value1Layout.setError(null);
                value2Layout.setError(null);
                if (HealthRecord.TYPE_BP.equals(type)) {
                    value1Layout.setHint("收缩压（高压）mmHg");
                    value2Layout.setVisibility(View.VISIBLE);
                    hint.setText("测量前安静休息 5 分钟，保持手臂与心脏同高。");
                } else if (HealthRecord.TYPE_GLUCOSE.equals(type)) {
                    value1Layout.setHint("血糖 mmol/L");
                    value2Layout.setVisibility(View.GONE);
                    hint.setText("建议在备注中写明空腹、餐前或餐后等测量状态。");
                } else {
                    value1Layout.setHint("体重 kg");
                    value2Layout.setVisibility(View.GONE);
                    hint.setText("尽量使用同一台秤，在相近时段和状态下测量。");
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        view.findViewById(R.id.record_dialog_close).setOnClickListener(clicked -> dialog.dismiss());
        view.findViewById(R.id.record_save).setOnClickListener(clicked -> {
            String type = typeInput.getSelectedItem().toString();
            String raw1 = value1.getText().toString().trim();
            String raw2 = value2.getText().toString().trim();
            value1Layout.setError(null);
            value2Layout.setError(null);
            String error = HealthRecord.validate(type, raw1, raw2);
            if (error != null) {
                if (HealthRecord.TYPE_BP.equals(type) && (raw2.isEmpty() || error.contains("舒张压"))) {
                    value2Layout.setError(error);
                } else {
                    value1Layout.setError(error);
                }
                return;
            }
            float number2 = HealthRecord.TYPE_BP.equals(type) ? Float.parseFloat(raw2) : 0f;
            recordStore.add(type, Float.parseFloat(raw1), number2, note.getText().toString().trim());
            markMeasurementTaskComplete();
            dialog.dismiss();
            refreshHome();
            if (tabs[2] != null) refreshRecords();
            Toast.makeText(this, "已保存到本机", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }

    private void bindProfile(View root) {
        root.findViewById(R.id.profile_bookmarks).setOnClickListener(view -> {
            bottomNavigation.setSelectedItemId(R.id.nav_library);
            bookmarksOnly = true;
            refreshLibrary();
        });
        root.findViewById(R.id.profile_consult).setOnClickListener(view -> openConsultation());
        root.findViewById(R.id.profile_privacy).setOnClickListener(view -> showInfo(
                "数据与隐私",
                "健康记录、收藏和每日计划使用 Android 应用私有存储。本应用不申请联网权限，不会上传这些数据。\n\n卸载应用或清除本机记录后，数据无法恢复。演示预约同样只保存在本机。"
        ));
        root.findViewById(R.id.profile_disclaimer).setOnClickListener(view -> showInfo(
                "健康科普声明",
                "华曼棠提供的内容用于健康科普和自我记录，不构成诊断、处方或个体化治疗建议。请勿根据应用内容自行开始、停用或调整药物。\n\n如出现胸痛、呼吸困难、意识异常等紧急症状，请立即联系急救服务。"
        ));
        root.findViewById(R.id.profile_about).setOnClickListener(view -> showInfo(
                "关于华曼棠",
                "华曼棠慢病智库 · 版本 1.0.0\n\n让慢病知识回到生活，让每一次记录都有意义。\n\n当前版本为功能演示，专家资料与预约流程仅作展示，尚未连接真实医疗服务。"
        ));
        root.findViewById(R.id.profile_clear).setOnClickListener(view -> {
            if (recordStore.getAll().isEmpty()) {
                Toast.makeText(this, "当前没有健康记录", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("清除全部健康记录？")
                    .setMessage("血压、血糖和体重记录将从此设备删除，且无法恢复。文章收藏不会被清除。")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("全部清除", (dialog, which) -> {
                        recordStore.clear();
                        refreshHome();
                        if (tabs[2] != null) refreshRecords();
                        Toast.makeText(this, "本机健康记录已清除", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });
    }

    private void refreshProfile() {
        int count = getBookmarks().size();
        ((TextView) tabs[3].findViewById(R.id.profile_bookmark_count)).setText(count + " 篇精选内容");
    }

    private void showInfo(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("知道了", null)
                .show();
    }

    private void openConsultation() {
        startActivity(new Intent(this, ConsultActivity.class));
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
