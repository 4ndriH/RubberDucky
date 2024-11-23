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

    public void queuePixels(int projectId, ArrayList<Pixel> pixels) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            int idx = 1;

            for (Pixel pixel : pixels) {
                PlacePixelsORM p = new PlacePixelsORM();
                p.setProjectId(projectId);
                p.setIndex(idx++);
                p.setXcoordinate(pixel.getX());
                p.setYcoordinate(pixel.getY());
                p.setImageColor(pixel.getImageColor());
                p.setAlpha(pixel.getAlpha());
                session.persist(p);
            }

            transaction.commit();
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
            Query<PlacePixelsORM> query = session.createQuery("FROM PlacePixelsORM WHERE projectId = :projectId", PlacePixelsORM.class);
            query.setParameter("projectId", projectId);
            List<PlacePixelsORM> pixelORMs = query.list();

            for (PlacePixelsORM pixelORM : pixelORMs) {
                Pixel pixel = new Pixel(pixelORM.getXcoordinate(), pixelORM.getYcoordinate(), pixelORM.getAlpha(), pixelORM.getImageColor());
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

    public void updatePlacePixelColor(double alpha, int x, int y, String imageColor, String placeColor) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("UPDATE PlacePixelsORM SET placeColor = :placeColor WHERE xcoordinate = :x AND ycoordinate = :y AND alpha = :alpha AND imageColor = :imageColor");
            query.setParameter("alpha", alpha);
            query.setParameter("imageColor", imageColor);
            query.setParameter("placeColor", placeColor);
            query.setParameter("x", x);
            query.setParameter("y", y);
            query.executeUpdate();
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
