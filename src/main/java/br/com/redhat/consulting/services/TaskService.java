package br.com.redhat.consulting.services;

import java.util.List;

import javax.inject.Inject;

import br.com.redhat.consulting.dao.TaskDao;
import br.com.redhat.consulting.model.Task;
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
    
    public void delete(Integer taskId) throws GeneralException {
        taskDao.remove(taskId);
    }

    public void removeById(List<Integer> tasksToRemove) {
        taskDao.removeById(tasksToRemove);
        
    }
    

}
