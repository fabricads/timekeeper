package br.com.redhat.consulting.dao;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.model.filter.TimecardSearchFilter;

@RequestScoped
public class TimecardDao extends BaseDao<Timecard, TimecardSearchFilter> {

    protected void configQuery(StringBuilder query, TimecardSearchFilter filter, List<Object> params) {
        if (filter.getProject() != null && filter.getProject().getId() != null) {
            query.append(" and ENT.project.id = ? ");
            params.add(filter.getProject().getId());
        }
        
        if (filter.getConsultant() != null && filter.getConsultant().getId() != null) {
            query.append(" and ENT.consultant.id = ? ");
            params.add(filter.getConsultant().getId());
        }
        
        if (filter.getInitDate() != null && filter.getEndDate() != null) {
            query.append(" and ENT2.day between ? and ? ");
            params.add(filter.getInitDate());
            params.add(filter.getEndDate());
        }
        
        if (filter.getStatus() != null) {
            query.append(" and ENT.status = ? ");
            params.add(filter.getStatus());
        }
        query.append(getOrderBy());

    }
    
    public Long countByDate(Integer projectId, Integer consultantId, Date startDate, Date endDate) {
        String qryStr = "select count(tce.id) from Timecard tc inner join tc.timecardEntries tce "
                + "where tc.project.id = ?0 "
                + "  and tc.consultant.id = ?1 "
                + "  and tce.day between ?2 and ?3";
        TypedQuery<Long> qry = getEntityManager().createQuery(qryStr, Long.class);
        qry.setParameter(0, projectId);
        qry.setParameter(1, consultantId);
        qry.setParameter(2, startDate);
        qry.setParameter(3, endDate);
        Long res = qry.getSingleResult();
        return res; 
        
    }
    
}
