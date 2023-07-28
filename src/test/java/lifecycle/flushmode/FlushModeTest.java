package lifecycle.flushmode;

import jakarta.persistence.FlushModeType;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.PersonSequenceKey;

class FlushModeTest {

    @Test
    void testFlushModeAuto() {
        // flushes before commit
        // flushes before any query
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        session.getTransaction().begin();
        PersonSequenceKey person = new PersonSequenceKey("Persisted in Auto Mode");
        session.persist(person);
        // not flush here - another entity
        Long count = session.createQuery("select count(*) from Person", Long.class).getSingleResult();
        System.out.println("COUNT = " + count); // = 0
        // flush here
        count = session.createQuery("select count(*) from PersonSequenceKey", Long.class).getSingleResult();
        System.out.println("COUNT = " + count); // = 1
        session.getTransaction().commit();
        session.close();
    }

    @Test
    void testFlushModeCommit() {
        // flushes only before commit
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        session.setFlushMode(FlushModeType.COMMIT);
        session.getTransaction().begin();
        PersonSequenceKey person = new PersonSequenceKey("Persisted in Commit Node");
        session.persist(person);
        Long count = session.createQuery("select count(*) from PersonSequenceKey", Long.class).getSingleResult();
        System.out.println("COUNT = " + count); // = 0
        // flush only here
        session.getTransaction().commit();
        session.close();
    }

    @Test
    void testFlushModeManual() {
        // only manual flush
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        session.setHibernateFlushMode(FlushMode.MANUAL);
        session.getTransaction().begin();
        PersonSequenceKey person = new PersonSequenceKey("Persisted in Manual Mode");
        session.persist(person);
        Long count = session.createQuery("select count(*) from PersonSequenceKey", Long.class).getSingleResult();
        System.out.println("COUNT = " + count); // 0
        session.getTransaction().commit();
        session.close();
        // no flush at all
    }

    @Test
    void testFlushModeAlways() {
        // only manual flush
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        session.setHibernateFlushMode(FlushMode.ALWAYS);
        session.getTransaction().begin();
        PersonSequenceKey person = new PersonSequenceKey("Persisted in Always Mode");
        session.persist(person);
        // flush here - another entity
        Long count = session.createQuery("select count(*) from Person", Long.class).getSingleResult();
        System.out.println("COUNT = " + count);
        count = session.createQuery("select count(*) from PersonSequenceKey", Long.class).getSingleResult();
        System.out.println("COUNT = " + count);
        session.getTransaction().commit();
        session.close();
    }

}
