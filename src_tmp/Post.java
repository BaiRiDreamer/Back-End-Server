import java.sql.Timestamp;

public class Post
{
    public int post_id;
    public String title;
    public String content;
    public Timestamp posting_time;
    public int posting_city_id;
    public String author_name;
    public String filename;
    public byte[] file;
    public boolean isUnknown;
}
