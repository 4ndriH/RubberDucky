package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class DBHandlerCourse {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerCourse.class);

    public static String getCourseName(String courseNumber) {
        String name = "";
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM Courses WHERE CourseNumber=?"
            );
            ps.setString(1, courseNumber);
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                name = courseNumber + " - " + rs.getString("CourseName");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return name;
    }

    public static HashSet<String> getCoursesWithVerifiedReviews() {
        HashSet<String> reviews = new HashSet<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM CourseReviews WHERE Verified=1"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                reviews.add(rs.getString("CourseNumber"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return reviews;
    }

    public static ArrayList<String> getReviewsForCourse(String courseNumber) {
        ArrayList<String> reviews = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM CourseReviews WHERE CourseNumber=? AND Verified=1"
            );
            ps.setString(1, courseNumber);
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                reviews.add(rs.getString("review"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return reviews;
    }
}
