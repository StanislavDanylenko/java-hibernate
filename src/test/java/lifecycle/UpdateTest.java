package lifecycle;

import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;

import java.time.Instant;
import java.util.List;

class UpdateTest {

    /* UPDATE vs MERGE
     * 1)
     * - When we call MERGE method on detached instance, it will update it with updated value.
     * - In case of UPDATE When we call update method on detached instance, it will give exception - NO, it works
     * 2) UPDATE: new instance with the same ID fails, MERGE updates the record
     * 3) UPDATE: not existing instance fails, MERGE creates a new record
     * 4) MERGE returns a new persisted instance
     */

    @Test
    void testSaveOrUpdate() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas >Save<OrUpdate", Instant.ofEpochSecond(1690034000), true);
            session.saveOrUpdate(person);
        });

        List<Person> persons = getPersons();
        System.out.println(persons);

        Person person = persons.get(0);
        person.setName("Stas SaveOr>Update<");

        HibernateUtil.doInSessionWithTransaction(session -> session.saveOrUpdate(person));

        persons = getPersons();
        System.out.println(persons); // updated
    }

    @Test
    void testUpdate() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Saved", Instant.ofEpochSecond(1690034000), true);
            session.save(person);
        });

        List<Person> persons = getPersons();
        System.out.println(persons);

        Person person = persons.get(0);
        person.setName("Stas Updated");

        HibernateUtil.doInSessionWithTransaction(session -> session.update(person));

        persons = getPersons();
        System.out.println(persons); // updated
    }

    @Test
    void testMerge() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
        });

        List<Person> persons = getPersons();
        System.out.println(persons);

        Person person = persons.get(0);
        person.setName("Stas Merged");

        HibernateUtil.doInSessionWithTransaction(session -> session.merge(person));

        persons = getPersons();
        System.out.println(persons); // updated
    }

    @Test
    void testSaveThenUpdate() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Saved", Instant.ofEpochSecond(1690034000), true);
            session.save(person);
            person.setName("Stas Updated");
            session.update(person); // updated
        });

        List<Person> persons = getPersons();
        System.out.println(persons);
    }

    @Test
    void testPersistThenMerge() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            person.setName("Stas Merged");
            session.merge(person); // updated
        });

        List<Person> persons = getPersons();
        System.out.println(persons);
    }

    @Test
    void testSaveAndUpdateDifferentObjectThrowException() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Saved", Instant.ofEpochSecond(1690034000), true);
            session.save(person);

            Person person2 = new Person("Stas Updated", Instant.ofEpochSecond(1690034000), true);
            person2.setId(1);
            session.update(person2); // exception
        });

        List<Person> persons = getPersons();
        System.out.println(persons);
    }

    @Test
    void testPersistAndMergeDifferentObjectSuccess() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);

            Person person2 = new Person("Stas Merged", Instant.ofEpochSecond(1690034000), true);
            person2.setId(1);
            session.merge(person2); // updated
        });

        List<Person> persons = getPersons();
        System.out.println(persons);
    }

    @Test
    void testUpdateNotExistingRecordThrowException() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Saved", Instant.ofEpochSecond(1690034000), true);
            session.update(person); // exception
        });

        List<Person> persons = getPersons();
        System.out.println(persons);
    }

    @Test
    void testMergeNotExistingRecordSuccess() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Merged", Instant.ofEpochSecond(1690034000), true);
            session.merge(person); // update
        });

        List<Person> persons = getPersons();
        System.out.println(persons);
    }

    @Test
    void testSaveThenUpdateSeveralTimes() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Saved", Instant.ofEpochSecond(1690034000), true);
            session.save(person);
            person.setName("Stas Updated");
            session.update(person);
            session.update(person); // only 1 query
        });

        List<Person> persons = getPersons();
        System.out.println(persons);
    }

    @Test
    void testPersistThenMergeSeveralTimes() {
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            person.setName("Stas Merged");
            session.merge(person);
            session.merge(person); // only 1 query
        });

        List<Person> persons = getPersons();
        System.out.println(persons);
    }

    @Test
    void testMergeLostUpdate() {
        Person persisted = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            return person;
        });

        HibernateUtil.doInSessionWithTransaction(session -> {
            persisted.setName("Stas Merged");
            Person mergedInstance = session.merge(persisted); // we must use it instead of function input

            persisted.setName("This name marge is lost");
        });

        List<Person> persons = getPersons();
        System.out.println(persons);
    }

    @Test
    void testMergeSavedUpdateDirtyCheck() {
        Person persisted = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            return person;
        });

        HibernateUtil.doInSessionWithTransaction(session -> {
            persisted.setName("Stas Merged");
            Person mergedInstance = session.merge(persisted);
            session.flush(); // just to show dirty context check
            mergedInstance.setName("This name marge saved"); // correct instance for update
        });

        List<Person> persons = getPersons();
        System.out.println(persons);
    }

    @Test
    void testUpdateDetached() {
        Person p = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Saved", Instant.ofEpochSecond(1690034000), true);
            session.save(person); // save first record
            return person;
        });

        HibernateUtil.doInSessionWithTransaction(session -> {
            p.setName("Stas Updated detached");
            session.update(p); // update ignored
        });

        System.out.println(getPersons());
    }

    @Test
    void testMergeDetached() {
        Person p = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas Persisted", Instant.ofEpochSecond(1690034000), true);
            session.persist(person); // save first record
            return person;
        });

        HibernateUtil.doInSessionWithTransaction(session -> {
            p.setName("Stas Merged detached");
            session.merge(p); // update the record
        });

        System.out.println(getPersons());
    }

    private static List<Person> getPersons() {
        List<Person> persons = HibernateUtil.doInSessionReturning(session -> {
            Query<Person> query = session.createQuery("FROM Person", Person.class);
            return query.list();
        });
        return persons;
    }

}
