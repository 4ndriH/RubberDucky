package services.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.entities.*;

public class HibernateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory;

    static {
        try {
            String dbUrl = System.getenv("DATABASE_URL");
            String dbUser = System.getenv("POSTGRES_USER");
            String dbPass = System.getenv("POSTGRES_PASSWORD");

            if (dbUrl == null || dbUser == null || dbPass == null) {
                LOGGER.warn("Environment variables not set, using default values");
                dbUrl = "jdbc:postgresql://localhost:5432/RubberDucky";
                dbUser = "rd_bot";
                dbPass = "password1234";
            }

            System.out.println("dbUrl: " + dbUrl);
            System.out.println("dbUser: " + dbUser);
            System.out.println("dbPass: " + dbPass);

            sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                    .setProperty("hibernate.hikari.dataSource.url", dbUrl)
                    .setProperty("hibernate.hikari.dataSource.user", dbUser)
                    .setProperty("hibernate.hikari.dataSource.password", dbPass)
                    .addAnnotatedClass(ConfigORM.class)
                    .addAnnotatedClass(UsersORM.class)
                    .addAnnotatedClass(AccessControlORM.class)
                    .addAnnotatedClass(ChannelMessageTrafficORM.class)
                    .addAnnotatedClass(MessageDeleteTrackerORM.class)
                    .addAnnotatedClass(PlaceProjectsORM.class)
                    .addAnnotatedClass(PlacePixelsORM.class)
                    .addAnnotatedClass(PlaceThroughputLogORM.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            LOGGER.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }

    public static void closeSession(Session session) {
        if (session != null) {
            session.close();
        }
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
