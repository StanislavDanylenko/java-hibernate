package cache;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.cache.BookCache;

import java.util.List;

class SecondLevelCacheTest {

    @Test
    void test2LCache() {
        createBooks();

        findBooks();

        // 0 queries, not 4, read from L2C
        findBookInOneTransaction();
        findBookInOneTransaction();
    }

    @Test
    void test2LCacheNotWork() {
        createBooks();

        findBooks();

        // cache is not working here
        String bookName = "Core Java";
        findBookByNameInOneTransaction(bookName);
        findBookByNameInOneTransaction(bookName);
    }

    private static void createBooks() {
        BookCache book = new BookCache("Core Java", "Learn Core Java with Coding Examples");
        BookCache book1 = new BookCache("Learn Hibernate", "Learn Hibernate with building projects");
        Transaction transaction = null;
        try (Session session = HibernateUtil.getInstance().getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(book);
            session.persist(book1);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    private static void findBooks() {
        try (Session session = HibernateUtil.getInstance().getSessionFactory().openSession()) {
            List<BookCache> books = session.createQuery("from BookCache", BookCache.class).list();
            books.forEach(b -> System.out.println("read books, print book name : " + b.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void findBookInOneTransaction() {
        try (Session session = HibernateUtil.getInstance().getSessionFactory().openSession()) {
            BookCache book = session.find(BookCache.class, 1L);
            System.out.println("get book 1 time: " + book.getDescription());
            BookCache book1 = session.find(BookCache.class, 1L);
            System.out.println("get book 2 time: " + book1.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void findBookByNameInOneTransaction(String name) {
        try (Session session = HibernateUtil.getInstance().getSessionFactory().openSession()) {
            BookCache book1 = session.createQuery("select b from BookCache b where b.name=:name", BookCache.class)
                    .setParameter("name", name)
                    .getSingleResult();
            System.out.println("name get book 1 time: " + book1.getDescription());

            BookCache book2 = session.createQuery("select b from BookCache b where b.name=:name", BookCache.class)
                    .setParameter("name", name)
                    .getSingleResult();
            System.out.println("name get book 2 time: " + book2.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
