package cn.huamantang.health;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ArticleActivity extends AppCompatActivity {
    public static final String EXTRA_ARTICLE_ID = "article_id";
    private Article article;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        SystemBars.apply(this, findViewById(R.id.article_root_view), null);
        preferences = getSharedPreferences("content_preferences", MODE_PRIVATE);
        String id = getIntent().getStringExtra(EXTRA_ARTICLE_ID);
        article = LibraryRepository.getById(id);

        ((ImageView) findViewById(R.id.article_hero)).setImageResource(article.getImageRes());
        ((TextView) findViewById(R.id.article_detail_category)).setText(article.getCategory());
        ((TextView) findViewById(R.id.article_detail_title)).setText(article.getTitle());
        ((TextView) findViewById(R.id.article_detail_meta)).setText("华曼棠医学编辑 · " + article.getReadTime());
        ((TextView) findViewById(R.id.article_summary)).setText(article.getSummary());
        ((TextView) findViewById(R.id.article_key_point)).setText(article.getKeyPoint());
        findViewById(R.id.article_back).setOnClickListener(view -> finish());
        findViewById(R.id.article_consult).setOnClickListener(view ->
                startActivity(new Intent(this, ConsultActivity.class)));

        LinearLayout body = findViewById(R.id.article_body);
        for (String section : article.getSections()) {
            String[] parts = section.split("\\|", 2);
            if (parts.length != 2) continue;
            TextView heading = new TextView(this);
            heading.setText(parts[0]);
            heading.setTextAppearance(R.style.TextAppearance_Huamantang_Section);
            heading.setPadding(0, 0, 0, dp(12));
            body.addView(heading);

            TextView paragraph = new TextView(this);
            paragraph.setText(parts[1]);
            paragraph.setTextAppearance(R.style.TextAppearance_Huamantang_Body);
            paragraph.setTextSize(16);
            paragraph.setLineSpacing(dp(7), 1f);
            paragraph.setTextIsSelectable(true);
            paragraph.setPadding(0, 0, 0, dp(28));
            body.addView(paragraph);
        }

        MaterialButton save = findViewById(R.id.article_save);
        updateSaveButton(save);
        save.setOnClickListener(view -> {
            Set<String> bookmarks = new HashSet<>(preferences.getStringSet("bookmarks", Collections.emptySet()));
            if (bookmarks.contains(article.getId())) {
                bookmarks.remove(article.getId());
            } else {
                bookmarks.add(article.getId());
            }
            preferences.edit().putStringSet("bookmarks", bookmarks).apply();
            updateSaveButton(save);
        });
    }

    private void updateSaveButton(MaterialButton button) {
        Set<String> bookmarks = preferences.getStringSet("bookmarks", Collections.emptySet());
        boolean saved = bookmarks != null && bookmarks.contains(article.getId());
        button.setText(saved ? "已收藏 · 点击取消" : "收藏文章");
        button.setIconResource(saved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark);
        button.setContentDescription(saved ? "取消收藏" + article.getTitle() : "收藏" + article.getTitle());
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
