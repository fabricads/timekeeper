package br.com.redhat.consulting.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.com.redhat.consulting.dao.ProjectDao;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.filter.ProjectSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

public class ProjectService {
    
    @Inject
    private ProjectDao projectDao;
    
    public List<Project> findByPM(String pmName) throws GeneralException {
        List<Project> res = Collections.emptyList();
        ProjectSearchFilter filter = new ProjectSearchFilter();
        Person pm = new Person();
        pm.setName(pmName);
        filter.setProjectManager(pm);
//        projectDao.setFetchCollection(new String[]{"consultants"});
        res = projectDao.find(filter);
        return res;
        
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
    
    public Integer countProjectsByPM(Integer pmId) throws GeneralException {
        Integer count = projectDao.countProjectsByPM(pmId);
        return count;
    }
    
    public void persist(Project project) throws GeneralException {
        Date today = new Date();
        if (project.getId() != null) {
            project.setLastModification(today);
            projectDao.update(project);
        } else {
            for (Person p: project.getConsultants()) {
                p.getProjects().add(project);
            }
            
            project.setRegistered(today);
            project.setLastModification(today);
            projectDao.insert(project);
        }
    }
    
    

}
