package br.com.redhat.consulting;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Alternative
public class EntityManagerTestProducers {

    @Produces
    @Alternative
    public EntityManager produceEntityManager() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("partner_timekeeper_test");
        EntityManager em = emf.createEntityManager();
        return em;
    }

    
}
