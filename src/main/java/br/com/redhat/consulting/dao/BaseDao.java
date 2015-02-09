package br.com.redhat.consulting.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.config.TransactionalMode;
import br.com.redhat.consulting.util.GeneralException;

public class BaseDao<ENTITY, SEARCH_FILTER> {

    private static Logger LOG = LoggerFactory.getLogger(BaseDao.class);

    @Inject
    private EntityManager em;

    private Class<ENTITY> entityClass;
    private String entityClassName;
    private String[] fetchCollection = new String[]{};
    private String orderBy = "";

    public BaseDao() {
        if (entityClass == null) {
            Type type = getClass().getGenericSuperclass();

            if (!(type instanceof ParameterizedType)) {
                type = getClass().getSuperclass().getGenericSuperclass();
            }
            ParameterizedType paramType = (ParameterizedType) type;
            entityClass = (Class<ENTITY>) paramType.getActualTypeArguments()[0];
        }
        entityClassName = entityClass.getSimpleName();
    }

    public ENTITY findById(Serializable id) {
        ENTITY ins = em.find(getEntityClass(), id);
        return ins;
    }

    @TransactionalMode
    public ENTITY insert(ENTITY newEntity) throws GeneralException {
        try {
            em.persist(newEntity);
        } catch (PersistenceException e) {
            String msg = "Error to insert " + newEntity + " to database.";
            LOG.error(msg, e);
            throw new GeneralException(msg, e);

        }
        return newEntity;
    }

    @TransactionalMode
    public ENTITY update(ENTITY entity) throws GeneralException {
        ENTITY ent = null;
        try {
            ent = em.merge(entity);
        } catch (PersistenceException e) {
            String msg = "Error to update " + entity + " to database.";
            LOG.error(msg, e);
            throw new GeneralException(msg, e);
        }
        return ent;
    }

    @TransactionalMode
    public void remove(ENTITY entity) throws GeneralException {
        try {
            em.remove(entity);
        } catch (PersistenceException e) {
            String msg = "Error to remove " + entity + " to database.";
            LOG.error(msg, e);
            throw new GeneralException(msg, e);
        }
    }
    
    @TransactionalMode
    public void remove(Integer id) throws GeneralException {
        try {
            ENTITY entity = findById(id);
            em.remove(entity);
        } catch (PersistenceException e) {
            String msg = "Error to remove " + entityClassName + ", id=" + id +" of database.";
            LOG.error(msg, e);
            throw new GeneralException(msg, e);
        }
    }

    protected Class<ENTITY> getEntityClass() {
        return entityClass;
    }
    
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @TransactionalMode
    public List<ENTITY> find(SEARCH_FILTER filter) throws GeneralException {
        List<Object> params = new ArrayList<Object>();
        StringBuilder queryBuffer = new StringBuilder();

        // montar a clausula select .. from ENTIDADE ... JOIN ... 
        queryBuffer.append(configQueryFrom(filter));

        // montar as clausulas WHERE
        configQuery(queryBuffer, filter, params);
        
        // ajusta para parametros posicionais de jpql (?0, ?1, etc)
        int start = 0;
        int order = 0;
        while ((start = queryBuffer.indexOf("?", start + 1)) > 0) {
            queryBuffer.insert(start + 1, order);
            order++;
        }
        
        TypedQuery<ENTITY> query = em.createQuery(queryBuffer.toString(), entityClass);
        
        // caso a classe Impl deseje realizar alguma customizacao no objeto TypedQuery
        configQuery(filter, query);
        
        configQueryParameters(params, query);
        
        List<ENTITY> res = Collections.emptyList();
        try {
            res = query.getResultList();
        } catch (PersistenceException e) {
            String msg = "Error building and executing query.";
            LOG.error(msg, e);
            throw new GeneralException(msg, e);
        } 
        return res;

    }

    protected void configQueryParameters(List<Object> params, TypedQuery<ENTITY> query) {
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i, params.get(i));
        }
    }

    protected void configQuery(StringBuilder query, SEARCH_FILTER filter, List<Object> params) {
    }

    protected String configQueryFrom(SEARCH_FILTER filter) {
        StringBuilder sql = new StringBuilder("select ");
        sql.append("distinct ENT");
        sql.append(" from ").append(entityClassName).append(" ENT");
        String[] collectionsToFetch = getFetchCollection();
        for (int i = 0; i < collectionsToFetch.length; i++) {
            String attrName = collectionsToFetch[i];
            sql.append(" left join fetch ENT.").append(attrName).append(" ENT").append(i+2);
        }
        sql.append(" where 1=1 ");
        return sql.toString();
    }
    
    protected void configQuery(SEARCH_FILTER filter, TypedQuery<ENTITY> query) {
    }
    
    public String[] getFetchCollection() {
        return fetchCollection;
    }
    
    public void OLDsetFetchCollection2(String[] coll) {
        this.fetchCollection = coll;
    }
    
    public void setFetchCollection(String ...colls) {
        fetchCollection = new String[colls.length];
        int i = 0;
        for (String collName: colls) { 
            this.fetchCollection[i++] = collName;
        }
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

}
