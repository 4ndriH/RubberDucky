package services.startup;

import org.slf4j.Logger;

import java.util.HashMap;

import static services.database.DBHandlerStartUp.createTableIfNotExists;
import static services.database.DBHandlerStartUp.doesTableExist;

public class DatabaseVerification {
    public static void verifyDatabaseIntegrity(Logger LOGGER) {
        HashMap<String, String> database = new HashMap<>();
        boolean tableAdded = false;

        database.put("BlacklistedUsers", """
                DiscordUserId TEXT,
                PRIMARY KEY(DiscordUserId)"""
        );
        database.put("Config", """
                Key TEXT,
                Value TEXT,
                PRIMARY KEY(Key)"""
        );
        database.put("MessageDeleteTracker", """
                DiscordServerId TEXT,
                DiscordChannelId TEXT,
                DiscordMessageId TEXT,
                DeleteTime INTEGER NOT NULL,
                UptimeNumber INTEGER,
                PRIMARY KEY(DiscordServerId, DiscordChannelId, DiscordMessageId)"""
        );
        database.put("PlaceProjects", """
                Id INTEGER NOT NULL,
                Progress INTEGER DEFAULT 0,
                DiscordUserId TEXT NOT NULL,
                PRIMARY KEY(Id)"""
        );
        database.put("PlacePixels", """
                Id INTEGER NOT NULL,
                Idx INTEGER NOT NULL,
                X INTEGER NOT NULL,
                Y INTEGER NOT NULL,
                ImageColor TEXT NOT NULL,
                Alpha REAL NOT NULL,
                PlaceColor TEXT,
                FOREIGN KEY(Id) REFERENCES PlaceProjects on update cascade on delete cascade,
                PRIMARY KEY(Id, Idx)"""
        );
        database.put("WhitelistedChannels", """
                DiscordChannelId TEXT,
                Command TEXT,
                PRIMARY KEY(DiscordChannelId, Command)"""
        );
        database.put("WhitelistedServers", """
                DiscordServerId TEXT,
                PRIMARY KEY(DiscordServerId)"""
        );
        database.put("SnowflakePermissions", """
                DiscordUserId TEXT,
                DiscordServerId TEXT,
                DiscordChannelId TEXT,
                Command TEXT,
                PRIMARY KEY(DiscordUserId, DiscordServerId, DiscordChannelId, Command)"""
        );
        database.put("PlaceEfficiencyLog", """
                Key INTEGER,
                NumberOfPixels INTEGER NOT NULL DEFAULT 3600,
                SecondsTaken INTEGER NOT NULL,
                Date INTEGER DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY(Key AUTOINCREMENT)"""
        );
        database.put("EfficiencyLog", """
                PiT	INTEGER,
                EthPlaceBots INTEGER DEFAULT 0,
                CountThread	INTEGER DEFAULT 0,
                PRIMARY KEY(PiT)"""
        );

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
