package lifecycle;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;

import java.time.Instant;
import java.util.List;

class ClearTest {

    // lose all changes and release the DB connection
    @Test
    void testClose() {
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Person person = new Person("Stas Without transaction", Instant.ofEpochSecond(1690034000), true);
        session.persist(person);
        session.close();

        List<Person> persons = getPeople();
        System.out.println(persons);
    }

    // Clean all changes in context
    @Test
    void testClear() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);

            person.setName("Hello");
            session.clear();
            session.flush(); // - saves nothing, dirty changes were cleared
        });

        List<Person> persons = getPeople();
        System.out.println(persons);
    }

    @Test
    void testDetach() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);

            person.setName("Hello");
            session.detach(person);
            session.flush(); // - saves nothing, instance out of session
        });

        List<Person> persons = getPeople();
        System.out.println(persons);
    }

    @Test
    void testEvict() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);

            person.setName("Hello");
            session.evict(person);
            session.flush(); // - saves nothing, instance out of session
        });

        List<Person> persons = getPeople();
        System.out.println(persons);
    }

    @Test
    void testReadonly() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);

            session.setReadOnly(person, true);
            person.setName("Hello");

            session.flush(); // - saves nothing, instance set to readonly
        });

        List<Person> persons = getPeople();
        System.out.println(persons);
    }

    private List<Person> getPeople() {
        return HibernateUtil.doInSessionReturning(ses -> {
            Query<Person> query = ses.createQuery("FROM Person", Person.class);
            return query.list();
        });
    }

}
