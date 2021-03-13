package commandHandling;

import java.util.List;

public interface CommandInterface {
    void handle(CommandContext ctx);

    String getName();

    String getHelp();

    default List<String> getAliases(){
        return List.of();
    }
}
