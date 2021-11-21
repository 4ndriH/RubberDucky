package services;

import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;

import java.io.File;
import java.util.ArrayList;

public class OnStartUp {
    private final Logger LOGGER = LoggerFactory.getLogger(OnStartUp.class);

    public OnStartUp() {
        DirectoryVerification();
        CONFIG.reload();
        MessageCleanUp();
    }

    private void MessageCleanUp () {
        (new Thread(() -> {
            while(CONFIG.instance == null) {
                try {
                    Thread.sleep(32768);
                } catch (InterruptedException ignored) {}
            }

            ArrayList<ArrayList<String>> msgs = DatabaseHandler.getDeleteMessages();
            for (int i = 0; i < msgs.get(0).size(); i++) {
                try {
                    CONFIG.instance.getGuildById(msgs.get(0).get(i))
                            .getTextChannelById(msgs.get(1).get(i))
                            .deleteMessageById(msgs.get(2).get(i)).queue(
                                    null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)
                            );
                    Thread.sleep(1024);
                } catch (Exception ignored) {}
            }
        })).start();
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
            LOGGER.info("The following Directories have been created: " + sb.substring(2));
        }
        //make important files downloadable from website, so they can be added automatically if need be
    }

    private void DataBaseVerification() {

    }
}
