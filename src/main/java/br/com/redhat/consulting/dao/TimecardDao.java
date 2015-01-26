package br.com.redhat.consulting.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.model.filter.TimecardSearchFilter;

@RequestScoped
public class TimecardDao extends BaseDao<Timecard, TimecardSearchFilter> {

    protected void configQuery(StringBuilder query, TimecardSearchFilter filter, List<Object> params) {
        if (filter.getProject() != null) {
            query.append(" and project = ? ");
            params.add(filter.getProject());
        }
        
        if (filter.getConsultant() != null) {
            query.append(" and consultant = ? ");
            params.add(filter.getConsultant());
        }
        
        if (filter.getStatus() != null) {
            query.append(" and status = ? ");
            params.add(filter.getStatus());
        }

    }
    
}
