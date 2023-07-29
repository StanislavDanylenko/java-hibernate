package lifecycle;

import org.hibernate.ReplicationMode;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;

import java.time.Instant;
import java.util.List;

class ReplicateTest {

    @Test
    void testReplicateOverwrite() {
        Person p = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            return person;
        });

        p.setName("Updated Name");
        HibernateUtil.doInSessionWithTransaction(session -> session.replicate(p, ReplicationMode.OVERWRITE)); // updated

        List<Person> persons = getPeople();
        System.out.println(persons);
    }

    @Test
    void testReplicateIgnore() {
        Person p = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            return person;
        });

        p.setName("Updated Name");
        HibernateUtil.doInSessionWithTransaction(session -> session.replicate(p, ReplicationMode.IGNORE)); // not updated

        List<Person> persons = getPeople();
        System.out.println(persons);
    }

    @Test
    void testReplicateLatestVersion() {
        Person p = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            return person;
        });

        p.setName("Updated Name");
        HibernateUtil.doInSessionWithTransaction(session -> session.replicate(p, ReplicationMode.LATEST_VERSION)); // updated

        List<Person> persons = getPeople();
        System.out.println(persons);
    }

    @Test
    void testReplicateLatestException() {
        Person p = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            return person;
        });

        p.setName("Updated Name");
        HibernateUtil.doInSessionWithTransaction(session -> session.replicate(p, ReplicationMode.EXCEPTION)); // exception should be thrown, but just ignores

        List<Person> persons = getPeople();
        System.out.println(persons);
    }

    @Test
    void testReplicateNewObject() {
        Person person = new Person("Stas", Instant.ofEpochSecond(1690034000), true);

        HibernateUtil.doInSessionWithTransaction(session -> session.replicate(person, ReplicationMode.OVERWRITE)); // created

        List<Person> persons = getPeople();
        System.out.println(persons);
    }

    private static List<Person> getPeople() {
        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });
        return persons;
    }

}
