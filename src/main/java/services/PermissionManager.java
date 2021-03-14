package services;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class PermissionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionManager.class);

    public static boolean permissionCheck(GuildMessageReceivedEvent event, String invoke) {
        return authentication(event, invoke);
    }

    private static boolean authentication(GuildMessageReceivedEvent event, String invoke) {
        return event.getAuthor().getId().equals(config.get("OWNER_ID")) || blackListedUsers(event, invoke);
    }

    private static boolean blackListedUsers(GuildMessageReceivedEvent event, String invoke) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("src/permissions/userBlacklist.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNext()) {
            if (scanner.next().equals(event.getAuthor().getId())) {
                return false;
            }
        }

        return permittedServer(event, invoke);
    }

    private static boolean permittedServer(GuildMessageReceivedEvent event, String invoke) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("src/permissions/servers.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNext()) {
            if (scanner.next().equals(event.getGuild().getId())) {
                return permittedChannels(event, invoke, "src/permissions/" + event.getGuild().getId() + "/");
            }
        }

        return false;
    }

    private static boolean permittedChannels(GuildMessageReceivedEvent event, String invoke, String path) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(path + "restrictedCommands.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(scanner.hasNext()) {
            if (scanner.next().equals(invoke)) {
                return permittedCommands(event, invoke, path + "/commands/");
            }
        }

        return true;
    }

    private static boolean permittedCommands(GuildMessageReceivedEvent event, String invoke, String path) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(path + invoke + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNext()) {
            if (scanner.nextLine().equals(event.getChannel().getId()) && scanner.hasNextLine()) {
                String line = scanner.nextLine();
                List<String> roleNames = new ArrayList<>();
                List<String> permittedRoles = Arrays.asList(line.split("\\s*,\\s*"));
                for (Role r : event.getGuild().retrieveMemberById(event.getAuthor().getIdLong()).complete().getRoles()) {
                    roleNames.add(r.getName());
                }
                for (String pr : permittedRoles) {
                    if (roleNames.contains(pr) || pr.equals("all")) {
                        return true;
                    }
                }
                return false;
            } else if (scanner.hasNextLine()){
                scanner.nextLine();
            }
        }

        return false;
    }
}
