package services.database.daos;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.HibernateUtil;
import services.database.entities.UsersORM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersDAO.class);

    public void addUser(String discordUserId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            UsersORM user = new UsersORM();
            user.setDiscordUserId(discordUserId);
            session.persist(user);
            transaction.commit();
        }  catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to add user, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public void toggleUserBlacklist(String discordUserId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            UsersORM user = session.get(UsersORM.class, discordUserId);
            user.setBlacklisted(!user.isBlacklisted());
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to toggle blacklist, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public UsersORM getUser(String discordUserId) {
        Session session = HibernateUtil.getSession();
        UsersORM user = null;

        try {
            user = session.get(UsersORM.class, discordUserId);
        } catch (Exception e) {
            LOGGER.error("Failed to get user", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return user;
    }

    public List<UsersORM> getUsers() {
        Session session = HibernateUtil.getSession();
        List<UsersORM> users = null;

        try {
            users = session.createQuery("FROM UsersORM", UsersORM.class).list();
        } catch (Exception e) {
            LOGGER.error("Failed to get all users", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return users == null ? List.of() : users;
    }

    public ArrayList<String> getUserBlacklist() {
        Session session = HibernateUtil.getSession();
        ArrayList<String> blacklist = new ArrayList<>();

        try {
            List<UsersORM> blacklistedUsers = session.createQuery("FROM UsersORM WHERE blacklisted = true", UsersORM.class).list();

            for (UsersORM user : blacklistedUsers) {
                blacklist.add(user.getDiscordUserId());
            }

        } catch (Exception e) {
            LOGGER.error("Failed to get blacklist", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return blacklist;
    }

    public HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> getSnowflakePermissions() {
        Session session = HibernateUtil.getSession();
        HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> snowflakes = new HashMap<>();

        try {
            List<UsersORM> users = session.createQuery("FROM UsersORM", UsersORM.class).list();

            for (UsersORM user : users) {
                snowflakes.put(user.getDiscordUserId(), user.getPermissions());
            }

        } catch (Exception e) {
            LOGGER.error("Failed to get snowflake permissions", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return snowflakes;
    }

    public void updateSnowflakePermissions(String DiscordUserId, HashMap<String, HashMap<String, ArrayList<String>>> permissions) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            UsersORM user = session.get(UsersORM.class, DiscordUserId);

            if (user == null) {
                addUser(DiscordUserId);
                user = session.get(UsersORM.class, DiscordUserId);
            }

            user.setPermissions(permissions);
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to update snowflake permissions, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }
}
