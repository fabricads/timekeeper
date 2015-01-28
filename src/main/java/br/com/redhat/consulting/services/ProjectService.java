package br.com.redhat.consulting.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.com.redhat.consulting.dao.ProjectDao;
import br.com.redhat.consulting.model.PartnerOrganization;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.filter.ProjectSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

public class ProjectService {
    
    @Inject
    private ProjectDao projectDao;
    
    public List<Project> findByPM(Integer pmId) throws GeneralException {
        ProjectSearchFilter filter = new ProjectSearchFilter();
        Person pm = new Person();
        pm.setId(pmId);
        filter.setProjectManager(pm);
//        projectDao.setFetchCollection(new String[]{"consultants"});
        List<Project> res = projectDao.find(filter);
        return res;
    }
    
    public Project findByName(String name) throws GeneralException {
        Project prj = null;
        ProjectSearchFilter filter = new ProjectSearchFilter();
        filter.setName(name);
        List<Project> res = projectDao.find(filter);
        if (res.size() > 0) 
            prj = res.get(0);
        return prj;
    }
    
    public List<Project> findAll() throws GeneralException {
        List<Project> res = Collections.emptyList();
        ProjectSearchFilter filter = new ProjectSearchFilter();
        res = projectDao.find(filter);
//        projectDao.setFetchCollection(new String[]{"consultants"});
        return res;
        
    }
    
    public Project findById(Integer pid) {
        Project project = projectDao.findById(pid);
        return project;
    }
    
    public Project findByIdWithConsultants(Integer pid) throws GeneralException {
        Project prj = null;
        ProjectSearchFilter filter = new ProjectSearchFilter();
        filter.setId(pid);
        projectDao.setFetchCollection(new String[]{"consultants"});
        List<Project> res = projectDao.find(filter);
        if (res.size() > 0) 
            prj = res.get(0);
        return prj;
    }
    
    public Integer countProjectsByPM(Integer pmId) throws GeneralException {
        Integer count = projectDao.countProjectsByPM(pmId);
        return count;
    }
    
    public void persist(Project project) throws GeneralException {
        Date today = new Date();
        for (Person p: project.getConsultants()) {
            p.getProjects().add(project);
        }
        if (project.getId() != null) {
            project.setLastModification(today);
            projectDao.update(project);
        } else {
            project.setRegistered(today);
            project.setLastModification(today);
            projectDao.insert(project);
        }
    }
    
    public void disable(Integer projectId) throws GeneralException {
        Project org = findById(projectId);
        org.setEnabled(false);
        projectDao.update(org);
    }
    
    public void enable(Integer projectId) throws GeneralException {
        Project org = findById(projectId);
        org.setEnabled(true);
        projectDao.update(org);
    }
    
    public void delete(Integer projectId) throws GeneralException {
        projectDao.remove(projectId);
    }


}
