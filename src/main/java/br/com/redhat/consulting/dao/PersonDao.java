package br.com.redhat.consulting.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.filter.PersonSearchFilter;

public class PersonDao extends BaseDao<Person, PersonSearchFilter> {
    
    private PersonSearchFilter filter;

    protected void configQuery(StringBuilder query, PersonSearchFilter filter, List<Object> params) {
        this.filter = filter;
        if (filter.isEnabled()) {
            query.append(" and ENT.enabled = ? ");
            params.add(filter.isEnabled());
        }
        if (filter.getId() != null) {
            query.append(" and ENT.id = ? ");
            params.add(filter.getId());
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
            query.append(" and lower(ENT.email) = ? ");
            params.add(filter.getEmail().toLowerCase());
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
    
    protected void addJoinToFromClause(StringBuilder ql) {
        if (StringUtils.isNotBlank(filter.getJoinClause()))
            ql.append(filter.getJoinClause());
    }

    
    public List<Person> findConsultantsAndActiveProjects() {
        String jql = "select distinct c from Person c inner join fetch c.tasks t "
        		+ "where t.project.enabled = :enabled "
        		+ "and (t.project.initialDate <= :today) "
        		+ "and	( "
        		+ "(t.project.endDate >= :today) or (t.project.endDate between :weekBeginning and :weekEnd) "
        		+ " ) ";
        TypedQuery<Person> query= getEntityManager().createQuery(jql, Person.class);
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date weekBeginning = calendar.getTime();
        
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date weekEnd = calendar.getTime();
        
        query.setParameter("enabled", true);
        query.setParameter("weekBeginning", weekBeginning);
        query.setParameter("weekEnd", weekEnd);
        query.setParameter("today", new Date());
        
        
        List<Person> res = query.getResultList();
        return res;
    }
    
    
}
