package cn.huamantang.health;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class HealthRecordTest {
    @Test
    public void acceptsValidBloodPressure() {
        assertNull(HealthRecord.validate(HealthRecord.TYPE_BP, "122", "78"));
        HealthRecord record = new HealthRecord(1, 1, HealthRecord.TYPE_BP, 122, 78, "");
        assertEquals("122/78", record.getDisplayValue());
        assertEquals("mmHg", record.getUnit());
    }

    @Test
    public void rejectsInvertedBloodPressure() {
        String error = HealthRecord.validate(HealthRecord.TYPE_BP, "70", "90");
        assertTrue(error.contains("应高于"));
    }

    @Test
    public void rejectsMissingDiastolicPressure() {
        String error = HealthRecord.validate(HealthRecord.TYPE_BP, "120", "");
        assertTrue(error.contains("舒张压"));
    }

    @Test
    public void validatesGlucoseAndWeight() {
        assertNull(HealthRecord.validate(HealthRecord.TYPE_GLUCOSE, "5.6", ""));
        assertNull(HealthRecord.validate(HealthRecord.TYPE_WEIGHT, "62.4", ""));
        assertTrue(HealthRecord.validate(HealthRecord.TYPE_GLUCOSE, "abc", "").contains("有效数字"));
        assertTrue(HealthRecord.validate(HealthRecord.TYPE_WEIGHT, "0", "").contains("范围"));
    }
}
