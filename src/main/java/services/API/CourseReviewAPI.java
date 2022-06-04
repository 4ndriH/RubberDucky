package services.API;

import io.javalin.Javalin;
import services.database.DBHandlerConfig;

public class CourseReviewAPI {

    public static void start() {
        Javalin app = Javalin.create().start(DBHandlerConfig.getConfig().get("APIAddress"), Integer.parseInt(DBHandlerConfig.getConfig().get("APIPort")));
        app.get("/test", ctx -> ctx.result("Hello World"));
    }

}
