package br.com.redhat.consulting.services;

import java.util.List;

import javax.inject.Inject;

import br.com.redhat.consulting.dao.PersonTaskDao;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.PersonTask;
import br.com.redhat.consulting.model.Task;
import br.com.redhat.consulting.model.filter.PersonTaskSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

public class PersonTaskService {
    
    @Inject
    private PersonTaskDao personTaskDao;
    
    public List<PersonTask> findAssociation(Integer consultantId, Integer taskId) throws GeneralException {
        PersonTaskSearchFilter filter = new PersonTaskSearchFilter();
        filter.setConsultant(new Person());
        filter.getConsultant().setId(consultantId);
        filter.setTask(new Task());
        filter.getTask().setId(taskId);
        List<PersonTask> consultantsTasks = personTaskDao.find(filter);
        return consultantsTasks;
    }
    
    public List<PersonTask> findByProject(Integer projectId) {
        List<PersonTask> consultantsTasks = personTaskDao.findByProject(projectId);
        return consultantsTasks;
    }
    
}
