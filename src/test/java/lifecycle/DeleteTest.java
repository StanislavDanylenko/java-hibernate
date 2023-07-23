package lifecycle;

import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;

import java.time.Instant;
import java.util.List;

class DeleteTest {

    // DELETE and REMOVE - no diff

    @Test
    void testDelete() {
        Person personSaved = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
            session.save(person); // save and populate ID
            return person;
        });

        HibernateUtil.doInSessionWithTransaction(session -> session.delete(personSaved));

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });

        System.out.println(persons);
    }

    @Test
    void testDeleteSecondTimeThrowException() {
        Person personSaved = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
            session.save(person); // save and populate ID
            return person;
        });

        HibernateUtil.doInSessionWithTransaction(session -> session.delete(personSaved));
        HibernateUtil.doInSessionWithTransaction(session -> session.delete(personSaved));

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });

        System.out.println(persons);
    }

    @Test
    void testDeleteTransient() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
            session.save(person);
        });

        Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
        person.setId(1);
        HibernateUtil.doInSessionWithTransaction(session -> session.delete(person));

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });

        System.out.println(persons);
    }

    @Test
    void testRemove() {
        Person personSaved = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
            session.persist(person); // save and populate ID
            return person;
        });

        HibernateUtil.doInSessionWithTransaction(session -> session.remove(personSaved));

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });

        System.out.println(persons);
    }

    @Test
    void testRemoveSecondTimeThrowException() {
        Person personSaved = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
            session.persist(person); // save and populate ID
            return person;
        });

        HibernateUtil.doInSessionWithTransaction(session -> session.remove(personSaved));
        HibernateUtil.doInSessionWithTransaction(session -> session.remove(personSaved));

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });

        System.out.println(persons);
    }

    @Test
    void testRemoveTransient() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
        });

        Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
        person.setId(1);
        HibernateUtil.doInSessionWithTransaction(session -> session.remove(person));

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });

        System.out.println(persons);
    }

}
