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

    default boolean isOwnerOnly() {
        return false;
    }
}
