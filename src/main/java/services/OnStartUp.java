package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.database.ConnectionPool;

import java.io.File;

public class OnStartUp {
    private final Logger LOGGER = LoggerFactory.getLogger(OnStartUp.class);

    public OnStartUp() {
        DirectoryVerification();
        new CoolDownManager();
        new ConnectionPool();
        CONFIG.reload();
    }

    private void DirectoryVerification() {
        StringBuilder sb = new StringBuilder();
        File DB = new File("DB");
        File resources = new File("resources");
        File logs = new File("resources/logs");
        File purge = new File("resources/purge");
        File tempFiles = new File("tempFiles");
        File place = new File("tempFiles/place");
        File queue = new File("tempFiles/place/queue");
        File encode = new File("tempFiles/place/encode");

        if (!DB.isDirectory()) {
            DB.mkdir();
            sb.append(", DB");
        }
        if (!resources.isDirectory()) {
            resources.mkdir();
            sb.append(", resources");
        }
        if (!logs.isDirectory()) {
            logs.mkdir();
            sb.append(", logs");
        }
        if (!purge.isDirectory()) {
            purge.mkdir();
            sb.append(", purge");
        }
        if (!tempFiles.isDirectory()) {
            tempFiles.mkdir();
            sb.append(", tempFiles");
        }
        if (!place.isDirectory()) {
            place.mkdir();
            sb.append(", place");
        }
        if (!queue.isDirectory()) {
            queue.mkdir();
            sb.append(", queue");
        }
        if (!encode.isDirectory()) {
            encode.mkdir();
            sb.append(", encode");
        }
        if (sb.length() > 0) {
            String s = sb.substring(2);
            LOGGER.info("The following Directories have been created: " + s);
        }
        //make important files downloadable from website, so they can be added automatically if need be
    }
}
