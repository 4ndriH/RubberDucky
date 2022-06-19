package services.Miscellaneous;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VVZScraper {
    private static final Logger LOGGER = LoggerFactory.getLogger(VVZScraper.class);
    final static String [] URL = {"http://www.vvz.ethz.ch/Vorlesungsverzeichnis/sucheLehrangebot.view?lang=de&search=on&semkez=",
            "&studiengangTyp=&deptId=&studiengangAbschnittId=&lerneinheitstitel=&lerneinheitscode=",
            "&famname=&rufname=&wahlinfo=&lehrsprache=&periodizitaet=&katalogdaten=&strukturAus=true&_strukturAus=on&search=Suchen"};
    final static String REGEX = "<a href=\\\"/Vorlesungsverzeichnis/lerneinheit.view\\?lerneinheitId=(.*?)\\\">(.*?)</a>";
    final static String NAME = "<a href=\"/Vorlesungsverzeichnis/lerneinheit.view?lerneinheitId=";

    public static void main(String[] args) throws IOException {
        System.out.println(getCourseName("701-0234-00L"));
    }

    public static String getCourseName(String id) {
        int year = Calendar.getInstance().get(Calendar.YEAR);

        boolean flipflop = false;
        //Checking the last 10 Semester for the ID
        for (int i = 0; i < 10; i++) {
            final VVZScraper scraper = new VVZScraper();
            String semester = year + (flipflop ? "W" : "S");
            final String url = URL[0] + semester + URL[1] + id + URL[2];
            String htmlContent = null;
            try {
                htmlContent = scraper.getContent(url);
            } catch (IOException e) {
                LOGGER.error("Error during htmlContent fetching", e);
            }
            if (htmlContent.contains(NAME)) {
                String extractedTitle = StringEscapeUtils.unescapeHtml4(scraper.extractTitle(htmlContent));
                LOGGER.info("Found course: " + id + " - " + extractedTitle);
                return extractedTitle;
            }
            //Course wasn't found that Semester will check next.
            if (flipflop) {
                year -= 1;
            }
            flipflop = !flipflop;
        }
        return "";
    }

    private String getContent(String url) throws IOException {
        final OkHttpClient client = new OkHttpClient.Builder().build();
        final String urlToScrape = url;
        final Request request = new Request.Builder().url(urlToScrape).build();
        final Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String extractTitle(String content) {
        final Pattern titleRegExp = Pattern.compile(REGEX, Pattern.DOTALL);
        final Matcher matcher = titleRegExp.matcher(content);
        matcher.find();
        return matcher.group(2);
    }
}
