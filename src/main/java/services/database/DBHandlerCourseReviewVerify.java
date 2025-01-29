package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.objects.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DBHandlerCourseReviewVerify {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerCourseReviewVerify.class);

    public static HashMap<Integer, Review> getUnverifiedReviews() {
        HashMap<Integer, Review> reviews = new HashMap<>();
        try (Connection connection = ConnectionPoolCR.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM CourseReviews WHERE VerificationStatus=0"
            );
            ResultSet rs = ps.executeQuery();
            int key = 1;
            while (!rs.isClosed() && rs.next()) {
                reviews.put(key, new Review(
                        key++,
                        rs.getString("uniqueUserId"),
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

    public static void updateVerifiedStatus(String uniqueUserId, String courseNumber, int verificationStatus) {
        try (Connection connection = ConnectionPoolCR.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE CourseReviews SET VerificationStatus = ? WHERE UniqueUserId = ? AND CourseNumber = ?"
            );
            ps.setInt(1, verificationStatus);
            ps.setString(2, uniqueUserId);
            ps.setString(3, courseNumber);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }
}