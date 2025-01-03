package commandhandling.commands.pleb;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.daos.ChannelMessageTrafficDAO;
import services.miscellaneous.Format;
import services.discordhelpers.EmbedHelper;
import services.listeners.CountThreadListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class ChannelEfficiency implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:place|count)?\\s?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelEfficiency.class);

    @Override
    public void handle(CommandContext ctx) {
        String parameter = "Count";

        if (!ctx.getArguments().isEmpty() && ctx.getArguments().get(0).startsWith("p")) {
            parameter = "Place";
        }

        doCommandStuff(ctx, parameter);
    }

    public static void doCommandStuff(CommandContext ctx, String parameter) {
        String embedTitle = parameter.equals("Count") ? "To Infinity And Beyond" : "ETH-Place-Bots";
        EmbedBuilder embed = EmbedHelper.embedBuilder(embedTitle);
        ArrayList<Integer> dataPoints = createDataSet(parameter);

        int messageCnt = dataPoints.stream().mapToInt(Integer::intValue).sum();
        double min = Collections.min(dataPoints) / 60.0;
        double max = Collections.max(dataPoints) / 60.0;
        double average = messageCnt / 1440.0 / 60.0;

        embed.addField("__24h Peak__", String.format("%.2f", max) + " msgs/sec", true);
        embed.addField("__24h Average__", String.format("%.2f", average) + " msgs/sec", true);
        embed.addField("__24h Low__", String.format("%.2f", min) + " msgs/sec", true);
        embed.addField("__24h Progress__", Format.Number(messageCnt) + " msgs", true);

        if (parameter.equals("Count")) {
            embed.addField("__Current Count __", Format.Number(CountThreadListener.lastSent), true);
        } else {
            embed.addBlankField(true);
        }

        embed.addBlankField(true);
        embed.setImage("attachment://LineChart.jpg");
        //embed.setFooter("/channelefficency");

        MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(convert(generatePlot(dataPoints)), "LineChart.jpg"));
        sendMessage(ctx, mca, 128);
    }

    public static BufferedImage generatePlot(ArrayList<Integer> dataPoints) {
        int graphWidth = 1440;
        int graphHeight = 300;
        int graphPadding = 50;

        BufferedImage b_img = new BufferedImage(graphWidth + 2 * graphPadding, graphHeight + 2 * graphPadding, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = b_img.createGraphics();

        // set background
        g2d.setColor(Color.decode("#2B2D31"));
        g2d.fillRect (0, 0, b_img.getWidth(), b_img.getHeight());

        // draw lines
        g2d.setColor(Color.decode("#B3B3B3"));
        // horizontal
        g2d.fillRect(graphPadding - 2, graphHeight + graphPadding + 1, graphWidth + 3, 2);
        for (int i = graphPadding; i <= graphPadding + 4 * 60; i += 60) {
            g2d.fillRect(graphPadding, i, 1440, 1);
        }

        // vertical
        g2d.fillRect(graphPadding - 2, graphPadding, 2, 5 * 60 + 1);
        for (int i = graphPadding + 60; i <= graphWidth + graphPadding; i += 60) {
            g2d.fillRect(i, graphPadding, 1, 300 + 1);
        }

        // draw data
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.decode("#B074AD"));

        for (int i = 0; i < dataPoints.size() - 1; i++) {
            g2d.drawLine(graphPadding + i, graphHeight + graphPadding - dataPoints.get(i), graphPadding + 1 + i, graphHeight + graphPadding - dataPoints.get(i + 1));
        }

        // add labels
        g2d.setColor(Color.WHITE);

        Font font = new Font("Arial", Font.PLAIN, 18);
        g2d.setFont(font);
        int Yaxis = 0;
        int Xaxis = -1440;

        // draw y axis labels
        for (int i = graphHeight + graphPadding + 8; i >= graphPadding; i -= 60) {
            g2d.drawString(String.valueOf(Yaxis++), graphPadding - 20, i);
        }

        // draw x axis labels
        for (int i = graphPadding - 27; i <= graphWidth + graphPadding; i += 60) {
            if (Xaxis == -960) {
                i += 7;
            } else if (Xaxis >= -60) {
                i += 7;
            }

            g2d.drawString(String.valueOf(Xaxis), i, graphHeight + graphPadding + 25);
            Xaxis += 60;
        }

        // draw x-axis title
        g2d.drawString("Minutes", graphWidth / 2 + graphPadding, graphHeight + graphPadding + 45);

        // draw y-axis title
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(-90), 0, 0);
        g2d.setFont(font.deriveFont(at));
        g2d.drawString("msgs/sec", graphPadding - 30, graphHeight - graphPadding - 10);

        return b_img;
    }

    private static ArrayList<Integer> createDataSet(String channel) {
        ChannelMessageTrafficDAO channelMessageTrafficDAO = new ChannelMessageTrafficDAO();
        ArrayList<Integer> dataPoints = channelMessageTrafficDAO.getChannelMessageTraffic(channel);
        Collections.reverse(dataPoints);

        while (dataPoints.size() < 1440) {
            dataPoints.add(0, 0);
        }

        return dataPoints;
    }

    public static InputStream convert(BufferedImage img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            LOGGER.error("Error converting image", e);
        }
        return new ByteArrayInputStream(os.toByteArray());
    }

    @Override
    public String getName() {
        return "ChannelEfficiency";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns information about how many messages wer sent");
        embed.addField("__Channel__", """
                You can select between these channels```
                Count
                Place
                ```""", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ce");
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
