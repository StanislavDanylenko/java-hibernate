package stanislav.danylenko.hibernate.runner;

import org.hibernate.Session;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;

import java.time.Instant;

// we can see insert statement but probably in Hibernate 6 it fails
public class InsertRunner {

    public static void main(String[] args) {
        testSaveOutsideTheTransaction();
//        testPersistOutsideTheTransaction();
    }

    static void testSaveOutsideTheTransaction() {
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Person person = new Person("Stas Without transaction", Instant.ofEpochSecond(1690034000), true);
        session.save(person);
        session.flush();
        session.close();
    }

    static void testPersistOutsideTheTransaction() {
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Person person = new Person("Stas Without transaction", Instant.ofEpochSecond(1690034000), true);
        session.persist(person);
        session.flush();
        session.close();
    }

}
