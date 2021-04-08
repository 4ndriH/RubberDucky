package services.database;

import java.sql.*;
import java.util.HashMap;

public class dbHandlerSpokesPeople {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Connection connectToDB () {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:DB/spokespeople.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static String getPeople (int year) {
        HashMap<String, String> assigned = new HashMap<>();
         String result = "";
        try {
            PreparedStatement getStatement = connectToDB().prepareStatement(
                    "SELECT * FROM people WHERE year = " + year
            );
            ResultSet rs = getStatement.executeQuery();

            while (rs.next()) {
                String subject = "**" + rs.getString("subject") + ":**";
                String student = "<@!" + rs.getString("id") + ">";

                if (!assigned.keySet().contains(subject))
                    assigned.put(subject, student);
                else
                    assigned.put(subject, assigned.get(subject) + " \n " + student);
            }

            for (String key : assigned.keySet())
                result += key + " \n " + assigned.get(key) + " \n ";
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
}
