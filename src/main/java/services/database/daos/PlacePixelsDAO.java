package services.database.daos;

import assets.objects.Pixel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.HibernateUtil;
import services.database.entities.PlacePixelsORM;

import java.util.ArrayList;
import java.util.List;

public class PlacePixelsDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlacePixelsDAO.class);

    public void queuePixels(ArrayList<PlacePixelsORM> pixels) {
        LOGGER.info("Queueing pixels");
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        session.setJdbcBatchSize(32_100);
        int count = 0;

        try {
            transaction = session.beginTransaction();

            for (PlacePixelsORM pixel : pixels) {
                session.persist(pixel);

                if (++count % 32_000 == 0) {
                    session.flush();
                    session.clear();
                }
            }

            transaction.commit();
            session.setJdbcBatchSize(10);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to queue pixels, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public ArrayList<Pixel> getPixels(int projectId) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        ArrayList<Pixel> pixels = new ArrayList<>();

        try {
            transaction = session.beginTransaction();
            Query<PlacePixelsORM> query = session.createQuery("FROM PlacePixelsORM WHERE key.projectId = :projectId", PlacePixelsORM.class);
            query.setParameter("projectId", projectId);
            List<PlacePixelsORM> pixelORMs = query.list();

            for (PlacePixelsORM pixelORM : pixelORMs) {
                Pixel pixel = new Pixel(pixelORM.getXCoordinate(), pixelORM.getYCoordinate(), pixelORM.getAlpha(), pixelORM.getImageColor());
                pixels.add(pixel);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get pixels, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return pixels;
    }

    public int getQueuedPixelsCount() {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        int queuedPixels = 0;

        try {
            transaction = session.beginTransaction();
            List<PlacePixelsORM> pixels = session.createQuery("FROM PlacePixelsORM", PlacePixelsORM.class).list();
            transaction.commit();

            queuedPixels = pixels.size();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to get queued pixels, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return queuedPixels;
    }

    public void updatePlacePixelColor(int projectId, int index, String placeColor) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            PlacePixelsORM pixel = session.get(PlacePixelsORM.class, new PlacePixelsORM.PlacePixelsKey(projectId, index));
            pixel.setPlaceColor(placeColor);
            session.merge(pixel);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to update pixel color, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }
}
