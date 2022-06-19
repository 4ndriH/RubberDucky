package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Objects.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DBHandlerCourseReviewVerify {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerCourseReviewVerify.class);

    public static HashMap<Integer, Review> getUnverifiedReviews() {
        HashMap<Integer, Review> reviews = new HashMap<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM CourseReviews WHERE Verified=0"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                reviews.put(rs.getInt("Key"), new Review(
                        rs.getInt("Key"),
                        rs.getString("DiscordUserId"),
                        rs.getString("nethz"),
                        rs.getString("Review"),
                        rs.getString("CourseNumber"),
                        rs.getLong("Date")
                ));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return reviews;
    }

    public static void updateVerifiedStatus(int key, int verificationStatus) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE CourseReviews SET Verified = ? WHERE Key = ?"
            );
            ps.setInt(1, verificationStatus);
            ps.setInt(2, key);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }
}
