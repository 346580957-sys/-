package cn.huamantang.health;

public final class Article {
    private final String id;
    private final String category;
    private final String title;
    private final String summary;
    private final String readTime;
    private final int imageRes;
    private final String[] sections;
    private final String keyPoint;

    public Article(String id, String category, String title, String summary, String readTime,
                   int imageRes, String[] sections, String keyPoint) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.summary = summary;
        this.readTime = readTime;
        this.imageRes = imageRes;
        this.sections = sections;
        this.keyPoint = keyPoint;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public String getReadTime() { return readTime; }
    public int getImageRes() { return imageRes; }
    public String[] getSections() { return sections; }
    public String getKeyPoint() { return keyPoint; }
}
