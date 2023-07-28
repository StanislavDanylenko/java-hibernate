package lifecycle.flushmode;

import jakarta.persistence.FlushModeType;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;

import java.time.Instant;

class FlushModeIdentityTest {

    // ALl of these things do not work

    @Test
    void testFlushModeAuto() {
        // flushes before commit
        // flushes before any query
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        session.getTransaction().begin();
        Person person = new Person("Persisted in Auto Mode", Instant.ofEpochSecond(1690034000), true);
        session.persist(person); // flushed here
        Long count = session.createQuery("select count(*) from PersonSequenceKey", Long.class).getSingleResult();
        System.out.println("COUNT = " + count); // = 0
        count = session.createQuery("select count(*) from Person", Long.class).getSingleResult();
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
        Person person = new Person("Persisted in Auto Mode", Instant.ofEpochSecond(1690034000), true);
        session.persist(person); // flushed here
        Long count = session.createQuery("select count(*) from Person", Long.class).getSingleResult();
        System.out.println("COUNT = " + count); // = 1
        session.getTransaction().commit();
        session.close();
    }

    @Test
    void testFlushModeManual() {
        // only manual flush
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        session.setHibernateFlushMode(FlushMode.MANUAL);
        session.getTransaction().begin();
        Person person = new Person("Persisted in Auto Mode", Instant.ofEpochSecond(1690034000), true);
        session.persist(person); // flushed here
        Long count = session.createQuery("select count(*) from Person", Long.class).getSingleResult();
        System.out.println("COUNT = " + count); // = 1`
        session.getTransaction().commit();
        session.close();
    }

    @Test
    void testFlushModeAlways() {
        // flush before each query, even not related
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        session.setHibernateFlushMode(FlushMode.ALWAYS);
        session.getTransaction().begin();
        Person person = new Person("Persisted in Auto Mode", Instant.ofEpochSecond(1690034000), true);
        session.persist(person); // flushed here
        Long count = session.createQuery("select count(*) from PersonSequenceKey", Long.class).getSingleResult();
        System.out.println("COUNT = " + count);
        count = session.createQuery("select count(*) from Person", Long.class).getSingleResult();
        System.out.println("COUNT = " + count);
        session.getTransaction().commit();
        session.close();
    }

}
