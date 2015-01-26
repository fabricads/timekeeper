package br.com.redhat.consulting.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.apache.commons.lang.StringUtils;

import br.com.redhat.consulting.model.Role;
import br.com.redhat.consulting.model.filter.RoleSearchFilter;

@RequestScoped
public class RoleDao extends BaseDao<Role, RoleSearchFilter> {

    protected void configQuery(StringBuilder query, RoleSearchFilter filter, List<Object> params) {
        if (StringUtils.isNotBlank(filter.getName())) {
            query.append(" and lower(ENT.name) like '%'||?||'%' ");
            params.add(filter.getName().toLowerCase());
        }

        query.append(" order by ENT.name");
        
    }
    
}
