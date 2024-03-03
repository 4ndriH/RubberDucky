package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static net.dv8tion.jda.internal.utils.IOUtil.silentClose;

public class Scrape implements CommandInterface {
    RestAction<?> latestUpdate = null;

    @Override
    public void handle(CommandContext ctx) {
        JDA jda = ctx.getJDA();
        MessageChannel c = jda.getGuildById("747752542741725244").getTextChannelById("747758757395562557");
        MessageChannel l = jda.getGuildById("817850050013036605").getTextChannelById("841393155478650920");

        String fileName =  "memes.txt";
        String filePath = "tempFiles/" + fileName;

        final FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            l.sendMessage("Failed to save message due to error getting file writer: " + e.getMessage()).queue();
            return;
        }

        Message progressMessage;
        try {
            // Using .complete for simplicity, but could also be moved to queue and use its callback.
            progressMessage = l.sendMessage(getProgressInfo(filePath, 0)).complete();
        }
        catch (Exception e) {
            e.printStackTrace();
            silentClose(fileWriter);
            return;
        }

        int MAX_DESIRED = 50000;
        List<Message> messages = new ArrayList<Message>();
        c.getIterableHistory()
                .forEachAsync(message -> {
                    messages.add(message);
                    handleProgressUpdate(progressMessage, filePath, messages.size());
                    return messages.size() < MAX_DESIRED;
                })
                .thenAccept(_ignored -> {
                    Collections.reverse(messages);
                    for (Message message : messages) {
                        // Don't save the progress message as it isn't part of chat.
                        if (message.getIdLong() == progressMessage.getIdLong()) {
                            continue;
                        }

                        try {
                            if (!message.getAttachments().isEmpty()) {
                                fileWriter.write("Date: " + message.getTimeCreated() + "\nAuthor: " + message.getAuthor().getName());

                                for (Message.Attachment a : message.getAttachments()) {
                                    fileWriter.write("\n" + "File: " + a.getUrl());
                                }
                                 fileWriter.write("\n\n\n");
                            } else if (message.getContentRaw().contains(".mp")) {
                                fileWriter.write("Date: " + message.getTimeCreated() + "\nAuthor: " + message.getAuthor().getName() + "\n" + "File: " + message.getContentRaw() + "\n\n\n");
                            }

                        }
                        catch (IOException e) {
                            // Rethrow as a RuntimeException so that the .exceptioally(...) will tell user what happened
                            throw new RuntimeException(e);
                        }
                    }
                    l.sendMessage("Conversation was successfully saved").queue();
                })
                .exceptionally(error -> {
                    error.printStackTrace();
                    l.sendMessage("Conversation saving failed due to error: " + error.getMessage()).queue();
                    return null;
                })
                .whenComplete((_ignored, _ignored2) -> {
                    // Set latestUpdate to null to try and prevent any updates being sent now that we're done
                    // as we're about to delete the progressMessage we've been updating.
                    latestUpdate = null;
                    progressMessage.delete().queue();
                    silentClose(fileWriter);
                });
    }

    private void handleProgressUpdate(Message progressMessage, String filePath, int totalMessages) {
        RestAction<?> action = progressMessage.editMessage(getProgressInfo(filePath, totalMessages));
        latestUpdate = action;

        action.setCheck(() -> {
            // Only send out the latest progress update.
            return action == latestUpdate;
        });
        //Submit is similar to .queue(...), but gives easier access to a "finally" like method.
        action.submit().whenComplete((_ignored, _ignored2) -> {
            // Clear the latest update if we just sent it.
            if (latestUpdate == action) {
                latestUpdate = null;
            }
        });
    }

    private String getProgressInfo(String filePath, int totalMessages) {
        String message = "";
        message += "Processing conversation to file:\n";
        message += "`" + filePath + "`\n\n";
        message += "Total messages retrieved thus far: " + totalMessages;

        return message;
    }

    @Override
    public String getName() {
        return "Scrape";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
