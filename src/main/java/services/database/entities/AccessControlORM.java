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
@Table(name = "access_control")
public class AccessControlORM {
    @Id
    @Column(name = "discord_server_id")
    private String discordServerId;

    @Column(name = "discord_channel_ids")
    @Convert(converter = AccessControlORM.JsonHashMapConverter.class)
    private HashMap<String, ArrayList<String>> discordChannelIds = new HashMap<>();

    public AccessControlORM() {}

    public AccessControlORM(String discordServerId, HashMap<String, ArrayList<String>> discordChannelIds) {
        this.discordServerId = discordServerId;
        this.discordChannelIds = discordChannelIds;
    }

    public String getDiscordServerId() {
        return discordServerId;
    }

    public void setDiscordServerId(String discordServerId) {
        this.discordServerId = discordServerId;
    }

    public HashMap<String, ArrayList<String>> getDiscordChannelIds() {
        return discordChannelIds;
    }

    public void setDiscordChannelIds(HashMap<String, ArrayList<String>> discordChannelIds) {
        this.discordChannelIds = discordChannelIds;
    }

    private static class JsonHashMapConverter implements AttributeConverter<HashMap<String, ArrayList<String>>, String> {
        private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlORM.JsonHashMapConverter.class);

        @Override
        public String convertToDatabaseColumn(HashMap<String, ArrayList<String>> attribute) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode rootNode = objectMapper.createObjectNode();

            for (Map.Entry<String, ArrayList<String>> entry : attribute.entrySet()) {
                ArrayNode arrayNode = objectMapper.createArrayNode();
                for (String value : entry.getValue()) {
                    arrayNode.add(value);
                }
                rootNode.set(entry.getKey(), arrayNode);
            }

            return rootNode.toString();
        }

        @Override
        public HashMap<String, ArrayList<String>> convertToEntityAttribute(String dbData) {
            HashMap<String, ArrayList<String>> permissions = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode jsonNode = objectMapper.readTree(dbData);
                Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();

                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    ArrayList<String> values = new ArrayList<>();
                    for (JsonNode value : field.getValue()) {
                        values.add(value.asText());
                    }
                    permissions.put(field.getKey(), values);
                }

                return permissions;
            } catch (JsonProcessingException e) {
                LOGGER.error("Failed to convert permissions to HashMap", e);
                return new HashMap<>();
            }
        }
    }
}
