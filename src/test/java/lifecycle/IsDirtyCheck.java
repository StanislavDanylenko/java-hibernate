package lifecycle;

import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.Person;
import stanislav.danylenko.hibernate.entities.lifecycle.PersonSequenceKey;

import java.time.Instant;

class IsDirtyCheck {

    @Test
    void testIsDirtySequence() {
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        session.getTransaction().begin();
        PersonSequenceKey person = new PersonSequenceKey("Persisted in Auto Mode");
        session.persist(person);

        boolean dirty = session.isDirty();
        System.out.println("DIRTY=" + dirty); // true

        session.flush();

        dirty = session.isDirty();
        System.out.println("DIRTY=" + dirty); // false

        person.setName("Hello");

        dirty = session.isDirty();
        System.out.println("DIRTY=" + dirty); // true

        session.getTransaction().commit();
        session.close();
    }

    @Test
    void testIsDirtyIdentity() {
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        session.getTransaction().begin();
        Person person = new Person("Persisted in Auto Mode", Instant.ofEpochSecond(1690034000), true);
        session.persist(person); // flushed

        boolean dirty = session.isDirty();
        System.out.println("DIRTY=" + dirty); // false

        session.flush();

        dirty = session.isDirty();
        System.out.println("DIRTY=" + dirty); // false

        person.setName("Hello");

        dirty = session.isDirty();
        System.out.println("DIRTY=" + dirty); // true

        session.getTransaction().commit();
        session.close();
    }

}
