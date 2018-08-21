package moneytransfer.services;

import moneytransfer.model.Client;
import moneytransfer.utils.JpaUtils;

import javax.persistence.EntityManager;

public class ClientService {

    public void saveClientIfNotExists(Client client) {
        EntityManager manager = JpaUtils.createEntityManager();
        try {
            manager.getTransaction().begin();
            manager.merge(client);
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw e;
        } finally {
            manager.close();
        }
    }

}
