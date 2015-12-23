package br.com.redhat.consulting.services;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.redhat.consulting.config.TransactionalMode;
import br.com.redhat.consulting.dao.ProjectDao;
import br.com.redhat.consulting.dao.TaskDao;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.Task;
import br.com.redhat.consulting.model.filter.ProjectSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
public class ProjectService {
    
    @Inject
    private ProjectDao projectDao;
    
    @Inject
    private TaskDao taskDao;
    
    public List<Project> findByPM(Integer pmId) throws GeneralException {
        ProjectSearchFilter filter = new ProjectSearchFilter();
        Person pm = new Person();
        pm.setId(pmId);
        filter.setProjectManager(pm);
        List<Project> res = projectDao.find(filter);
        return res;
    }
    
    public Project findByIdAndConsultant(Integer prjId, Integer consultantId) throws GeneralException {
        ProjectSearchFilter filter = new ProjectSearchFilter();
        filter.setId(prjId);
        Person consultant = new Person();
        consultant.setId(consultantId);
        filter.addConsultant(consultant);
        List<Project> res = projectDao.find(filter);
        Project prj = null;
        if (res.size() > 0) 
            prj = res.get(0);
        return prj;
    }
    
    public List<Project> findByConsultant(Integer consultantId) throws GeneralException {
        List<Project> res = projectDao.findProjectsByConsultant(consultantId);
        return res;
    }
    
    public List<Project> findByConsultantToFill(Integer consultantId) throws GeneralException {
        List<Project> res = projectDao.findProjectsToFill(consultantId);
        return res;
    }
    
    public boolean checkProjectCanFillMoreTimecards(Integer prjId) throws GeneralException {
        boolean res = projectDao.checkProjectCanFillMoreTimecards(prjId);
        return res;
    }
    
    public Date lastFilledTimecard(Integer prjId) throws GeneralException {
        Date res = projectDao.lastFilledTimecard(prjId);
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
    
    public List<Project> findAll(Boolean enabled) throws GeneralException {
        List<Project> res = null;
        ProjectSearchFilter filter = new ProjectSearchFilter();
        if (enabled != null) 
            filter.setEnabled(enabled);
        projectDao.setFetchCollection("consultants");
        res = projectDao.find(filter);
        return res;
        
    }
    
    
    public List<Project> findNoEntriesThisWeekProjects() {
    	List<Project> projects = projectDao.findNoEntriesThisWeekProjects();
    	return projects;
    }
    
    @TransactionalMode
    public List<Project> findAndDisableJustEndedProjects() {
    	List<Project> projects = projectDao.findJustEndedProjects();
    	projectDao.disableJustEndedProjects();
    	return projects;
    }
    
    public Project findById(Integer pid) {
        Project project = projectDao.findById(pid);
        return project;
    }
    
    public Project findByIdWithConsultants(Integer pid) throws GeneralException {
        Project prj = null;
        ProjectSearchFilter filter = new ProjectSearchFilter();
        filter.setId(pid);
        projectDao.setFetchCollection("consultants");
        List<Project> res = projectDao.find(filter);
        if (res.size() > 0) 
            prj = res.get(0);
        return prj;
    }

    public Project findByIdWithConsultantsAndTasks(Integer pid) throws GeneralException {
        Project prj = projectDao.findByIdWithConsultantsAndTasks(pid);
        return prj;
    }

    public Project findByIdWithTimecards(Integer pid) throws GeneralException {
        Project prj = null;
        ProjectSearchFilter filter = new ProjectSearchFilter();
        filter.setId(pid);
        List<Project> res = projectDao.find(filter);
        if (res.size() > 0) 
            prj = res.get(0);
        return prj;
    }
    
    public Long countProjectsByPM(Integer pmId) throws GeneralException {
        Long count = projectDao.countProjectsByPM(pmId);
        return count;
    }
    
    @TransactionalMode
    public void persist(Project project) throws GeneralException {
        Date today = new Date();
        if (project.getId() != null) {
            project.setLastModification(today);
            projectDao.update(project);
        } else {
            project.setRegistered(today);
            project.setLastModification(today);
            projectDao.insert(project);
        }
        for (Task task: project.getTasks()) {
            if (task.getId() == null)
                taskDao.insert(task);
            
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
    
    @TransactionalMode
    public void delete(Integer projectId) throws GeneralException {
        Project project = findById(projectId);
        for (Task task: project.getTasks()) {
            taskDao.remove(task);
        }
        projectDao.remove(project);
    }


}
