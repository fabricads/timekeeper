package br.com.redhat.consulting.services;

import java.util.List;

import javax.inject.Inject;

import br.com.redhat.consulting.dao.RoleDao;
import br.com.redhat.consulting.model.Role;
import br.com.redhat.consulting.model.filter.RoleSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

public class RoleService {
    
    @Inject
    private RoleDao roleDao;
    
    public List<Role> findAllRoles() throws GeneralException {
        List<Role> res = null;
        RoleSearchFilter filter = new RoleSearchFilter();
        res = roleDao.find(filter);
        return res;
    }
    
    public List<Role> findByName(String roleName) throws GeneralException {
        List<Role> res = null;
        RoleSearchFilter filter = new RoleSearchFilter();
        filter.setName(roleName);
        res = roleDao.find(filter);
        return res;
    }
    

}
