package assets.Objects;

public class Review {
    public int key;
    public String discordUserId;
    public String ethId;
    public String review;
    public String courseNumber;
    public long timeStamp;

    public Review(int key, String discordUserId, String ethId, String review, String courseNumber, long timeStamp) {
        this.key = key;
        this.discordUserId = discordUserId;
        this.ethId = ethId;
        this.review = review;
        this.courseNumber = courseNumber;
        this.timeStamp = timeStamp;
    }
}
