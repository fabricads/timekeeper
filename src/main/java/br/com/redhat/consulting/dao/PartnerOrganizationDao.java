package br.com.redhat.consulting.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.apache.commons.lang.StringUtils;

import br.com.redhat.consulting.model.PartnerOrganization;
import br.com.redhat.consulting.model.filter.PartnerOrganizationSearchFilter;

@RequestScoped
public class PartnerOrganizationDao extends BaseDao<PartnerOrganization, PartnerOrganizationSearchFilter> {

    protected void configQuery(StringBuilder query, PartnerOrganizationSearchFilter filter, List<Object> params) {
        if (filter.isEnabled()) {
            query.append(" and ENT.enabled = ? ");
            params.add(filter.isEnabled());
        }
        
        if (StringUtils.isNotBlank(filter.getName())) {
            query.append(" and upper(ENT.name) like '%'||?||'%' ");
            params.add(filter.getName().toUpperCase());
        }
        query.append(" order by ENT.name");
    }
    
//    public String[] getFetchCollection() {
//        return new String[]{"persons"};
//    }
    
    
}
