package cn.huamantang.health;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class LibraryRepository {
    private static final List<Article> ARTICLES = Collections.unmodifiableList(Arrays.asList(
        new Article(
            "glucose-start", "糖尿病", "血糖高一点，先从这三件事开始",
            "比盯着某一次血糖更重要的，是理解饮食、活动与长期趋势之间的关系。",
            "6 分钟 · 基础知识", R.drawable.topic_diabetes,
            new String[]{
                "先看见完整的一天|血糖会随着进食、活动、睡眠和情绪变化。一次偏高或偏低，不等于全部管理失效。把测量时间、是否空腹、用餐和用药情况一起记下，才能和医生讨论有意义的线索。",
                "让餐盘先有秩序|不必追求复杂食谱。尝试让蔬菜、优质蛋白和主食各有位置，规律进餐，减少含糖饮料。具体主食量与用药安排，应结合个人情况由医生或营养师评估。",
                "把活动变成日常|在身体允许的前提下，从容易坚持的步行开始。若正在使用可能引起低血糖的药物，运动前后如何监测与补充能量，需要先咨询医护人员。"
            },
            "一次数字不是结论。持续记录、规律生活，并带着记录与医生沟通，才更接近真正有效的管理。"
        ),
        new Article(
            "pressure-at-home", "高血压", "在家测血压，为什么每次都不一样？",
            "姿势、情绪、说话，甚至刚喝过的一杯咖啡，都可能让血压读数发生变化。",
            "5 分钟 · 测量方法", R.drawable.topic_bp,
            new String[]{
                "先让身体安静下来|测量前避免剧烈运动、吸烟和含咖啡因饮品，排空膀胱后安静休息至少 5 分钟。测量时保持安静，不说话，也不要反复查看结果。",
                "固定一个可重复的姿势|坐在有靠背的椅子上，双脚平放地面，背部有支撑。袖带与心脏保持同一高度，手臂自然放松。尽量在相近时段、使用同一台经过验证的设备测量。",
                "记录一组，而不是追逐一次|在医生建议下，可在同一时段间隔测量并记录。不要因为某一次读数自行增减药物；若读数显著异常或伴随胸痛、呼吸困难、剧烈头痛等症状，应及时就医。"
            },
            "可靠的趋势来自可重复的测量条件。比起反复测到满意为止，固定时间和姿势更有价值。"
        ),
        new Article(
            "lipid-report", "血脂异常", "看懂血脂报告，不只看一个箭头",
            "血脂不只是一个“高或低”，不同指标要放在年龄、疾病史和整体风险里理解。",
            "7 分钟 · 报告解读", R.drawable.topic_lipid,
            new String[]{
                "四项指标各有含义|总胆固醇反映整体水平；低密度脂蛋白胆固醇与动脉粥样硬化风险关系密切；高密度脂蛋白胆固醇不能简单理解为越高越好；甘油三酯容易受到近期饮食和饮酒影响。",
                "参考范围不等于个人目标|不同人的控制目标可能不同。既往心血管疾病、糖尿病、慢性肾病、吸烟和家族史等因素，都会影响医生对风险的判断。报告没有箭头，也不一定意味着无需管理。",
                "把报告带回完整生活中|饮食、体重、运动和药物共同影响血脂。就诊时带上既往报告和正在使用的药物清单，通常比单独询问某一个数值更有帮助。"
            },
            "血脂目标是个体化的。不要用别人的报告或参考区间，替自己决定是否需要治疗。"
        ),
        new Article(
            "uric-acid-table", "高尿酸与痛风", "高尿酸与痛风：餐桌上的选择",
            "饮食管理很重要，但痛风并不只是“吃出来”的，也不需要把生活过成一张忌口清单。",
            "6 分钟 · 饮食生活", R.drawable.topic_gout,
            new String[]{
                "先守住几个明确的方向|减少酒精，尤其是啤酒和烈酒；避免大量含糖饮料；动物内脏、部分海鲜和高嘌呤肉汤需要留意。具体限制程度应结合尿酸水平、发作情况和医生建议。",
                "不必拒绝所有蛋白质|均衡摄入蛋、奶和适量其他蛋白来源，有助于避免过度忌口造成营养不足。饮水量是否需要调整，要结合心肾功能等个人情况。",
                "急性发作时不只靠饮食|关节红、肿、热、痛时，应减少负重并及时接受专业评估。不要自行开始、停用或调整降尿酸及其他药物，复诊时主动说明发作频率和用药情况。"
            },
            "痛风管理需要饮食、体重、药物与复诊共同配合，不该只靠过度忌口。"
        ),
        new Article(
            "sleep-rhythm", "睡眠与情绪", "睡不好的夜晚，如何重新找回节律",
            "睡眠不是一场必须赢下的考试。减少用力，建立稳定线索，身体才更容易放松。",
            "5 分钟 · 生活方式", R.drawable.topic_sleep,
            new String[]{
                "先固定起床时间|相比强迫自己立刻入睡，稳定的起床时间更容易建立节律。白天适度接触自然光、安排活动，也能帮助身体区分白天与夜晚。",
                "给睡前留一段缓冲区|把强光、工作信息和刺激性食物留在卧室之外。可以尝试纸质阅读、舒缓伸展或呼吸练习，但不必把任何方法变成新的任务压力。",
                "知道何时寻求帮助|若打鼾响亮并伴随呼吸暂停、白天明显嗜睡，或失眠持续影响生活，应接受专业评估。长期情绪低落、焦虑或出现伤害自己的想法时，更要尽快寻求支持。"
            },
            "偶尔睡不好并不需要恐慌。稳定节律、减少睡前刺激，比追求“完美睡眠”更实际。"
        ),
        new Article(
            "daily-management", "健康管理", "把慢病管理，放进可坚持的一天",
            "真正有效的计划，不是改变一切，而是让重要的事情在生活里反复发生。",
            "4 分钟 · 长期管理", R.drawable.hero,
            new String[]{
                "只选三个关键动作|可以从按医嘱用药、规律记录和适量活动中，选择最需要坚持的三件事。目标越具体，越容易知道今天是否完成，也越能减少无形的心理负担。",
                "把记录留给复诊|保存指标、用药变化和不舒服的感受，复诊时按时间顺序说明。不要只在感觉不好时记录，稳定时期的信息同样有助于判断长期趋势。",
                "允许计划有调整|旅行、生病和生活变化都可能打乱节奏。及时与医护人员沟通，调整成当下能执行的版本，比因为中断而彻底放弃更重要。"
            },
            "慢病管理不是短跑。能长期重复的小行动，往往比短暂而激烈的改变更有意义。"
        )
    ));

    private LibraryRepository() {}

    public static List<Article> getAll() {
        return ARTICLES;
    }

    public static Article getById(String id) {
        for (Article article : ARTICLES) {
            if (article.getId().equals(id)) return article;
        }
        return ARTICLES.get(0);
    }

    public static List<Article> filter(String category, String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.getDefault());
        List<Article> result = new ArrayList<>();
        for (Article article : ARTICLES) {
            boolean categoryMatches = "全部".equals(category) || article.getCategory().equals(category);
            StringBuilder searchableText = new StringBuilder(article.getCategory() + " "
                    + article.getTitle() + " " + article.getSummary());
            for (String section : article.getSections()) searchableText.append(" ").append(section);
            String haystack = searchableText.toString().toLowerCase(Locale.getDefault());
            if (categoryMatches && (normalized.isEmpty() || haystack.contains(normalized))) {
                result.add(article);
            }
        }
        return result;
    }
}
