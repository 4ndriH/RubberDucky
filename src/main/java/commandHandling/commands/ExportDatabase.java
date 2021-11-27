package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.DatabaseHandler;
import services.Miscellaneous;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ExportDatabase implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(ExportDatabase.class);

    public ExportDatabase(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);
        File txt = new File("resources/dbExport.txt");
        PrintStream printer;

        try {
            printer = new PrintStream(txt);
        } catch (FileNotFoundException e) {
            LOGGER.error("There was an exception in ExportDatabase", e);
            return;
        }

        ArrayList<String> tables = DatabaseHandler.getTables();

        for (String table : tables) {
            ArrayList<String> columns = DatabaseHandler.getColumns(table);
            printer.println("===" + table + "===");
            for (String column : columns) {
                printer.print(column + "\t");
            }
            printer.println();
            for (String str : DatabaseHandler.getValuesOfTable(table, columns)) {
                printer.print(str + "\n");
            }
            printer.println("---------------------------------");
        }

        ctx.getJDA().openPrivateChannelById(CONFIG.OwnerID.get()).queue(
                channel -> channel.sendFile(txt).queue()
        );
    }

    @Override
    public String getName() {
        return "ExportDatabase";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Exports the database to a txt file");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("edb", "dbe", "exportdb");
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
