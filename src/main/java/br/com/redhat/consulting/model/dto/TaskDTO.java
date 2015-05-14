package br.com.redhat.consulting.model.dto;

import br.com.redhat.consulting.model.Task;


public class TaskDTO  {

    private Integer id;
    private String name;
    private boolean dissociateOfProject;
    private Integer taskType;
    
    public TaskDTO() { }
    
    public TaskDTO(Task task) { 
        this.id = task.getId();
        this.name = task.getName();
        this.taskType = task.getTaskType();
    }
    
    public TaskDTO(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isDissociateOfProject() {
        return dissociateOfProject;
    }

    public void setDissociateOfProject(boolean dissociateOfProject) {
        this.dissociateOfProject = dissociateOfProject;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Task toTask() {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setTaskType(taskType);
        return task;
    }

    @Override
    public String toString() {
        return "Task [id=" + getId() + ", name=" + name + "]";
    }

}
