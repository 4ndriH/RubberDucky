package services.database.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Entity
@Table(name = "users")
public class UsersORM {
    @Id
    @Column(name = "discord_user_id")
    private String discordUserId;

    @Column(name = "blacklisted")
    private boolean blacklisted;

    @Column(name = "permissions")
    @Convert(converter = JsonHashMapConverter.class)
    private HashMap<String, HashMap<String, ArrayList<String>>> permissions = new HashMap<>();

    public UsersORM() {}

    public UsersORM(String discordUserId, boolean blacklisted, HashMap<String, HashMap<String, ArrayList<String>>> permissions) {
        this.discordUserId = discordUserId;
        this.blacklisted = blacklisted;
        this.permissions = permissions;
    }

    public String getDiscordUserId() {
        return discordUserId;
    }

    public void setDiscordUserId(String discordUserId) {
        this.discordUserId = discordUserId;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public HashMap<String, HashMap<String, ArrayList<String>>> getPermissions() {
        return permissions;
    }

    public void setPermissions(HashMap<String, HashMap<String, ArrayList<String>>> permissions) {
        this.permissions = permissions;
    }

    private static class JsonHashMapConverter implements AttributeConverter<HashMap<String, HashMap<String, ArrayList<String>>>, String> {
        private static final Logger LOGGER = LoggerFactory.getLogger(JsonHashMapConverter.class);

        @Override
        public String convertToDatabaseColumn(HashMap<String, HashMap<String, ArrayList<String>>> permissions) {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode rootNode = objectMapper.createObjectNode();

            for (String serverId : permissions.keySet()) {
                JsonNode serverNode = objectMapper.createObjectNode();

                for (String channelId : permissions.get(serverId).keySet()) {
                    ArrayNode channelNode = objectMapper.createArrayNode();

                    for (String command : permissions.get(serverId).get(channelId)) {
                        channelNode.add(command);
                    }

                    ((ObjectNode) serverNode).set(channelId, channelNode);
                }

                ((ObjectNode) rootNode).set(serverId, serverNode);
            }

            return rootNode.toString();
        }

        @Override
        public HashMap<String, HashMap<String, ArrayList<String>>> convertToEntityAttribute(String permissions) {
            try {
                HashMap<String, HashMap<String, ArrayList<String>>> permissionsMap = new HashMap<>();
                ObjectMapper objectMapper = new ObjectMapper();

                JsonNode rootNode = objectMapper.readTree(permissions);

                if (rootNode.isObject()) {
                    Iterator<Map.Entry<String, JsonNode>> serverIterator = rootNode.fields();

                    while (serverIterator.hasNext()) {
                        Map.Entry<String, JsonNode> serverEntry = serverIterator.next();
                        Iterator<Map.Entry<String, JsonNode>> channelIterator = serverEntry.getValue().fields();

                        HashMap<String, ArrayList<String>> channelMap = new HashMap<>();

                        while (channelIterator.hasNext()) {
                            Map.Entry<String, JsonNode> channelEntry = channelIterator.next();
                            ArrayList<String> commands = new ArrayList<>();

                            for (JsonNode command : channelEntry.getValue()) {
                                commands.add(command.asText());
                            }

                            channelMap.put(channelEntry.getKey(), commands);
                        }

                        permissionsMap.put(serverEntry.getKey(), channelMap);
                    }
                }

                return permissionsMap;
            } catch (JsonProcessingException e) {
                LOGGER.error("Failed to convert permissions to HashMap", e);
                return new HashMap<>();
            }
        }
    }
}
