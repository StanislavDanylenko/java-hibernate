package lifecycle;

import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;

import java.time.Instant;

class LifecycleTest {

    @Test
    void lifecycleTest() {
        // New (Transient)
        Person person = new Person("Some name", Instant.ofEpochSecond(1690034000), true);
        System.out.println(person);

        // Managed (Persistent)
        HibernateUtil.doInSessionWithTransaction(session -> session.persist(person));
        System.out.println(person); // id was changed

        // Detached (session was committed)
        person.setName("Some name Updated");
        System.out.println(person);
        Person person1 = HibernateUtil.doInSessionReturning(session -> session.get(Person.class, person.getId()));
        System.out.println(person1);
    }

}
