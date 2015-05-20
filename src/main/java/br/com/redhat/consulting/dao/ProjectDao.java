package br.com.redhat.consulting.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.Query;
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
        
        if (filter.isEnabled() != null) {
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
        
        if (filter.getConsultants().size() > 0) {
            addInnerJoin("inner join ENT.consultants cons");
            query.append(" and cons.id in (");
            for (int i = 0; i < filter.getConsultants().size(); i++) {
                query.append("?");
                params.add(filter.getConsultants().get(i).getId());
                if (i+1 < filter.getConsultants().size())
                    query.append(",");
            }
            query.append(")");
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
    
    public Long countProjectsByPM(Integer pmId) {
        Long count = 0L;
        TypedQuery<Long> query= getEntityManager().createQuery("select count(p) from Project p where p.projectManager.id = ?0", Long.class);
        query.setParameter(0, pmId);
        count = query.getSingleResult();
        return count;
    }
    
    public List<Project> findNoEntriesThisWeekProjects() {
        String jql = "select p from Project p "
        		+ "where p not in ( "
        		+ "select p2 "
        		+ "from Project p2 "
        		+ "inner join p2.timecards tc "
        		+ "inner join tc.timecardEntries tce "
        		+ "where tce.day between :weekBeginning and :weekEnd "
        		+ ") "
        		+ "and (p.initialDate <= :today) "
        		+ "and	( "
        		+ " (p.endDate >= :today) or (p.endDate between :weekBeginning and :weekEnd) "
        		+ " ) "
        		+ "order by p.projectManager ";
        
        TypedQuery<Project> query= getEntityManager().createQuery(jql, Project.class);
      
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date weekBeginning = calendar.getTime();
        
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date weekEnd = calendar.getTime();
        
        query.setParameter("weekBeginning", weekBeginning);
        query.setParameter("weekEnd", weekEnd);
        query.setParameter("today", new Date());
        
        List<Project> res = query.getResultList();
        return res;
    }
    
    public void disableJustEndedProjects() {
    	String jqlUpdate = "update Project p set p.enabled = :disabled where p.endDate < :today and p.enabled = :enabled";
    	Query queryUpdate = getEntityManager().createQuery(jqlUpdate);
        queryUpdate.setParameter("today", new Date());
        queryUpdate.setParameter("enabled", true);
        queryUpdate.setParameter("disabled", false);
    }
    
    public List<Project> findJustEndedProjects() {
    	String jqlSelect = "select p from Project p inner join fetch p.projectManager pm where p.endDate < :today and p.enabled = :enabled ";
    	TypedQuery<Project> querySelect = getEntityManager().createQuery(jqlSelect, Project.class);
    	querySelect.setParameter("today", new Date());
    	querySelect.setParameter("enabled", true);
    	
    	List<Project> projects = querySelect.getResultList();
    	return projects;
    }
    
    public List<Project> findProjectsByConsultant(Integer consultantId) {
        String jql = "select p from Project p inner join p.consultants c where c.id = ?0 order by p.name";
        TypedQuery<Project> query= getEntityManager().createQuery(jql, Project.class);
        query.setParameter(0, consultantId);
        List<Project> res = query.getResultList();
        return res;
    }
    
    public List<Project> findProjectsToFill(Integer consultantId) {
        String jql = "select distinct p from Project p inner join p.consultants c left join fetch p.timecards tc where p.enabled=true and c.id = ?0 order by p.name";
        TypedQuery<Project> query= getEntityManager().createQuery(jql, Project.class);
        query.setParameter(0, consultantId);
        List<Project> res = query.getResultList();
        return res;
    }
    
    public boolean checkProjectCanFillMoreTimecards(Integer prjId) {
        String jql = "select distinct p.id from Project p left outer join p.timecards tc left outer join tc.timecardEntries tce where p.id=?0 group by p.id having p.endDate > max(tce.day)";
        TypedQuery<Integer> query= getEntityManager().createQuery(jql, Integer.class);
        query.setParameter(0, prjId);
        Integer res = 0;
        try {
            res = query.getSingleResult();
        } catch (NoResultException e) { 
            // nada a fazer.
        }
        return res > 0;
    }
    
    public Date lastFilledTimecard(Integer prjId) {
        String jql = "select max(tce.day)  from Project p left outer join p.timecards tc left outer join tc.timecardEntries tce where p.id=?0 group by p.id having p.endDate > max(tce.day)";
        TypedQuery<Date> query= getEntityManager().createQuery(jql, Date.class);
        query.setParameter(0, prjId);
        Date res = null;
        try {
            res = query.getSingleResult();
        } catch (NoResultException e) { 
            // nada a fazer.
        }
        return res;
    }
    
    
    
}
