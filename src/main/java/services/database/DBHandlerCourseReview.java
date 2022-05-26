package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBHandlerCourseReview {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerCourseReview.class);

    public static void insertCourse(String courseNumber, String courseName) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Courses (CourseNumber, CourseName) VALUES (?, ?)"
            );
            ps.setString(1, courseNumber);
            ps.setString(2, (courseName.length() == 0 ? "Could not find Course!" : courseName));
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void insertCourseReview(String id, String review, String courseNumber) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO CourseReviews (DiscordUserId, Review, CourseNumber, Date) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, id);
            ps.setString(2, review);
            ps.setString(3, courseNumber);
            ps.setLong(4, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static boolean containsCourseNumber(String courseNumber) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT EXISTS(SELECT 1 FROM Courses WHERE CourseNumber=?) AS containsCheck"
            );
            ps.setString(1, courseNumber);
            return ps.executeQuery().getInt("containsCheck") > 0;
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return false;
    }
}
