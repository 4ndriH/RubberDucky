package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHandlerCourse {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerCourse.class);

    public static String getCourseName(String courseNumber) {
        String course = "";
        try (Connection connection = ConnectionPoolCR.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM Courses WHERE CourseNumber=?"
            );
            ps.setString(1, courseNumber);
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                course = rs.getString("CourseNumber") + " - " + rs.getString("CourseName");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return course;
    }
}