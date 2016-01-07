package br.com.redhat.consulting.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.apache.commons.lang.StringUtils;

import br.com.redhat.consulting.model.PartnerOrganization;
import br.com.redhat.consulting.model.filter.PartnerOrganizationSearchFilter;

@RequestScoped
public class PartnerOrganizationDao extends BaseDao<PartnerOrganization, PartnerOrganizationSearchFilter> {

    private PartnerOrganizationSearchFilter filter;
    
    protected void configQuery(StringBuilder query, PartnerOrganizationSearchFilter filter, List<Object> params) {
        this.filter = filter;
        if (filter.getId() != null) {
            query.append(" and ENT.id = ? ");
            params.add(filter.getId());
        }
        if (filter.isEnabled() != null) {
            query.append(" and ENT.enabled = ? ");
            params.add(filter.isEnabled());
        }
        if (StringUtils.isNotBlank(filter.getName())) {
            query.append(" and upper(ENT.name) = ? ");
            params.add(filter.getName().toUpperCase());
        }
        
        if (StringUtils.isNotBlank(filter.getPartialName())) {
            query.append(" and upper(ENT.name) like '%'||?||'%' ");
            params.add(filter.getPartialName().toUpperCase());
        }
        query.append(" order by ENT.name");
    }
    
    protected void addJoinToFromClause(StringBuilder ql) {
        if (filter.isJoinPersons())
            ql.append("left join fetch ENT.persons");
        if (filter.isJoinContacts())
            ql.append("left join fetch ENT.contacts");
    }

    
}
