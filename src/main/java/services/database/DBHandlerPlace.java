package services.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Objects.Pixel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class DBHandlerPlace {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandlerPlace.class);

    public static void insertProjectIntoQueue(int id, String discordUserId, ArrayList<Pixel> pixels) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO PlaceProjects (Id, DiscordUserId) VALUES (?, ?)"
            );
            ps.setInt(1, id);
            ps.setString(2, discordUserId);
            ps.executeUpdate();

            connection.setAutoCommit(false);
            ps = connection.prepareStatement(
                    "INSERT INTO PlacePixels (Id, Idx, X, Y, ImageColor, Alpha) VALUES (?, ?, ?, ?, ?, ?)"
            );
            int idx = 1;
            for (Pixel pixel : pixels) {
                ps.setInt(1, id);
                ps.setInt(2, idx++);
                ps.setInt(3, pixel.getX());
                ps.setInt(4, pixel.getY());
                ps.setString(5, pixel.getImageColor());
                ps.setDouble(6, pixel.getAlpha());
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void insertTimeTaken(int secondsTaken) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO PlaceEfficiencyLog (SecondsTaken) VALUES (?)"
            );
            ps.setInt(1, secondsTaken);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void removeProjectFromQueue(int key) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM PlaceProjects WHERE Id = ?"
            );
            ps.setInt(1, key);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static String[] getPlaceProjectQueue() {
        String[] strings = new String[]{"", "", ""};
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM PlaceProjects"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                strings[0] += rs.getInt("Id") + "\n";
                strings[1] += String.format(Locale.US, "%,d", rs.getInt("Progress")).replace(',', '\'') + "\n";
                strings[2] += "<@!" + rs.getString("DiscordUserId") + ">\n";
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return strings;
    }

    public static ArrayList<Integer> getPlaceProjectIDs() {
        ArrayList<Integer> ids = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT Id FROM PlaceProjects"
            );
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                ids.add(rs.getInt("Id"));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return ids;
    }

    public static int getNextProject() {
        ArrayList<Integer> ids = getPlaceProjectIDs();
        return ids.size() == 0 ? -1 : Collections.min(ids);
    }

    public static String getProjectAuthor(int id) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM PlaceProjects WHERE Id = ?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.isClosed() && rs.next()) {
                return rs.getString("DiscordUserId");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return "";
    }

    public static int getProjectProgress(int id) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM PlaceProjects WHERE Id = ?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.isClosed() && rs.next()) {
                return rs.getInt("Progress");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return 0;
    }

    public static ArrayList<Pixel> getProjectPixels(int id) {
        ArrayList<Pixel> pixels = new ArrayList<>();
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM PlacePixels WHERE Id = ?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                pixels.add(new Pixel(
                        rs.getInt("X"),
                        rs.getInt("Y"),
                        rs.getDouble("Alpha"),
                        rs.getString("ImageColor"),
                        rs.getString("PlaceColor")
                ));
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return pixels;
    }

    public static int getPixelsInQueue() {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT (SELECT COUNT(*) FROM PlacePixels) - (SELECT SUM(Progress) FROM PlaceProjects WHERE Progress > 0) AS pixelsInQueue"
            );
            ResultSet rs = ps.executeQuery();
            if (!rs.isClosed() && rs.next()) {
                return rs.getInt("pixelsInQueue");
            }
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
        return 0;
    }

    public static void updateProgress(int id, int progress) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE PlaceProjects SET Progress = ? WHERE Id = ?"
            );
            ps.setInt(1, progress);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }

    public static void updatePixelPlaceColor(double alpha, int x, int y, String imageColor, String placeColor) {
        try (Connection connection = ConnectionPool.getConnection()){
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE PlacePixels SET PlaceColor = ? WHERE X = ? AND Y = ? AND ImageColor = ? AND Alpha = ?"
            );
            ps.setString(1, placeColor);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setString(4, imageColor);
            ps.setDouble(5, alpha);
            ps.executeUpdate();
        } catch (SQLException sqlE) {
            LOGGER.error("SQL Exception", sqlE);
        }
    }
}