package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class GetAPILog implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAPILog.class);

    public GetAPILog(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        Scanner scanner;
        try {
             scanner = new Scanner(new File("/usr/games/CRAPI/logs/error_log"));
        } catch (FileNotFoundException e) {
            LOGGER.warn("api error log could not be found");
            return;
        }

        StringBuilder sb = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.contains("Started server process")) {
                sb = new StringBuilder();
            }

            sb.append(line).append("\n");
        }

        ctx.getJDA().openPrivateChannelById("155419933998579713").complete()
                .sendFile(new ByteArrayInputStream(sb.toString().getBytes()), "api_error_log.txt").queue();
    }

    @Override
    public String getName() {
        return "GetAPILog";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Sends you the error log since the most recent startup");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("gal");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
