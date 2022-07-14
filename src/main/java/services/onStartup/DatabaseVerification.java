package services.onStartup;

import org.slf4j.Logger;

import java.util.HashMap;

import static services.database.DBHandlerStartUp.createTableIfNotExists;
import static services.database.DBHandlerStartUp.doesTableExist;

public class DatabaseVerification {
    public static void verifyDatabaseIntegrity(Logger LOGGER) {
        HashMap<String, String> database = new HashMap<>();
        boolean tableAdded = false;

        database.put("ApiUsers",
                "Username TEXT NOT NULL," +
                "Password TEXT NOT NULL," +
                "Whitelisted INTEGER DEFAULT 1," +
                "PRIMARY KEY(Username)"
        );
        database.put("BlacklistedUsers",
                "DiscordUserId TEXT," +
                "PRIMARY KEY(DiscordUserId)"
        );
        database.put("Config",
                "Key TEXT," +
                "Value TEXT," +
                "PRIMARY KEY(Key)"
        );
        database.put("CourseReviews",
                "Key INTEGER," +
                "DiscordUserId TEXT," +
                "nethz TEXT," +
                "Review TEXT NOT NULL," +
                "CourseNumber TEXT NOT NULL," +
                "Verified INTEGER NOT NULL DEFAULT 0," +
                "Date INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY(Key)"
        );
        database.put("Courses",
                "CourseNumber TEXT," +
                "CourseName TEXT NOT NULL," +
                "PRIMARY KEY(CourseNumber)"
        );
        database.put("MessageDeleteTracker",
                "DiscordServerId TEXT," +
                "DiscordChannelId TEXT," +
                "DiscordMessageId TEXT," +
                "DeleteTime INTEGER NOT NULL," +
                "UptimeNumber INTEGER," +
                "PRIMARY KEY(DiscordServerId, DiscordChannelId, DiscordMessageId)"
        );
        database.put("PlaceProjects",
                "Id INTEGER NOT NULL," +
                "Progress INTEGER DEFAULT 0," +
                "DiscordUserId TEXT NOT NULL," +
                "PRIMARY KEY(Id)"
        );
        database.put("PlacePixels",
                "Id INTEGER NOT NULL," +
                "Idx INTEGER NOT NULL," +
                "X INTEGER NOT NULL," +
                "Y INTEGER NOT NULL," +
                "ImageColor TEXT NOT NULL," +
                "Alpha REAL NOT NULL," +
                "PlaceColor TEXT," +
                "FOREIGN KEY(Id) REFERENCES PlaceProjects on update cascade on delete cascade," +
                "PRIMARY KEY(Id, Idx)"
        );
        database.put("SpokesPeople",
                "user TEXT NOT NULL UNIQUE," +
                "subject TEXT NOT NULL," +
                "year TEXT NOT NULL," +
                "PRIMARY KEY(user)"
        );
        database.put("WhitelistedChannels",
                "DiscordChannelId TEXT," +
                "Command TEXT," +
                "PRIMARY KEY(DiscordChannelId, Command)"
        );
        database.put("WhitelistedServers",
                "DiscordServerId TEXT," +
                "PRIMARY KEY(DiscordServerId)"
        );
        database.put("SnowflakePermissions",
                "DiscordUserId TEXT," +
                "DiscordServerId TEXT," +
                "DiscordChannelId TEXT," +
                "Command TEXT," +
                "PRIMARY KEY(DiscordUserId, DiscordServerId, DiscordChannelId, Command)"
        );
        database.put("PinghellHQ",
                "DiscordUserId TEXT," +
                "PinghellStatus INTEGER NOT NULL DEFAULT 1," +
                "ServerMember INTEGER NOT NULL DEFAULT 0," +
                "PRIMARY KEY(DiscordUserId)");

        for (String table : database.keySet()) {
            if (!doesTableExist(table)) {
                createTableIfNotExists(table, database.get(table));
                tableAdded = true;
                LOGGER.info("Table " + table + " has been created");
            }
        }

        if (!tableAdded) {
            LOGGER.info("DB Table verification completed");
        }
    }
}
