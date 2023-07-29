package lifecycle;

import org.hibernate.ReplicationMode;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;

import java.time.Instant;
import java.util.List;

class OtherTest {

    @Test
    void testRefresh() {
        Person p = HibernateUtil.doInSessionWithTransactionReturning(session -> {
            Person person = new Person("Stas", Instant.ofEpochSecond(1690034000), true);
            session.persist(person);
            session.createQuery("UPDATE Person set name='Updated' where id = 1").executeUpdate();
            System.out.println(person);
            session.refresh(person);
            System.out.println(person);
            return person;
        });

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
