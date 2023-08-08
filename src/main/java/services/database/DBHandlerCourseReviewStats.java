package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHandlerCourseReviewStats {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerCourseReviewStats.class);

    public static int getPublishedReviews() {
        int publishedReviews = 0;
        try (Connection connection = ConnectionPoolCR.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT COUNT(*) AS total FROM (SELECT * FROM CourseReviews WHERE Review IS NOT NULL AND VerificationStatus=1 OR Recommended IS NOT NULL)"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                publishedReviews = rs.getInt("total");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return publishedReviews;
    }

    public static int getReviewedCourseCount() {
        int reviewedCourseCount = 0;
        try (Connection connection = ConnectionPoolCR.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT COUNT(DISTINCT CourseNumber) AS total FROM CourseReviews WHERE VerificationStatus = 1 OR Recommended IS NOT NULL"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                reviewedCourseCount = rs.getInt("total");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return reviewedCourseCount;
    }
}
