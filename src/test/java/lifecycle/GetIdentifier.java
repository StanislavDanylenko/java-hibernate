package lifecycle;

import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.lifecycle.PersonSequenceKey;

class GetIdentifier {

    @Test
    void testGetIdentifier() {
        HibernateUtil.doInSessionWithTransactionReturning(session -> {
            PersonSequenceKey person = new PersonSequenceKey("Persisted");
            session.persist(person); // save and populate ID
            Integer identifier = (Integer) session.getIdentifier(person);
            System.out.println("IDENTIFIER=" + identifier); // 1
            return person;
        });
    }

    @Test
    void testGetEntityName() {
        HibernateUtil.doInSessionWithTransactionReturning(session -> {
            PersonSequenceKey person = new PersonSequenceKey("Persisted");
            session.persist(person); // save and populate ID
            String entityName = session.getEntityName(person);
            System.out.println("ENTITY NAME=" + entityName); // stanislav.danylenko.hibernate.entities.lifecycle.PersonSequenceKey
            return person;
        });
    }

}
