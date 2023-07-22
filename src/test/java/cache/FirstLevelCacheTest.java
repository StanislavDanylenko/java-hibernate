package cache;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.cache.Book;

import java.util.List;

class FirstLevelCacheTest {

    @Test
    void testFindAllBooks() {
        createBooks();
        findBooks();
    }

    @Test
    void testFLCache() {
        createBooks();

        // 2 queries, not 4
        findBookInOneTransaction();
        findBookInOneTransaction();
    }

    @Test
    void testFLCacheNotWork() {
        createBooks();

        // cache is not working here
        String bookName = "Core Java";
        findBookByNameInOneTransaction(bookName);
        findBookByNameInOneTransaction(bookName);
    }

    private void createBooks() {
        Book book = new Book("Core Java", "Learn Core Java with Coding Examples");
        Book book1 = new Book("Learn Hibernate", "Learn Hibernate with building projects");
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

    private void findBooks() {
        try (Session session = HibernateUtil.getInstance().getSessionFactory().openSession()) {
            List<Book> books = session.createQuery("from Book", Book.class).list();
            books.forEach(b -> System.out.println("Print book name : " + b.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findBookInOneTransaction() {
        try (Session session = HibernateUtil.getInstance().getSessionFactory().openSession()) {
            Book book = session.find(Book.class, 1L);
            System.out.println("get book 1 time: " + book.getDescription());

            Book book1 = session.find(Book.class, 1L);
            System.out.println("get book 2 time: " + book1.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findBookByNameInOneTransaction(String name) {
        try (Session session = HibernateUtil.getInstance().getSessionFactory().openSession()) {
            Book book1 = session.createQuery("select b from Book b where b.name=:name", Book.class)
                    .setParameter("name", name)
                    .getSingleResult();
            System.out.println("name get book 1 time: " + book1.getDescription());

            Book book2 = session.createQuery("select b from Book b where b.name=:name", Book.class)
                    .setParameter("name", name)
                    .getSingleResult();
            System.out.println("name get book 2 time: " + book2.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
