package cn.huamantang.health;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class TrendView extends View {
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Float> values = new ArrayList<>();
    private String unit = "";

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        linePaint.setColor(Color.rgb(69, 108, 86));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dp(2.5f));
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        fillPaint.setColor(Color.argb(24, 69, 108, 86));
        fillPaint.setStyle(Paint.Style.FILL);
        gridPaint.setColor(Color.argb(32, 21, 53, 43));
        gridPaint.setStrokeWidth(dp(1));
        textPaint.setColor(Color.rgb(110, 119, 112));
        textPaint.setTextSize(dp(11));
        textPaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setValues(List<Float> newValues, String unit) {
        values.clear();
        values.addAll(newValues);
        this.unit = unit;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left = dp(8);
        float right = getWidth() - dp(12);
        float top = dp(28);
        float bottom = getHeight() - dp(22);
        if (right <= left || bottom <= top) return;

        for (int index = 0; index < 3; index++) {
            float y = top + (bottom - top) * index / 2f;
            canvas.drawLine(left, y, right, y, gridPaint);
        }
        if (values.isEmpty()) return;

        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;
        for (float value : values) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        float padding = Math.max((max - min) * 0.25f, "mmHg".equals(unit) ? 4f : 0.5f);
        min -= padding;
        max += padding;
        if (max == min) max = min + 1f;

        List<Float> xValues = new ArrayList<>();
        List<Float> yValues = new ArrayList<>();
        for (int index = 0; index < values.size(); index++) {
            float x = values.size() == 1 ? (left + right) / 2f : left + (right - left) * index / (values.size() - 1f);
            float y = bottom - (values.get(index) - min) / (max - min) * (bottom - top);
            xValues.add(x);
            yValues.add(y);
        }

        if (values.size() > 1) {
            Path area = new Path();
            area.moveTo(xValues.get(0), bottom);
            for (int index = 0; index < values.size(); index++) {
                area.lineTo(xValues.get(index), yValues.get(index));
            }
            area.lineTo(xValues.get(values.size() - 1), bottom);
            area.close();
            canvas.drawPath(area, fillPaint);

            Path line = new Path();
            line.moveTo(xValues.get(0), yValues.get(0));
            for (int index = 1; index < values.size(); index++) {
                line.lineTo(xValues.get(index), yValues.get(index));
            }
            canvas.drawPath(line, linePaint);
        }

        for (int index = 0; index < values.size(); index++) {
            boolean latest = index == values.size() - 1;
            linePaint.setStyle(Paint.Style.FILL);
            linePaint.setColor(latest ? Color.rgb(21, 53, 43) : Color.rgb(69, 108, 86));
            canvas.drawCircle(xValues.get(index), yValues.get(index), dp(latest ? 5 : 3), linePaint);
        }
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.rgb(69, 108, 86));

        float latestValue = values.get(values.size() - 1);
        String label = String.format(Locale.CHINA, latestValue == Math.floor(latestValue) ? "%.0f" : "%.1f", latestValue) + " " + unit;
        canvas.drawText(label, right, dp(15), textPaint);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
