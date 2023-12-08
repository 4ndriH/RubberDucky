package assets.Objects;

public class Review {
    public int key;
    public String discordUserId;
    public String uniqueUserId;
    public String review;
    public String courseNumber;
    public long timeStamp;

    public Review(int key, String uniqueUserId, String review, String courseNumber, long timeStamp) {
        this.key = key;
        this.uniqueUserId = uniqueUserId;
        this.review = review;
        this.courseNumber = courseNumber;
        this.timeStamp = timeStamp;
    }
}
