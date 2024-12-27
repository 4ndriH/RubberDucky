package services.database.daos;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.HibernateUtil;
import services.database.entities.ConfigORM;

import java.util.List;

public class ConfigDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigDAO.class);

    public void updateConfig(String key, String value) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            ConfigORM config = session.get(ConfigORM.class, key);
            config.setValue(value);
            session.merge(config);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to update config, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public ConfigORM getConfigEntry(String key) {
        Session session = HibernateUtil.getSession();
        ConfigORM config = null;

        try {
            config = session.get(ConfigORM.class, key);
        } catch (Exception e) {
            LOGGER.error("Failed to get config", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return config;
    }

    public List<ConfigORM> getConfig() {
        Session session = HibernateUtil.getSession();
        List<ConfigORM> configs = null;

        try {
            configs = session.createQuery("FROM ConfigORM", ConfigORM.class).list();
        } catch (Exception e) {
            LOGGER.error("Failed to get all configs", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return configs == null ? List.of() : configs;
    }

    public boolean addConfigEntryIfNotExists(String key, String value, String type) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            ConfigORM config = session.get(ConfigORM.class, key);

            if (config == null) {
                config = new ConfigORM(key, value, type);
                session.persist(config);
                transaction.commit();
                return true;
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to add config, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return false;
    }
}
