package services.database.daos;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.HibernateUtil;
import services.database.entities.AccessControlORM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccessControlDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlDAO.class);

    public void addServer(String discordServerId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            AccessControlORM server = new AccessControlORM();
            server.setDiscordServerId(discordServerId);
            session.persist(server);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to add server, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public void removeServer(String discordServerId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            AccessControlORM server = session.get(AccessControlORM.class, discordServerId);
            session.remove(server);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to remove server, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public ArrayList<String> getWhitelistedServers() {
        Session session = HibernateUtil.getSession();
        ArrayList<String> serverIds = new ArrayList<>();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            serverIds = (ArrayList<String>) session.createQuery("SELECT a.discordServerId FROM AccessControlORM a", String.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get server ids, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return serverIds;
    }

    public void addChannel(String discordServerId, String discordChannelId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            AccessControlORM server = session.get(AccessControlORM.class, discordServerId);
            server.getDiscordChannelIds().put(discordChannelId, new ArrayList<>());
            session.merge(server);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to add channel, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public void removeChannel(String discordServerId, String discordChannelId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            AccessControlORM server = session.get(AccessControlORM.class, discordServerId);
            server.getDiscordChannelIds().get(discordChannelId).clear();
            session.merge(server);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to remove channel, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public void addCommand(String discordServerId, String discordChannelId, String command) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            AccessControlORM server = session.get(AccessControlORM.class, discordServerId);
            server.getDiscordChannelIds().putIfAbsent(discordChannelId, new ArrayList<>());
            server.getDiscordChannelIds().get(discordChannelId).add(command);
            session.merge(server);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to add command, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public void removeCommand(String discordServerId, String discordChannelId, String command) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            AccessControlORM server = session.get(AccessControlORM.class, discordServerId);
            server.getDiscordChannelIds().get(discordChannelId).remove(command);
            session.merge(server);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to remove command, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public HashMap<String, ArrayList<String>> getChannelIds() {
        Session session = HibernateUtil.getSession();
        List<AccessControlORM> servers = new ArrayList<>();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            servers = session.createQuery("FROM AccessControlORM", AccessControlORM.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get servers, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        HashMap<String, ArrayList<String>> channelIds = new HashMap<>();
        for (AccessControlORM server : servers) {
            channelIds.putAll(server.getDiscordChannelIds());
        }

        return channelIds;
    }
}
