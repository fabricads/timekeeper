package br.com.redhat.consulting.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

import br.com.redhat.consulting.model.PersonTask;
import br.com.redhat.consulting.model.filter.PersonTaskSearchFilter;

@RequestScoped
public class PersonTaskDao extends BaseDao<PersonTask, PersonTaskSearchFilter> {

    protected void configQuery(StringBuilder query, PersonTaskSearchFilter filter, List<Object> params) {
        if (filter.getConsultant() != null && filter.getConsultant().getId() != null && filter.getConsultant().getId() > 0) {
            query.append(" and ENT.consultant.id = ? ");
            params.add(filter.getConsultant().getId());
        }
        
        if (filter.getTask() != null && filter.getTask().getId() != null && filter.getTask().getId() > 0) {
            query.append(" and ENT.task.id = ? ");
            params.add(filter.getTask().getId());
        }
        query.append(" order by ENT.consultant.name");
        
    }
    
    public List<PersonTask> findByProject(Integer projectId) {
        String jql = "select distinct pt from PersonTask pt inner join pt.task t where t.project.id=:projectId";
        TypedQuery<PersonTask> query = getEntityManager().createQuery(jql, PersonTask.class);
        query.setParameter("projectId", projectId);
        List<PersonTask> res = query.getResultList();
        return res;
    }

    
}
