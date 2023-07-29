package lifecycle.cachemode;

import org.hibernate.CacheMode;
import org.junit.jupiter.api.Test;
import stanislav.danylenko.hibernate.config.HibernateUtil;
import stanislav.danylenko.hibernate.entities.cache.BookCache;

import java.util.List;

class CacheModeTest {

    @Test
    void testCacheModeNormal() {
        CacheMode cacheMode = CacheMode.NORMAL;

        saveBooks(cacheMode);

        findBooks(cacheMode); // 1 query here

        // 0 queries, not 4, read from L2C
        findBookInOneTransaction(cacheMode);
        findBookInOneTransaction(cacheMode);

        HibernateUtil.doInSessionWithTransaction(session -> {
            session.setCacheMode(cacheMode);
            List<BookCache> books = session.createQuery("from BookCache", BookCache.class).list();
            BookCache book = new BookCache("Core Java", "Learn Core Java with Coding Examples");
            session.persist(book);
            books = session.createQuery("from BookCache", BookCache.class).list();
            BookCache book1 = session.find(BookCache.class, 1L);
        });
    }

    @Test
    void testCacheModeIgnore() { // it is not working - stop testing it
        CacheMode cacheMode = CacheMode.IGNORE;

        saveBooks(cacheMode);

        findBooks(cacheMode); // 4 queries of 4 should be, but no 2 queries

        findBookInOneTransaction(cacheMode);
        findBookInOneTransaction(cacheMode);

        HibernateUtil.doInSessionWithTransaction(session -> {
            session.setCacheMode(cacheMode);
            List<BookCache> books = session.createQuery("from BookCache", BookCache.class).list();
            BookCache book = new BookCache("Core Java", "Learn Core Java with Coding Examples");
            session.persist(book);
            books = session.createQuery("from BookCache", BookCache.class).list();
            BookCache book1 = session.find(BookCache.class, 1L);
        });
    }

    private static void saveBooks(CacheMode cacheMode) {
        HibernateUtil.doInSessionWithTransaction(session -> {
            session.setCacheMode(cacheMode);
            BookCache book = new BookCache("Core Java", "Learn Core Java with Coding Examples");
            BookCache book1 = new BookCache("Learn Hibernate", "Learn Hibernate with building projects");
            session.persist(book);
            session.persist(book1);
        });
    }

    private static void findBooks(CacheMode cacheMode) {
        HibernateUtil.doInSession(session -> {
            session.setCacheMode(cacheMode);
            List<BookCache> books = session.createQuery("from BookCache", BookCache.class).list();
            books.forEach(b -> System.out.println("read books, print book name : " + b.getName()));
        });
    }

    private static void findBookInOneTransaction(CacheMode cacheMode) {
        HibernateUtil.doInSession(session -> {
            session.setCacheMode(cacheMode);
            BookCache book = session.find(BookCache.class, 1L);
            System.out.println("get book 1 time: " + book.getDescription());
            BookCache book1 = session.find(BookCache.class, 1L);
            System.out.println("get book 2 time: " + book1.getDescription());
        });
    }
}
