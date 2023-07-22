package stanislav.danylenko.hibernate.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// not a singleton really, simplified for example
public final class HibernateUtil {

    private static HibernateUtil instance;
    private final SessionFactory sessionFactory;

    private HibernateUtil() {
        this.sessionFactory = buildSessionFactory();
    }

    public static HibernateUtil getInstance() {
        if (instance == null) {
            instance = new HibernateUtil();
        }
        return instance;
    }

    public SessionFactory buildSessionFactory() {
        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure().build();

        Metadata metadata = new MetadataSources(standardRegistry)
//                .addAnnotatedClass(Book.class)
                .getMetadataBuilder()
                .build();

        SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();

        return sessionFactoryBuilder.build();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static <T> T doInSessionWithTransactionReturning(Function<Session, T> sessionFunction) {
        Session session = getInstance().getSessionFactory().getCurrentSession();
        try {
            session.getTransaction().begin();
            T result = sessionFunction.apply(session);
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Something went wrong when performing operations with db", e);
        } finally {
            session.close();
        }
    }

    public static void doInSessionWithTransaction(Consumer<Session> sessionConsumer) {
        Session session = getInstance().getSessionFactory().getCurrentSession();
        try {
            session.getTransaction().begin();
            sessionConsumer.accept(session);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Something went wrong when performing operations with db", e);
        } finally {
            session.close();
        }
    }

    public static void doInSession(Consumer<Session> sessionConsumer) {
        try (Session session = getInstance().getSessionFactory().openSession()) {
            sessionConsumer.accept(session);
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong when performing operations with db", e);
        }
    }

    public static <T> T doInSessionReturning(Function<Session, T> sessionFunction) {
        try (Session session = getInstance().getSessionFactory().openSession()) {
            return sessionFunction.apply(session);
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong when performing operations with db", e);
        }
    }
}
