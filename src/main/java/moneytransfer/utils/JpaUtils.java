package moneytransfer.utils;

import org.h2.tools.Server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;

public class JpaUtils {
    private static EntityManagerFactory factory;

    static {
        factory = Persistence.createEntityManagerFactory("moneytransfer");
        try {
            Server.createWebServer("-webPort", "8082").start();
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void init() {}

    public static EntityManager createEntityManager() {
        return factory.createEntityManager();
    }
}
