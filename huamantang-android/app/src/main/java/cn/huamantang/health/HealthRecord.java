package cn.huamantang.health;

import java.util.Locale;

public final class HealthRecord {
    public static final String TYPE_BP = "血压";
    public static final String TYPE_GLUCOSE = "血糖";
    public static final String TYPE_WEIGHT = "体重";

    public final long id;
    public final long timestamp;
    public final String type;
    public final float value1;
    public final float value2;
    public final String note;

    public HealthRecord(long id, long timestamp, String type, float value1, float value2, String note) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
        this.note = note == null ? "" : note;
    }

    public String getUnit() {
        if (TYPE_BP.equals(type)) return "mmHg";
        if (TYPE_GLUCOSE.equals(type)) return "mmol/L";
        return "kg";
    }

    public String getDisplayValue() {
        if (TYPE_BP.equals(type)) {
            return String.format(Locale.CHINA, "%.0f/%.0f", value1, value2);
        }
        return String.format(Locale.CHINA, "%.1f", value1);
    }

    public static String validate(String type, String rawValue1, String rawValue2) {
        try {
            if (rawValue1 == null || rawValue1.trim().isEmpty()) return "请填写测量数值";
            float value1 = Float.parseFloat(rawValue1.trim());
            if (!Float.isFinite(value1)) return "请输入有效数值";
            if (TYPE_BP.equals(type)) {
                if (rawValue2 == null || rawValue2.trim().isEmpty()) return "请填写舒张压（低压）";
                float value2 = Float.parseFloat(rawValue2.trim());
                if (!Float.isFinite(value2)) return "请输入有效数值";
                if (value1 < 60 || value1 > 260 || value2 < 30 || value2 > 180) {
                    return "请核对血压数值，收缩压范围 60–260，舒张压范围 30–180";
                }
                if (value1 <= value2) return "收缩压（高压）应高于舒张压（低压）";
            } else if (TYPE_GLUCOSE.equals(type)) {
                if (value1 < 0.5f || value1 > 50f) return "请核对血糖数值，可记录范围为 0.5–50.0 mmol/L";
            } else if (TYPE_WEIGHT.equals(type)) {
                if (value1 < 20 || value1 > 300) return "请核对体重数值，可记录范围为 20–300 kg";
            } else {
                return "请选择有效的指标类型";
            }
            return null;
        } catch (NumberFormatException exception) {
            return "请输入有效数字，不要包含单位";
        }
    }
}
