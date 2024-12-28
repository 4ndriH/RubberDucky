package services.database.daos;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.HibernateUtil;
import services.database.entities.ChannelMessageTrafficORM;

import java.util.ArrayList;
import java.util.List;

public class ChannelMessageTrafficDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelMessageTrafficDAO.class);

    public void addChannelMessageTraffic(int ethPlaceBots, int countThread) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            ChannelMessageTrafficORM channelMessageTraffic = new ChannelMessageTrafficORM();
            channelMessageTraffic.setEthPlaceBots(ethPlaceBots);
            channelMessageTraffic.setCountThread(countThread);
            channelMessageTraffic.setCreatedAt(System.currentTimeMillis());
            session.persist(channelMessageTraffic);
            transaction.commit();
        }  catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to add channel message traffic, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public ArrayList<Integer> getChannelMessageTraffic(String channel) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        ArrayList<Integer> dataPoints = new ArrayList<>();

        try {
            transaction = session.beginTransaction();
            String column = channel.equals("Count") ? "countThread" : "ethPlaceBots";

            Query<Integer> query = session.createQuery(
                    "SELECT c." + column + " FROM ChannelMessageTrafficORM c ORDER BY c.createdAt DESC", Integer.class
            ).setMaxResults(1440);
            List<Integer> results = query.getResultList();
            dataPoints.addAll(results);
            transaction.commit();
        }  catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get channel message traffic, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return dataPoints;
    }

    public void importLogEntry(ArrayList<ChannelMessageTrafficORM> logs) {
        Session session = HibernateUtil.getSession();
        session.setJdbcBatchSize(256_000);
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            for (ChannelMessageTrafficORM log : logs) {
                session.persist(log);
            }
            session.flush();
            transaction.commit();
            session.clear();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to import log entry, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }
}
