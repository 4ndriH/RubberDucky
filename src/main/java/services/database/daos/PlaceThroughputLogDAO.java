package services.database.daos;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.HibernateUtil;
import services.database.entities.PlaceThroughputLogORM;

public class PlaceThroughputLogDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceThroughputLogDAO.class);

    public void logThroughput(int pixelsPlaced, int timeTaken) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            PlaceThroughputLogORM log = new PlaceThroughputLogORM();
            log.setBatchSize(pixelsPlaced);
            log.setMessageBatchTime(timeTaken);
            session.persist(log);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Failed to log throughput, rolled back", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }
}
