package services.API;

import io.javalin.Javalin;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class CourseReviewAPI {

    public static void start() {
//        Javalin app = Javalin.create().start(Integer.parseInt(DBHandlerConfig.getConfig().get("APIPort")));

//        app.get("/test", ctx -> ctx.result("Hello World\n"));

        Javalin.create(config -> {
            config.server(() -> {
                Server server = new Server();
                ServerConnector sslConnector = new ServerConnector(server, getSslContextFactory());
                sslConnector.setPort(443);
                ServerConnector connector = new ServerConnector(server);
                connector.setPort(80);
                server.setConnectors(new Connector[]{sslConnector, connector});
                return server;
            });
        }).start().get("/test", ctx -> ctx.result("Hello World"));



    }

    private static SslContextFactory getSslContextFactory() {
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(CourseReviewAPI.class.getResource("/usr/games/keystore tests/clientkeystore").toExternalForm());
        sslContextFactory.setKeyStorePassword("password");
        return sslContextFactory;
    }
}
