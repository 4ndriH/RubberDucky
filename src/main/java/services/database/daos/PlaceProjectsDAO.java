package services.database.daos;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.HibernateUtil;
import services.database.entities.PlaceProjectsORM;

import java.util.List;
import java.util.Locale;

public class PlaceProjectsDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceProjectsDAO.class);

    public void queueProject(String discordUserId, int projectId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            PlaceProjectsORM project = new PlaceProjectsORM(projectId, 0, discordUserId);
            session.persist(project);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to queue project, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public void dequeueProject(int projectId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            PlaceProjectsORM project = session.get(PlaceProjectsORM.class, projectId);
            session.remove(project);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to dequeue project, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public String[] getQueue() {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        String[] queue = new String[3];

        try {
            transaction = session.beginTransaction();
            List<PlaceProjectsORM> projects = session.createQuery("FROM PlaceProjectsORM", PlaceProjectsORM.class).list();
            transaction.commit();

            for (PlaceProjectsORM project : projects) {
                queue[0] += project.getProjectId() + "\n";
                queue[1] += String.format(Locale.US, "%,d", project.getPixelsDrawn()).replace(',', '\'') + "\n";
                queue[2] += "<@!" + project.getDiscordUserId() + ">\n";
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get queue, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return queue;
    }

    public List<Integer> getProjectIds() {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        List<Integer> projectIds = null;

        try {
            transaction = session.beginTransaction();
            projectIds = session.createQuery("SELECT projectId FROM PlaceProjectsORM", Integer.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get project IDs, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return projectIds;
    }

    public int getNextProject() {
        List<Integer> projectIds = getProjectIds();
        return projectIds.isEmpty() ? -1 : projectIds.stream().min(Integer::compareTo).get();
    }

    public String getProjectAuthor(int projectId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        String discordUserId = "";

        try {
            transaction = session.beginTransaction();
            PlaceProjectsORM project = session.get(PlaceProjectsORM.class, projectId);
            discordUserId = project.getDiscordUserId();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get project author, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return discordUserId;
    }

    public int getProjectProgress(int projectId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        int progress = 0;

        try {
            transaction = session.beginTransaction();
            PlaceProjectsORM project = session.get(PlaceProjectsORM.class, projectId);
            progress = project.getPixelsDrawn();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get project progress, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return progress;
    }

    public int pixelsToBeDrawn() {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        int queuedPixels = 0;

        try {
            transaction = session.beginTransaction();
            List<PlaceProjectsORM> projects = session.createQuery("FROM PlaceProjectsORM", PlaceProjectsORM.class).list();
            transaction.commit();

            for (PlaceProjectsORM project : projects) {
                queuedPixels += project.getPixelsDrawn();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get queued pixels, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        PlacePixelsDAO placePixelsDAO = new PlacePixelsDAO();

        return placePixelsDAO.getQueuedPixelsCount() - queuedPixels;
    }

    public void updateProjectProgress(int projectId, int pixelsDrawn) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            PlaceProjectsORM project = session.get(PlaceProjectsORM.class, projectId);
            project.setPixelsDrawn(pixelsDrawn);
            session.merge(project);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to update project progress, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }
}
