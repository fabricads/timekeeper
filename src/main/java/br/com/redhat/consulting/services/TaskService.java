package br.com.redhat.consulting.services;

import java.util.List;

import javax.inject.Inject;

import br.com.redhat.consulting.dao.TaskDao;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.Task;
import br.com.redhat.consulting.model.filter.TaskSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

public class TaskService {
    
    @Inject
    private TaskDao taskDao;
    
    public void save(Task task) throws GeneralException {
        if (task.getId() != null) {
            taskDao.update(task);
        } else {
            taskDao.insert(task);
        }
    }
    
    public Task findById(Integer taskId) {
        Task task = taskDao.findById(taskId);
        return task;
    }
    
    public List<Task> findByProject(Integer projectId) throws GeneralException {
        TaskSearchFilter filter = new TaskSearchFilter();
        Project prj = new Project();
        prj.setId(projectId);
        filter.setProject(prj);
        List<Task> res = taskDao.find(filter);
        return res;
    }
    
    public void delete(Integer taskId) throws GeneralException {
        taskDao.remove(taskId);
    }

    public void removeById(List<Integer> tasksToRemove) {
        taskDao.removeById(tasksToRemove);
        
    }
    

}
