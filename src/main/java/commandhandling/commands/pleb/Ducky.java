package commandhandling.commands.pleb;

import assets.Config;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import services.discordhelpers.EmbedHelper;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class Ducky implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        int nr = new Random().nextInt(Objects.requireNonNull(new File(Config.DIRECTORY_PATH + "resources/images/duckies/").list()).length);
        EmbedBuilder embed = EmbedHelper.embedBuilder("A RubberDucky").setImage("attachment://ducky" + nr + ".png");
        MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(new File(Config.DIRECTORY_PATH + "resources/images/duckies/ducky" + nr + ".png")));
        sendMessage(ctx, mca, 32);
    }

    @Override
    public String getName() {
        return "Ducky";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Sends a rubber ducky image. \n Why? idk, ask <@!223932775474921472> ¯\\_(ツ)_/¯");
        embed.addField("Suggestions", "If you have image suggestions send them to <@!155419933998579713>", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ucky");
    }
}
