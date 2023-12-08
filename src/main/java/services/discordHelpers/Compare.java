package services.discordHelpers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;

public class Compare {
    public static boolean channels(Channel c1, Channel c2) {
        return c1.getId().equals(c2.getId());
    }

    public static boolean channels(Channel c, String id) {
        return c.getId().equals(id);
    }

    public static boolean messageContent(Message msg, String content, boolean contains, boolean ignoreCase) {
        if (!contains) {
            if (ignoreCase) {
                return msg.getContentRaw().equalsIgnoreCase(content);
            } else {
                return msg.getContentRaw().equals(content);
            }
        } else {
            if (ignoreCase) {
                return msg.getContentRaw().toLowerCase().contains(content.toLowerCase());
            } else {
                return msg.getContentRaw().contains(content);
            }
        }
    }

    public static boolean messageContent(Message msg, String content) {
        return messageContent(msg, content, false, false);
    }
}
