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
            sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                    .addAnnotatedClass(ConfigORM.class)
                    .addAnnotatedClass(UsersORM.class)
                    .addAnnotatedClass(AccessControlORM.class)
                    .addAnnotatedClass(ChannelMessageTrafficORM.class)
                    .addAnnotatedClass(MessageDeleteTrackerORM.class)
                    .addAnnotatedClass(PlaceProjectsORM.class)
                    .addAnnotatedClass(PlaceThroughputLogORM.class)
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
