package services.place;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordhelpers.EmbedHelper;
import services.miscellaneous.GifSequenceWriter;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class TimelapseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimelapseHelper.class);

    public static void generate(int chunk, MessageReceivedEvent event) {
        try {
            Thread.sleep(5000); // make sure Karlo's bot is ready to send the chunk. Probably unnecessary
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> pixels = ChunkFetcher.fetchChunks(chunk);

        if (pixels == null) {
            LOGGER.warn("Could not fetch chunk " + chunk, new Exception());
            return;
        }

        sendChunk(pixels, chunk, event.getJDA());
        generateTimeLapse(pixels, chunk, event);
    }

    private static void sendChunk(ArrayList<String> pixels, int chunk, JDA jda) {
        StringBuilder sb = new StringBuilder();
        PrintStream writer;

        try {
            writer = new PrintStream("tempFiles/place/timelapse/chunk_" + chunk + ".txt");
        } catch (FileNotFoundException e) {
            LOGGER.warn("Could not create file", e);
            return;
        }

        for (String pixel : pixels) {
            writer.println(pixel);
            sb.append(pixel).append("\n");
        }

        writer.close();

        InputStream stream = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
        String fileName = "chunk_" + chunk + ".txt";

        jda.getGuildById("817850050013036605").getTextChannelById("969901898389925959").sendMessage(fileName).addFiles(FileUpload.fromData(stream, fileName)).queue();

        LOGGER.debug("Chunk " + chunk + " saved", new Exception());
    }

    private static void generateTimeLapse(ArrayList<String> pixels, int chunk, MessageReceivedEvent event) {
        BufferedImage image;

        try {
            image = ImageIO.read(new File("tempFiles/place/timelapse/chunk_" + (chunk - 1) + ".png"));
        } catch (IOException e) {
            LOGGER.error("Could not read image", e);
            return;
        }

        try {
            ImageOutputStream output = new FileImageOutputStream(new File("tempFiles/place/timelapse/chunk_" + chunk + ".gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 50, true);

            writer.writeToSequence(image);
            BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);

            for (int i = 0; i < pixels.size(); i++) {
                String[] pixel = pixels.get(i).split(" ");
                image.setRGB(Integer.parseInt(pixel[0]), Integer.parseInt(pixel[1]), Color.decode(pixel[2]).getRGB());
                img.setRGB(Integer.parseInt(pixel[0]), Integer.parseInt(pixel[1]), Color.decode(pixel[2]).getRGB());

                if (i % 200 == 0) {
                    writer.writeToSequence(img);
                    img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
                }
            }

            for (int i = 0; i < 64; i++) {
                writer.writeToSequence(img);
            }

            writer.close();
            output.close();
            LOGGER.debug("Timelapse of chunk " + chunk + " saved", new Exception());
        } catch (IOException e) {
            LOGGER.error("Could not create gif", e);
            return;
        }

        try {
            ImageIO.write(image, "png", new File("tempFiles/place/timelapse/chunk_" + chunk + ".png"));
        } catch (IOException e) {
            LOGGER.error("Could not write image", e);
        }

        File gif = new File("tempFiles/place/timelapse/chunk_" + chunk + ".gif");
        EmbedBuilder embed = EmbedHelper.embedBuilder("Timelapse of chunk " + chunk);
        embed.setImage("attachment://chunk_" + chunk + ".gif");
        event.getMessage().replyEmbeds(embed.build()).addFiles(FileUpload.fromData(gif)).queue();

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        gif.delete();
    }
}
