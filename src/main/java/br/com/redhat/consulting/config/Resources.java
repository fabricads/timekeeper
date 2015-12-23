package br.com.redhat.consulting.config;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resources {

    private static Logger LOG = LoggerFactory.getLogger(Resources.class);

    @PersistenceContext
    private EntityManager em;

    @Produces
    public EntityManager produceEntityManager() {
        return em;
    }

}
