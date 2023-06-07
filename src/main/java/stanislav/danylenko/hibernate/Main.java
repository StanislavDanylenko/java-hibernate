package stanislav.danylenko.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.Book;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Book book = new Book("Core Java", "Learn Core Java with Coding Examples");
        Book book1 = new Book("Learn Hibernate", "Learn Hibernate with building projects");
        Transaction transaction = null;
        try (Session session = HibernateUtil.getInstance().getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the book objects
            session.persist(book);
            session.persist(book1);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

//        readBooks();
        readBook("Core Java");
        readBook("Core Java");
    }

    private static void readBooks() {
        try (Session session = HibernateUtil.getInstance().getSessionFactory().openSession()) {
            List<Book> books = session.createQuery("from Book", Book.class).list();
            books.forEach(b -> System.out.println("Print book name : " + b.getName()));
            books = session.createQuery("from Book", Book.class).list();
            books.forEach(b -> System.out.println("Print book name : " + b.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readBook(String name) {
        try (Session session = HibernateUtil.getInstance().getSessionFactory().openSession()) {
//            Book book1 = session.createQuery("select b from Book b where b.name=:name", Book.class)
//                                .setParameter("name", name)
//                                .getSingleResult();
//            Book book2 = session.createQuery("select b from Book b where b.name=:name", Book.class)
//                                .setParameter("name", name)
//                                .getSingleResult();
            Book book = session.find(Book.class, 1L);
            System.out.println(book.getDescription());
//            Book book1 = session.find(Book.class, 1L);
//            System.out.println(book1.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}