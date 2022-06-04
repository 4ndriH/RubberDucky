package services.API;

import services.database.DBHandlerConfig;

import static spark.Spark.*;

public class CourseReview {
    static String keyStoreLocation = "-";
    static String keyStorePassword = "abcd1234";

    public static void api() {
        //secure(keyStoreLocation, keyStorePassword, null, null);
        ipAddress(DBHandlerConfig.getConfig().get("ip"));
        port(Integer.parseInt(DBHandlerConfig.getConfig().get("port")));

        get("/hello", (request, response) -> "Hello World!");
    }

}
