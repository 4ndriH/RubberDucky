package commandhandling;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.regex.Pattern;

public interface CommandInterface {
    Pattern argumentPattern = Pattern.compile(".*");

    void handle(CommandContext ctx);

    String getName();

    EmbedBuilder getHelp();

    default String getNameLC() {
        return getName().toLowerCase();
    }

    default List<String> getAliases(){
        return List.of();
    }

    default int getRestrictionLevel() {
        return 3;
    }

    @Deprecated
    default boolean requiresFurtherChecks() {
        return false;
    }

    default boolean attachmentCheck() {
        return true;
    }

    default boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
