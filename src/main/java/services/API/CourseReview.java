package services.API;

import static spark.Spark.*;

public class CourseReview {
    static String keyStoreLocation = "-";
    static String keyStorePassword = "abcd1234";

    public static void api() {
        //secure(keyStoreLocation, keyStorePassword, null, null);
        ipAddress("rubberducky.vsos.ethz.ch");
        port(5678);

        get("/hello", (request, response) -> "Hello World!");
    }

}
