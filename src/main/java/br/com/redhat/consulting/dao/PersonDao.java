package br.com.redhat.consulting.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.PersonType;
import br.com.redhat.consulting.model.filter.PersonSearchFilter;

@RequestScoped
public class PersonDao extends BaseDao<Person, PersonSearchFilter> {

    protected void configQuery(StringBuilder query, PersonSearchFilter filter, List<Object> params) {
        if (filter.isEnabled()) {
            query.append(" and ENT.enabled = ? ");
            params.add(filter.isEnabled());
        }
        
        if (StringUtils.isNotBlank(filter.getName())) {
            query.append(" and lower(ENT.name) = ? ");
            params.add(filter.getName().toLowerCase());
        }
        
        if (StringUtils.isNotBlank(filter.getPartialName())) {
            query.append(" and lower(ENT.name) like '%'||?||'%' ");
            params.add(filter.getPartialName().toLowerCase());
        }

        if (StringUtils.isNotBlank(filter.getEmail())) {
            query.append(" and upper(ENT.email) = ? ");
            params.add(filter.getEmail().toUpperCase());
        }
        
        if (filter.getPersonIds() != null && filter.getPersonIds().size() > 0) {
            query.append(" and ENT.id in ( ");
            for (int i = 0; i< filter.getPersonIds().size(); i++) {
                Integer id = filter.getPersonIds().get(i);
                query.append("?");                
                params.add(id);
                if (i+1 < filter.getPersonIds().size())
                    query.append(",");
            }
            query.append(" ) ");
        }
        
        if (filter.getPersonType() != null) {
            query.append(" and ENT.personType = ? ");
            params.add(filter.getPersonType());
        }
        
        if (filter.getPersonTypes() != null) {
            query.append(" and ENT.personType in ( ");
            for (int i = 0; i < filter.getPersonTypes().length; i++) {
                int _type = filter.getPersonTypes()[i];
                query.append("?");
                params.add(_type);
                if (i+1 < filter.getPersonTypes().length)
                    query.append(", ");
            }
            query.append(" ) ");
        }
        
        if (filter.getPartnerOrganization() != null && StringUtils.isNotBlank(filter.getPartnerOrganization().getName())) {
            query.append(" and lower(ENT.partnerOrganization.name) like '%'||?||'%' ");
            params.add(filter.getPartnerOrganization().getName().toLowerCase());
        }
        query.append(" order by ENT.name");
    }
    
//    public String[] getFetchCollection() {
//        return new String[]{"partnerOrganization","projects"};
//    }

/*    public List<Person> findConsultants() {
        List<Person> l = null;
        TypedQuery<Person> tq = getEntityManager().createQuery(
                "select new Person(person.oraclePAId, person.name, person.email, person.city, person.state, org) from Person person left join  person.partnerOrganization org"
                + " where person.personType in (?0, ?1)", Person.class);
        tq.setParameter(0, PersonType.CONSULTANT_PARTNER.getId());
        tq.setParameter(1, PersonType.MANAGER_REDHAT.getId());
        l = tq.getResultList();
        return l;
    }
    
*/    
}
