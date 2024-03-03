package commandhandling;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.regex.Pattern;

public interface CommandInterface {
    Pattern argumentPattern = Pattern.compile("^");

    void handle(CommandContext ctx);

    String getName();

    EmbedBuilder getHelp();

    default String getNameLC() {
        return getName().toLowerCase();
    }

    default List<String> getAliases(){
        return List.of();
    }

    // ---------------------------------------------------------
    // SecurityClearance:
    // 0 - Owner
    // 1 - Administrator
    // 2 - Moderators
    // 3 - Plebs
    // ---------------------------------------------------------

     default int getRestrictionLevel() {
        String packageName = this.getClass().getPackageName().split("\\.")[2];

        return switch (packageName) {
            case "owner" -> 0;
            case "admin" -> 1;
            case "mod"   -> 2;
            default      -> 3;
        };
    }

    default boolean attachmentCheck(CommandContext ctx) {
        return true;
    }

    default boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
