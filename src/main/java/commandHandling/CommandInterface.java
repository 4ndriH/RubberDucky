package commandHandling;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public interface CommandInterface {
    void handle(CommandContext ctx);

    String getName();

    EmbedBuilder getHelp();

    default List<String> getAliases(){
        return List.of();
    }

    default int getRestrictionLevel() {
        return 3;
    }

    default boolean requiresFurtherChecks() {
        return false;
    }
}
