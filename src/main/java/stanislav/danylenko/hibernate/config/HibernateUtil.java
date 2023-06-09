package stanislav.danylenko.hibernate.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

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

}
