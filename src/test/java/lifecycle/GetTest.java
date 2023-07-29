package lifecycle;

import org.hibernate.Filter;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.MultiIdentifierLoadAccess;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetTest {

    // Get == Find == byId.load()
    // Load == getReference
    // ByMultipleIds == byId.load() for several ids
    // createQuery getSingleResult throws an exception

    @Test
    void testGet() { // instance or null
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        Person person = HibernateUtil.doInSessionReturning(session -> session.get(Person.class, 1));
        Person person2 = HibernateUtil.doInSessionReturning(session -> session.get(Person.class, 2));

        assertNotNull(person);
        assertNull(person2);
    }

    @Test
    void testFind() { // instance or null
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        Person person = HibernateUtil.doInSessionReturning(session -> session.find(Person.class, 1));
        Person person2 = HibernateUtil.doInSessionReturning(session -> session.find(Person.class, 2));

        assertNotNull(person);
        assertNull(person2);
    }

    @Test
    void testLoad() { // it just returns empty proxy object
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        Person person = HibernateUtil.doInSessionReturning(session -> {
            Person p = session.load(Person.class, 1);
            System.out.println(p); // <- this will load the object
            return p;
        });
        Person person2 = HibernateUtil.doInSessionReturning(session -> session.load(Person.class, 2));

        assertNotNull(person); // not null and loaded above
        assertNotNull(person2); // not null, but not accessible outside the session
    }

    @Test
    void testGetReference() { // it just returns empty proxy object
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        Person person = HibernateUtil.doInSessionReturning(session -> {
            Person p = session.getReference(Person.class, 1);
            System.out.println(p); // <- this will load the object
            return p;
        });
        Person person2 = HibernateUtil.doInSessionReturning(session -> session.getReference(Person.class, 2));

        assertNotNull(person); // not null and loaded above
        assertNotNull(person2); // not null, but not accessible outside the session
    }

    @Test
    void testById() { // return instance or null
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        Person person = HibernateUtil.doInSessionReturning(session -> {
            IdentifierLoadAccess<Person> loadAccess = session.byId(Person.class);
            return loadAccess.load(1);
        });
        Person person2 = HibernateUtil.doInSessionReturning(session -> {
            IdentifierLoadAccess<Person> loadAccess = session.byId(Person.class);
            return loadAccess.load(2);
        });

        assertNotNull(person); // not null and loaded
        assertNull(person2); // null
    }

    @Test
    void testByMultipleIds() { // it just returns empty proxy object
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        List<Person> person = HibernateUtil.doInSessionReturning(session -> {
            MultiIdentifierLoadAccess<Person> loadAccess = session.byMultipleIds(Person.class);
            return loadAccess.multiLoad(1, 2);
        });

        assertNotNull(person); // loaded + null
    }

    @Test
    void testQuery() { // returns instance or throw an exception
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        Person person1 = HibernateUtil.doInSessionReturning(session ->
                session.createQuery("from Person where id = 1", Person.class).getSingleResult());
        assertNotNull(person1);

        Person person2 = HibernateUtil.doInSessionReturning(session ->
                session.createQuery("from Person where id = 2", Person.class).getSingleResult()); // exception
    }

    @Test
    void testQueryList() { // returns list
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        List<Person> persons = HibernateUtil.doInSessionReturning(session ->
                session.createQuery("from Person", Person.class).getResultList());
        assertNotNull(persons);
    }

    @Test
    void testNativeQuery() { // returns instance or throw an exception
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        Person person1 = HibernateUtil.doInSessionReturning(session ->
                session.createNativeQuery("select * from persons where id = 1", Person.class).getSingleResult());
        assertNotNull(person1);

        Person person2 = HibernateUtil.doInSessionReturning(session ->
                session.createNativeQuery("select * from persons where id = 2", Person.class).getSingleResult()); // exception
    }

    @Test
    void testNamedQuery() { // returns instance or throw an exception
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        Person person1 = HibernateUtil.doInSessionReturning(session ->
                session.createNamedQuery("findById", Person.class) // name set in the class
                        .setParameter("idparam", 1)
                        .getSingleResult());
        assertNotNull(person1);

        Person person2 = HibernateUtil.doInSessionReturning(session ->
                session.createNamedQuery("findById", Person.class)
                        .setParameter("idparam", 2)
                        .getSingleResult()); // exception
    }

    @Test
    void testMutationQuery() { // returns instance or throw an exception
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), true);
            session.persist(person);
        });

        int updated = HibernateUtil.doInSessionWithTransactionReturning(session ->
                session.createMutationQuery("DELETE from Person where id = 1").executeUpdate());
        assertEquals(1, updated);

        updated = HibernateUtil.doInSessionWithTransactionReturning(session ->
                session.createMutationQuery("DELETE from Person where id = 2").executeUpdate());
        assertEquals(0, updated);
    }

    @Test
    void testFilter() { // returns instance or throw an exception
        HibernateUtil.doInSessionWithTransaction(session -> {
            Person person = new Person("Stas", Instant.now(), false);
            session.persist(person);
        });

        List<Person> people = HibernateUtil.doInSessionReturning(session -> {
            Filter filter = session.enableFilter("testFilter");
            filter.setParameter("testVal", true);
            return session.createQuery("FROM Person", Person.class).getResultList();
        });
        System.out.println(people);
    }
}
