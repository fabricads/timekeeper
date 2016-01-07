package br.com.redhat.consulting.services;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.com.redhat.consulting.dao.PartnerOrganizationDao;
import br.com.redhat.consulting.model.PartnerOrganization;
import br.com.redhat.consulting.model.filter.PartnerOrganizationSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

public class PartnerOrganizationService {
    
    @Inject
    private PartnerOrganizationDao orgDao;
    
    public List<PartnerOrganization> findOrganizations(Boolean enabled) throws GeneralException {
        List<PartnerOrganization> res = null;
        PartnerOrganizationSearchFilter filter = new PartnerOrganizationSearchFilter();
        if (enabled != null)
            filter.setEnabled(enabled);
        filter.setJoinPersons(true);
        orgDao.setDistinct(true);
        res = orgDao.find(filter);
        return res;
    }
    
    public PartnerOrganization findByName(String name) throws GeneralException {
        List<PartnerOrganization> res = null;
        PartnerOrganizationSearchFilter filter = new PartnerOrganizationSearchFilter();
        filter.setName(name);
        res = orgDao.find(filter);
        PartnerOrganization org = null;
        if (res.size() > 0) 
            org = res.get(0);
        return org;
    }
    
    public void insert(PartnerOrganization org) throws GeneralException {
        org.setRegistered(new Date());
        orgDao.insert(org);
    }
    
    public void disable(Integer orgId) throws GeneralException {
        PartnerOrganization org = findById(orgId);
        org.setEnabled(false);
        orgDao.update(org);
    }
    
    public void enable(Integer orgId) throws GeneralException {
        PartnerOrganization org = findById(orgId);
        org.setEnabled(true);
        orgDao.update(org);
    }
    
    public void delete(Integer orgId) throws GeneralException {
        orgDao.remove(orgId);
    }
    
    public void persist(PartnerOrganization org) throws GeneralException {
        if (org.getId() != null) {
            orgDao.update(org);
        } else {
            org.setRegistered(new Date());
            orgDao.insert(org);
        }
    }
    
    public PartnerOrganization findById(Integer id) throws GeneralException {
        PartnerOrganizationSearchFilter filter = new PartnerOrganizationSearchFilter();
        filter.setId(id);
        filter.setJoinContacts(true);
        List<PartnerOrganization> res = orgDao.find(filter);
        PartnerOrganization org = null;
        if (res.size() > 0) {
            org = res.get(0);
        }
        return org;
    }
    
    
}
