package services.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CountThread10kPolicingListener extends ListenerAdapter {
    private String lastDiscordUserId = "";
    private int lastCountedNumber = -1;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ThreadChannel thread = event.getJDA().getGuildById("747752542741725244").getThreadChannelById("993390913881640970");
//        TextChannel thread = event.getJDA().getGuildById("817850050013036605").getTextChannelById("1020951518582673478");
        String countVerifyDiscordId = "";
        int countVerifyNumber = -1;

        List<Message> messageList = thread.getHistory().retrievePast(64).complete();

        for (int i = 0; i < 64; i++) {
            try {
                lastCountedNumber = Integer.parseInt(messageList.get(i).getContentRaw());
                lastDiscordUserId = messageList.get(i).getAuthor().getId();
                break;
            } catch (Exception ignored) {}
        }

        for (int i = messageList.size() - 1; i >= 0; i--) {
            try {
                if (countVerifyNumber == -1) {
                    countVerifyNumber = Integer.parseInt(messageList.get(i).getContentRaw());
                    countVerifyDiscordId = messageList.get(i).getAuthor().getId();
                } else if (countVerifyDiscordId.equals(messageList.get(i).getAuthor().getId()) || countVerifyNumber + 1 != Integer.parseInt(messageList.get(i).getContentRaw())) {
                    lastCountedNumber = countVerifyNumber;
                    lastDiscordUserId = countVerifyDiscordId;
                    throw new IllegalArgumentException();
                } else {
                    countVerifyNumber = Integer.parseInt(messageList.get(i).getContentRaw());
                    countVerifyDiscordId = messageList.get(i).getAuthor().getId();
                }
            } catch (Exception e) {
                wrongCountHandler(messageList.get(i));
            }
        }

        lastCountedNumber = countVerifyNumber;
        lastDiscordUserId = countVerifyDiscordId;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
//        if (event.getChannel().getId().equals("1020951518582673478")) {
        if (event.getChannel().getId().equals("993390913881640970")) {
            try {
                if (lastDiscordUserId.equals(event.getAuthor().getId()) || lastCountedNumber + 1 != Integer.parseInt(event.getMessage().getContentRaw())) {
                    throw new IllegalArgumentException();
                }

                lastCountedNumber++;
                lastDiscordUserId = event.getAuthor().getId();
            } catch (Exception e) {
                wrongCountHandler(event.getMessage());
            }
        }
    }

    private void wrongCountHandler(Message msg) {
        msg.addReaction(Emoji.fromUnicode("U+1F1FC")).queue(
                null, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (ex) -> {})
        );
        msg.addReaction(Emoji.fromUnicode("U+1F1F7")).queue(
                null, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (ex) -> {})
        );
        msg.addReaction(Emoji.fromUnicode("U+1F1F4")).queue(
                null, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (ex) -> {})
        );
        msg.addReaction(Emoji.fromUnicode("U+1F1F3")).queue(
                null, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (ex) -> {})
        );
        msg.addReaction(Emoji.fromUnicode("U+1F1EC")).queue(
                null, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (ex) -> {})
        );
        msg.addReaction(Emoji.fromFormatted("a:POLICE:796671967922749441")).queue(
                null, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (ex) -> {})
        );
    }
}