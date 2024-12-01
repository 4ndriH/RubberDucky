package services.database.daos;

import assets.objects.DeletableMessage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.HibernateUtil;
import services.database.entities.MessageDeleteTrackerORM;

import java.util.ArrayList;
import java.util.List;

public class MessageDeleteTrackerDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDeleteTrackerDAO.class);

    public void addMessageToTracker(String discordServerId, String discordChannelId, String discordMessageId, long deleteTime) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            MessageDeleteTrackerORM messageDeleteTracker = new MessageDeleteTrackerORM(
                    discordMessageId, discordServerId, discordChannelId, deleteTime
            );
            session.persist(messageDeleteTracker);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to add message to tracker, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public ArrayList<DeletableMessage> getMessagesToDelete() {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        ArrayList<DeletableMessage> messages = new ArrayList<>();

        try {
            transaction = session.beginTransaction();
            Query<MessageDeleteTrackerORM> query = session.createQuery(
                    "FROM MessageDeleteTrackerORM WHERE timeToDelete < :currentSystemTime", MessageDeleteTrackerORM.class
            );
            query.setParameter("currentSystemTime", System.currentTimeMillis());
            List<MessageDeleteTrackerORM> messageDeleteTrackerORMs = query.list();

            for (MessageDeleteTrackerORM messageDeleteTrackerORM : messageDeleteTrackerORMs) {
                messages.add(new DeletableMessage(
                        messageDeleteTrackerORM.getDiscordServerId(),
                        messageDeleteTrackerORM.getDiscordChannelId(),
                        messageDeleteTrackerORM.getDiscordMessageId(),
                        messageDeleteTrackerORM.getTimeToDelete()
                ));
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get messages to delete, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return messages;
    }

    public void pruneMessageDeleteTracker() {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Query<MessageDeleteTrackerORM> query = session.createQuery("FROM MessageDeleteTrackerORM WHERE timeToDelete < :currentSystemTime", MessageDeleteTrackerORM.class);
            query.setParameter("currentSystemTime", System.currentTimeMillis());
            List<MessageDeleteTrackerORM> messageDeleteTrackerORMs = query.list();

            for (MessageDeleteTrackerORM messageDeleteTrackerORM : messageDeleteTrackerORMs) {
                session.remove(messageDeleteTrackerORM);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to prune message delete tracker, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }
}
