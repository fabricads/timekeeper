package br.com.redhat.consulting.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.filter.ProjectSearchFilter;

@RequestScoped
public class ProjectDao extends BaseDao<Project, ProjectSearchFilter> {

    protected void configQuery(StringBuilder query, ProjectSearchFilter filter, List<Object> params) {
        if (filter.getId() != null) {
            query.append(" and ENT.id = ? ");
            params.add(filter.getId());
        }
        
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
        
        if (filter.getPaNumber() != null) {
            query.append(" and ENT.paNumber = ? ");
            params.add(filter.getPaNumber());
        }
        
        if (filter.getProjectManager() != null && StringUtils.isNotBlank(filter.getProjectManager().getName())) {
            query.append(" and lower(ENT.projectManager.name) like '%'||?||'%' ");
            params.add(filter.getProjectManager().getName().toLowerCase());
        }
        
        if (filter.getProjectManager() != null && filter.getProjectManager().getId() != null) {
            query.append(" and ENT.projectManager.id = ? ");
            params.add(filter.getProjectManager().getId());
        }
        query.append(" order by ENT.name");
        
    }
    
    public Integer countProjectsByPM(Integer pmId) {
        Integer count = 0;
        TypedQuery<Integer> query= getEntityManager().createQuery("select count(p) from Project where p.projectManager.id = ?0", Integer.class);
        query.setParameter(0, pmId);
        count = query.getSingleResult();
        return count;
    }
    
}
