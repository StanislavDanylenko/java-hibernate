package lifecycle;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;
import stanislav.danylenko.hibernate.entities.lifecycle.PersonSequenceKey;

import java.time.Instant;
import java.util.List;

class CreateTest {

    /* SAVE vs PERSIST
     * 1) save returns an identifier when persist is void
     * 2) save insert ID immediately when persist only at flush time
     * 3) save works outside the transaction when persist throws an exception - no it doesn't work
     * 4) save creates a new row for a detached objects when persist throws an exception
     */

    @Test
    void testSave() {
        HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
            Integer id = (Integer) session.save(person);// save and populate ID
            return person;
        });

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });

        System.out.println(persons);
    }

    @Test
    void testPersist() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Persist", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
        });

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });

        System.out.println(persons);
    }

    @Test
    void testPersistPopulateID() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            PersonSequenceKey person = new PersonSequenceKey("Stas Persist");
            session.persist(person);
            session.flush(); // populate ID
        });

        List<PersonSequenceKey> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<PersonSequenceKey> query = session.createQuery("FROM PersonSequenceKey", PersonSequenceKey.class);
            return query.list();
        });

        System.out.println(persons);
    }

    @Test
    void testSaveOutsideTheTransaction() {
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Person person = new Person("Stas Without transaction", Instant.ofEpochSecond(1690034000), true);
        session.save(person); // we can see insert statement but probably in Hibernate 6 flush fails
        session.flush();
        session.close();
    }

    @Test
    void testPersistOutsideTheTransaction() {
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Person person = new Person("Stas Without transaction", Instant.ofEpochSecond(1690034000), true);
        session.persist(person); // throws an exception as expected
        session.flush();
        session.close();
    }

    // 1 query - save
    // 2 query - update
    @Test
    void testSaveDirtySaveAtCommit() {
        Person finalPerson = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
            session.save(person);
            person.setName("Hello");
            return person; // flush on commit dirty check
        });
        System.out.println(finalPerson);
    }

    // 1 query - save
    // 2 query - update
    @Test
    void testPersistDirtySaveAtCommit() {
        Person finalPerson = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Persist", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            person.setName("Hello");
            return person; // flush on commit dirty check
        });
        System.out.println(finalPerson);
    }

    @Test
    void testSaveSecondTime() {
        Person finalPerson = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
            session.save(person);
            session.save(person); // only one query
            return person;
        });
        System.out.println(finalPerson);
    }

    @Test
    void testPersistSecondTime() {
        Person finalPerson = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Save", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            session.persist(person); // only one query
            return person;
        });
        System.out.println(finalPerson);
    }

    @Test
    void testSaveOrUpdate() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas >Save<OrUpdate", Instant.ofEpochSecond(1690034000), true);
            session.saveOrUpdate(person);
        });

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });

        System.out.println(persons);
    }

    @Test
    void testSaveDetached() {
        Person p = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas One and Two times", Instant.ofEpochSecond(1690034000), true);
            session.save(person); // save first record
            return person;
        });

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            session.save(p); // save second record
            return session.createQuery("FROM Person").getResultList();
        });

        System.out.println(persons);
    }

    @Test
    void testPersistDetached() {
        Person p = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas One and Two times", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            return person;
        });

        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            session.persist(p); // throws exception
            return session.createQuery("FROM Person").getResultList();
        });

        System.out.println(persons);
    }

}
