package services.place;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.miscellaneous.GifSequenceWriter;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class TimelapseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimelapseHelper.class);

    public static void generate(int chunk) {
        try {
            Thread.sleep(5000); // make sure Karlos bot is ready to send the chunk. Probably unnecessary
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> pixels = ChunkFetcher.fetchChunks(chunk);

        if (pixels == null) {
            LOGGER.warn("Could not fetch chunk " + chunk, new Exception());
            return;
        }

        sendChunk(pixels, chunk);
        generateTimeLapse(pixels, chunk);
    }

    private static void sendChunk(ArrayList<String> pixels, int chunk) {
        PrintStream writer;
        try {
            writer = new PrintStream("tempFiles/place/timelapse/chunk_" + chunk + ".txt");
        } catch (FileNotFoundException e) {
            LOGGER.warn("Could not create file", e);
            return;
        }

        for (String pixel : pixels) {
            writer.println(pixel);
        }

        writer.close();
        LOGGER.info("Chunk " + chunk + " saved", new Exception());
    }

    private static void generateTimeLapse(ArrayList<String> pixels, int chunk) {
        BufferedImage image;

        try {
            image = ImageIO.read(new File("tempFiles/place/timelapse/chunk_" + (chunk - 1) + ".png"));
        } catch (IOException e) {
            LOGGER.error("Could not read image", e);
            return;
        }

        try {
            ImageOutputStream output = new FileImageOutputStream(new File("tempFiles/place/timelapse/chunk_" + chunk + ".gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 23, true);

            writer.writeToSequence(image);
            BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);

            for (int i = 0; i < pixels.size(); i++) {
                String[] pixel = pixels.get(i).split(" ");
                image.setRGB(Integer.parseInt(pixel[0]), Integer.parseInt(pixel[1]), Color.decode(pixel[2]).getRGB());
                img.setRGB(Integer.parseInt(pixel[0]), Integer.parseInt(pixel[1]), Color.decode(pixel[2]).getRGB());

                if (i % 100 == 0) {
                    writer.writeToSequence(img);
                    img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
                }
            }

            for (int i = 0; i < 64; i++) {
                writer.writeToSequence(img);
            }

            writer.close();
            output.close();
        } catch (IOException e) {
            LOGGER.error("Could not create gif", e);
            return;
        }

        try {
            ImageIO.write(image, "png", new File("tempFiles/place/timelapse/chunk_" + chunk + ".png"));
        } catch (IOException e) {
            LOGGER.error("Could not write image", e);
        }

        LOGGER.info("Timelapse of chunk " + chunk + " saved", new Exception());
    }
}
